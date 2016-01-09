package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
/**
 * Classe qui fabrique les mots et le dictionnaire.
 * @author François Lefebvre & Adrian Pinzaru
 *
 */
public class FabriqueMotSingleton 
{
	private static FabriqueMotSingleton instance= null;
	private Dictionnaire dictionnaire;
	private Properties properties = new Properties();
	
	/**
	 * Constructeur de la classe, exécute la lecture du fichier proprietes.xml et crée le dictionnaire.
	 */
	protected FabriqueMotSingleton()
	{
		lireProprietes();
		creerDictionaire();
	}
	
	/**
	 * Retourne le singleton de la classe.
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
	 * Lit le fichier de configuration
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
	 * Crée le dictionnaire
	 */
	private void creerDictionaire()
	{
		dictionnaire = new Dictionnaire(properties.getProperty("max.mots"));
			BufferedReader br = null;
			
			try {
				String line = null;
	
				br  = new BufferedReader(
						new InputStreamReader(
								this.getClass()
								.getResourceAsStream("liste_de_mots.txt")));
				
				while((line = br.readLine()) != null)
				{
					dictionnaire.ajouter(new Mot(line));
				}
				
				br.close();
			
			} catch ( IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Renvoi le dictionnaire.
	 * @return
	 */
	public Dictionnaire getDictionnaire() {
		return dictionnaire;
	}

}
