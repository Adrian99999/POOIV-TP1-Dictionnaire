package model;

import java.text.Collator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.SimpleStringProperty;


@SuppressWarnings("serial")
public class Dictionnaire extends TreeMap<String, Mot> {
	

	static final Comparator<String> IGNORE_CASE = new Comparator<String>(){
		@Override
		public int compare(String o1, String o2) {
			Collator fr_FRCollator = Collator.getInstance(new Locale("fr","FR"));
			return fr_FRCollator.compare(o1, o2);
		}
	};
	
	int maxMotsDef = 0;
	
	public Dictionnaire(String maxProperty) {
		super(IGNORE_CASE);
		maxMotsDef = Integer.parseInt(maxProperty);
	}
	
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
	
	@SuppressWarnings("unchecked")
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
		System.out.println(System.currentTimeMillis() - debut + " ms");
		return rechercher(dictionnaireReduit, parametresDeRecherche);
	}

	public Mot ajouter(Mot mot) {
//		lancerExceptionSiMotNonValide(mot);
		this.put(mot.getMot(), mot);
		mot.setDateSaisieMot(LocalDate.now());
		return mot;
	}
	
	public Mot update(Mot motReference) {
		lancerExceptionSiMotNonValide(motReference);
		String libelle = motReference.getMot();
		Mot motAMettreAJour = this.get(libelle);
		Mot motMisAJour;
		if (this.containsKey(libelle)) {
			if (!motAMettreAJour.equals(motReference)) {
				motMisAJour = motAMettreAJour.updateInfoAPartirDe(motAMettreAJour);
			} else {
				return null;
			}
		} else {
			motMisAJour = this.ajouter(motAMettreAJour);
		}
		return motMisAJour;
	}
	
	public <T> Map<String, T> subMapWithKeysThatAreSuffixes(String prefix, NavigableMap<String, T> map) {
	    if ("".equals(prefix)) return map;
	    String lastKey = createLexicographicallyNextStringOfTheSameLenght(prefix);
	    return map.subMap(prefix, true, lastKey, false);
	}

	String createLexicographicallyNextStringOfTheSameLenght(String input) {
	    final int lastCharPosition = input.length()-1;
	    String inputWithoutLastChar = input.substring(0, lastCharPosition);
	    char lastChar = input.charAt(lastCharPosition) ;
	    char incrementedLastChar = (char) (lastChar + 1);
	    return inputWithoutLastChar+incrementedLastChar;
	}
	
	public class DefinitionTropLongueException extends RuntimeException {
		DefinitionTropLongueException() {
			super();
		}
	}
	
	private void lancerExceptionSiMotNonValide(Mot mot) {
		if (mot.getNombreMotsDansDefinition() > maxMotsDef) {
			throw new DefinitionTropLongueException();
		}
	}

	public int getMaxMotDef() {
		return maxMotsDef;
	}
}
