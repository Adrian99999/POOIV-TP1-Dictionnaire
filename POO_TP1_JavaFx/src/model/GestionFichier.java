package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GestionFichier implements Runnable {
	/**
	 * La liste des mots du fichier
	 */
	private BlockingQueue<String> listeMots = new LinkedBlockingQueue<String>(200);
	
	public GestionFichier() {

	}
	
	public BlockingQueue<String> getListeMots()
	{
		return listeMots;
	}

	@Override
	public void run() {
		
		BufferedReader br = null;
		
		try {
			br  = new BufferedReader(
					new InputStreamReader(
							this.getClass()
							.getResourceAsStream("liste_de_mots.txt")));
			
			String line = null;
			while((line = br.readLine()) != null)
			{
				listeMots.put(line);
				System.out.println(line + " lecture");

			}
			listeMots.put("_fin_");
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

		
	
}
