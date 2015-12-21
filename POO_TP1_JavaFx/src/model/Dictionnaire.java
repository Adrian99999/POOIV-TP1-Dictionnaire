package model;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Dictionnaire extends TreeMap<String, Mot> {
	
	@SuppressWarnings("unchecked")
	public List<Mot> rechercher(FiltreDeRecherche parametresDeRecherche) {
		
		List<Object> liste = this.values()
								.stream()
								.filter(parametresDeRecherche)
								.collect(Collectors.toList());
		return (List<Mot>) (Object) liste;
		
	}
}
