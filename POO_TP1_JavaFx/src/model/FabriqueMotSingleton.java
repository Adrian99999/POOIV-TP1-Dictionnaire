package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	private Dictionnaire dictionnaire;
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
	 * Méthode qui lit le fichier de configuration
	 */
	private void lireProprietes()
	{
		InputStream in = this.getClass().getResourceAsStream("properties.xml");
		
		try {
			properties.loadFromXML(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Méthode qui crée le dictionaire, remplit la Map
	 */
	private void creerDictionaire()
	{
		dictionnaire = new Dictionnaire();
		dictionnaire.setMaxMotDef(properties.getProperty("max.mots"));
		
		
//		new Thread(() -> {
			BufferedReader br = null;
			
			
			try {
				String line = null;
	
				br  = new BufferedReader(
						new InputStreamReader(
								this.getClass()
								.getResourceAsStream("liste_de_mots.txt")));
				
				while((line = br.readLine()) != null)
				{
//					System.out.println(line);
					dictionnaire.put(line, new Mot(line));
				}
				
				br.close();
			
			} catch ( IOException e) {
				e.printStackTrace();
			}
//		}).start();;
	}


	public Dictionnaire getDictionnaire() {
		return dictionnaire;
	}

}