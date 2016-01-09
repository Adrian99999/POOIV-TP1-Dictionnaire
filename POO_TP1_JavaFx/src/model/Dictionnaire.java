package model;

import java.text.Collator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe qui gère les données du dictionnaire.
 * @author François Lefebvre & Adrian Pinzaru
 *
 */
public class Dictionnaire extends TreeMap<String, Mot> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Comparateur qui permet de gérer mots sans égard à la case.
	 */
	static final Comparator<String> IGNORE_CASE = new Comparator<String>(){
		@Override
		public int compare(String o1, String o2) {
			Collator fr_FRCollator = Collator.getInstance(new Locale("fr","FR"));
			return fr_FRCollator.compare(o1, o2);
		}
	};
	
	/**
	 * Nombre de mots maximum accepté dans la définition des mots.
	 */
	int maxMotsDef = 0;
	
	/**
	 * Constructeur du dictionnaire
	 * @param maxProperty Nombre de mots maximum pour les définitions.
	 */
	public Dictionnaire(String maxProperty) {
		super(IGNORE_CASE);
		maxMotsDef = Integer.parseInt(maxProperty);
	}
	
	/**
	 * S'assure de passer par la méthode ajouter.
	 */
	@Override
	public Mot put(String libelle, Mot mot) {
		return this.ajouter(mot);
	}
	
	/**
	 * Ajoute un mot au dictionnaire.
	 * @param mot Mot à ajotuer.
	 * @return mot qui a été ajouté.
	 */
	public Mot ajouter(Mot mot) {
		super.put(mot.getMot(), mot);
		mot.setDateSaisieMot(LocalDate.now());
		return mot;
	}
	
	/**
	 * Permet de faire la mise à jour du dictionnaire que ce soit par l'ajout 
	 * d'un nouveau mot ou par la modification d'un mot existant.
	 * @param motReference Mot contenant les nouvelles informations (sans les date)
	 * du mot à modifier ou bien mot à ajouter.
	 * @return Mot modifié ou ajouté.
	 */
	public Mot update(Mot motReference) {
		lancerRuntimeExceptionSiMotNonValide(motReference);
		String libelle = motReference.getMot();
		Mot motAMettreAJour = this.get(libelle);
		Mot motMisAJour;
		if (this.containsKey(libelle)) {
			if (!motAMettreAJour.equals(motReference)) {
				motMisAJour = motAMettreAJour.updateInfoAPartirDe(motReference);
			} else {
				return null;
			}
		} else {
			motMisAJour = this.ajouter(motAMettreAJour);
		}
		return motMisAJour;
	}
	
	/**
	 * Lance une recherche dans le dictionnaire à partir de paramètres de recherche.
	 * La méthode recherche d'abord un sous arbre compatible avec le filtre.
	 * Elle passe ensuite les résultats par le filtre.
	 * @param parametresDeRecherche
	 * @return Liste de mots trouvés.
	 */
	public List<String> rechercher(FiltreDeRecherche parametresDeRecherche) {
		long debut = System.currentTimeMillis();
		Map<String, Mot> dictionnaireReduit;
		Matcher prefixMatcher = Pattern.compile("\\^(\\w+)").matcher(parametresDeRecherche.getRegex());
		
		if (prefixMatcher.matches()) {
			String prefixAlphabetique = prefixMatcher.group(1);
			dictionnaireReduit = subMapWithKeysThatAreSuffixes(prefixAlphabetique, this);
		} else {
			dictionnaireReduit = this;
		}
		System.out.println("Résultat trouvé en " + (System.currentTimeMillis() - debut) + " ms");
		return rechercher(dictionnaireReduit, parametresDeRecherche);
	}

	
	/**
	 * Méthode qui effectue une recherche dans un dictionnaire grâce aux 
	 * filtres de recherche.
	 * @param dic Dictionnaire dans lequel chercher.
	 * @param parametresDeRecherche
	 * @return Liste de mots trouvés.
	 */
	private static List<String> rechercher(Map<String, Mot> dic, FiltreDeRecherche parametresDeRecherche) {
		Stream<Mot> resultat;
		
		if (parametresDeRecherche.estNull()) 
		{
			resultat = dic.values().stream();
		} 
		else if (parametresDeRecherche.rechercheLeMotExacte()) 
		{
			List<Mot> liste = new ArrayList<>();
			String motRecherche = parametresDeRecherche.getExpression();
			Mot motTrouve = dic.get(motRecherche);
			if (motTrouve != null) {
				liste.add(motTrouve);
			}
			resultat = liste.stream().filter(parametresDeRecherche);
		} 
		else 
		{
			resultat = dic.values()
						.stream()
						.filter(parametresDeRecherche);
		}
		return resultat.map(Mot::getMot).collect(Collectors.toList());
	}
	
	/**
	 * Retourne un sous arbre dont tous les clés commencent par un préfixe.
	 * Méthode écrite par piotrek.
	 * <a href="http://stackoverflow.com/questions/10711494/get-values-in-treemap-whose-string-keys-start-with-a-pattern">Source</a>
	 * @param prefix
	 * @param map
	 * @return
	 */
	public <T> Map<String, T> subMapWithKeysThatAreSuffixes(String prefix, NavigableMap<String, T> map) {
	    if ("".equals(prefix)) return map;
	    String lastKey = createLexicographicallyNextStringOfTheSameLenght(prefix);
	    return map.subMap(prefix, true, lastKey, false);
	}
	
	/**
	 * Renvoi une expression dont la valeur suie immédiatement celle de 
	 * l'expresion fournie en paramètre.
	 * Méthode écrite par piotrek.
	 * <a href="http://stackoverflow.com/questions/10711494/get-values-in-treemap-whose-string-keys-start-with-a-pattern">Source</a>
	 * @param input Expression de départ.
	 * @return Expression de valeur immédiatement supérieure.
	 */
	String createLexicographicallyNextStringOfTheSameLenght(String input) {
	    final int lastCharPosition = input.length()-1;
	    String inputWithoutLastChar = input.substring(0, lastCharPosition);
	    char lastChar = input.charAt(lastCharPosition) ;
	    char incrementedLastChar = (char) (lastChar + 1);
	    return inputWithoutLastChar+incrementedLastChar;
	}
	
	/**
	 * Vérifie la validité d'une mot et lance une RuntimeException (unchecked)
	 * si le mot est invalide.
	 * @param mot Mot à vérifier.
	 */
	private void lancerRuntimeExceptionSiMotNonValide(Mot mot) {
		if (mot.getNombreMotsDansDefinition() > maxMotsDef) {
			throw new DefinitionTropLongueException();
		}
	}
	
	/**
	 * Retourne la valeur du nombre de mot maximal que la définition peut
	 * comporter.
	 * @return Nombre maximal.
	 */
	public int getMaxMotDef() {
		return maxMotsDef;
	}
	
	/**
	 * RuntimeException liée à une définition trop longue.
	 */
	public class DefinitionTropLongueException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		DefinitionTropLongueException() {
			super();
		}
	}

}
