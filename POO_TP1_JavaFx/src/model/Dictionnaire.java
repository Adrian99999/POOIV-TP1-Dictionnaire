package model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
	
	@SuppressWarnings("unchecked")
	public List<String> rechercher(FiltreDeRecherche parametresDeRecherche) {
		Stream<Mot> resultat;
		
		if (parametresDeRecherche.estNull()) 
		{
			resultat = this.values().stream();
		} 
		else if (parametresDeRecherche.rechercheLeMotExacte()) 
		{
			List<Mot> liste = new ArrayList<>();
			String motRecherche = parametresDeRecherche.getExpression();
			Mot motTrouve = this.get(motRecherche);
			if (motTrouve != null) {
				liste.add(motTrouve);
			}
			resultat = liste.stream().filter(parametresDeRecherche);
		} 
		else 
		{
			resultat = this.values()
						.stream()
						.filter(parametresDeRecherche);
		}
		return resultat.map(Mot::getMot).collect(Collectors.toList());
	}

	public void setMaxMotDef(String maxProperty) {
		maxMotsDef = Integer.parseInt(maxProperty);
	}

	public void ajouter(Mot mot) {
		this.put(mot.getMot().toLowerCase(), mot);
	}
}
