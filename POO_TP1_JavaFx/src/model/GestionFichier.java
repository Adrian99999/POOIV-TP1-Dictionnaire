package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GestionFichier {
	/**
	 * La liste des mots du fichier
	 */
	private List<String> listeMots = new ArrayList<>();
	//public static void main(String [] args){
	public GestionFichier() {
		BufferedReader br = null;
		try {
			br  = new BufferedReader(
					new InputStreamReader(
							this.getClass()
							.getResourceAsStream("liste_de_mots.txt")));
			String line = null;
			
			while((line = br.readLine()) != null)
			{
				System.out.println(line);
		       listeMots.add(line);
			}
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public List<String> getListeMots()
	{
		return listeMots;
	}
}
