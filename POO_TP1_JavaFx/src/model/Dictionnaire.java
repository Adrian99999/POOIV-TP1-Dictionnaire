package model;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.scene.Parent;

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
	public List<Mot> rechercher(FiltreDeRecherche parametresDeRecherche) {
		
		List<Object> liste = this.values()
								.stream()
								.filter(parametresDeRecherche)
								.collect(Collectors.toList());
		return (List<Mot>) (Object) liste;
	}

	public void setMaxMotDef(String maxProperty) {
		maxMotsDef = Integer.parseInt(maxProperty);
	}
	
	

	

		
}
