package controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Classe pour lancer l'application Dictionnaire.
 * </br></br>
 * Cette application permet de consulter et modifier un dictionnaire.
 *  </br></br>
 * Note: Pour le drag and drop d'information entre les mots, il faut le faire
 * entre l'image ou la définition d'un mot affiché et un autre mot dans la
 * liste des mots.
 * @author François Lefebvre & Adrian Pinzaru
 *
 */
public class Main extends Application {
	
	/** 
	 * Lance l'application.
	 */
	@Override
	public void start(Stage primaryStage) {
			
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("../vue/Dictionaire_view.fxml"));
			Pane root = (Pane) loader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Dictionnaire");
			primaryStage.getIcons().add(new Image("/vue/dictionary.png"));
			primaryStage.show();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Void Main
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
