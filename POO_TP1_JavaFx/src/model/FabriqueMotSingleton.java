package model;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.text.Collator;
import java.text.Normalizer;
/**
 * Classe qui fabrique les mots et le diactionaire
 * @author Adrian Pinzaru
 *
 */
public class FabriqueMotSingleton 
{
	private static FabriqueMotSingleton instance= null;
	private Map<String, Mot> dictionaire = new HashMap<>();
	private TreeSet<String> treeSetDictionaire = new TreeSet<String>(IGNORE_CASE);
	
	private Properties properties = new Properties();
	/**
	 * Constructeur de la classe, execut la lecture du fichier proprietes.xml et cree le dictionaire;
	 */
	protected FabriqueMotSingleton()
	{
		lireProprietes();
		creerDictionaire();
	}
	/**
	 * Méthode qui retourne l'instance de la classe
	 * @return instance de la classe FabriqueMotSingleton
	 */
	public static FabriqueMotSingleton getInstance()
	{
		if(instance == null)
		{
			instance = new FabriqueMotSingleton();
		}
		return instance;
	}
	/**
	 * Méthode qui crée le dictionaire, remplit la Map
	 */
	private void creerDictionaire()
	{
		GestionFichier fichier = new GestionFichier();
		for (int i=0; i<fichier.getListeMots().size(); i++)
		{
			//String dateCreationMot = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss").format(Calendar.getInstance().getTime());

			Mot mot = new Mot(fichier.getListeMots().get(i),"définition", "nom fichier", LocalDate.now(), LocalDate.now() );
			
			dictionaire.put(fichier.getListeMots().get(i), mot);
			treeSetDictionaire.add(fichier.getListeMots().get(i));
		}
	}
	/**
	 * Méthode qui lit le fichier de configuration
	 */
	private void lireProprietes()
	{
		InputStream in = this.getClass().getResourceAsStream("properties.xml");
		try {
			
			properties.loadFromXML(in);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				
				in.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Méthode qui retourne la liste des mots dans un TreeSet
	 * @return TreeSet
	 */
	public TreeSet<String> getTreeSetDictionaire() {
		return treeSetDictionaire;
	}
	/**
	 * 
	 * @return Map
	 */
	public Map<String, Mot> getDictionaire() {
		return dictionaire;
	}
	
	static final Comparator<String> IGNORE_CASE = new Comparator<String>(){

		@Override
		public int compare(String o1, String o2) {
			// TODO Auto-generated method stub
			//String test1 = Normalizer.normalize(o1, Normalizer.Form.NFD);
			//String test2 = Normalizer.normalize(o2, Normalizer.Form.NFD);
			Collator fr_FRCollator = Collator.getInstance(new Locale("fr","FR"));
			return fr_FRCollator.compare(o1, o2);
		}
		
	};
}