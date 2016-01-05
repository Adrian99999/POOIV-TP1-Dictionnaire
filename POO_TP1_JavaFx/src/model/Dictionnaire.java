package model;

import java.text.Collator;
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
	
	public Dictionnaire() {
		super(IGNORE_CASE);
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
		Map<String, Mot> dictionnaireReduit;
		Matcher prefixMatcher = Pattern.compile("\\^(\\w+)").matcher(parametresDeRecherche.getRegex());
		
		if (prefixMatcher.matches()) {
			String prefixAlphabetique = prefixMatcher.group(1);
			dictionnaireReduit = subMapWithKeysThatAreSuffixes(prefixAlphabetique, this);
		} else {
			dictionnaireReduit = this;
		}
		return rechercher(dictionnaireReduit, parametresDeRecherche);
	}

	public void setMaxMotDef(String maxProperty) {
		maxMotsDef = Integer.parseInt(maxProperty);
	}

	public void ajouter(Mot mot) {
		this.put(mot.getMot().toLowerCase(), mot);
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
}
