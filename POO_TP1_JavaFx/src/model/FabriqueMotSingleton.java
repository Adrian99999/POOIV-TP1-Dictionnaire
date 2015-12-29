package model;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;
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
	 * Méthode qui crée le dictionaire, remplit la Map
	 */
	private void creerDictionaire()
	{
		dictionnaire = new Dictionnaire();
		GestionFichier fichier = new GestionFichier();
		for (int i=0; i<fichier.getListeMots().size(); i++)
		{
			//String dateCreationMot = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss").format(Calendar.getInstance().getTime());

			Mot mot = new Mot(fichier.getListeMots().get(i),"définition", "nom fichier", LocalDate.now(), LocalDate.now() );
			
			dictionnaire.put(fichier.getListeMots().get(i), mot);
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

	public Dictionnaire getDictionnaire() {
		return dictionnaire;
	}

}