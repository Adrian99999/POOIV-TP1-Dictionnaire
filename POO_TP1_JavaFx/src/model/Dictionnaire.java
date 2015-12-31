package model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.stream.Collectors;


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
		List<String> resultat;
		
		if (parametresDeRecherche.estNull()) {
			resultat = new ArrayList<String>(this.keySet());
		} else if (!parametresDeRecherche.rechercheDansLeContenu() && 
				!parametresDeRecherche.getExpression().isEmpty()) {
			resultat = new ArrayList<String>();
			Mot mot = this.get(parametresDeRecherche.getExpression());
			if (mot != null && parametresDeRecherche.test(mot)) {
				resultat.add(mot.getMot());
			}
		} else {
			List<Object> liste = this.values()
									.stream()
									.filter(parametresDeRecherche)
									.map(Mot::getMot)
									.collect(Collectors.toList());
			resultat = (List<String>) (Object) liste;
		}
		return resultat;
	}

	public void setMaxMotDef(String maxProperty) {
		maxMotsDef = Integer.parseInt(maxProperty);
	}
	
	

	

		
}
