package controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	private Stage primaryStage;
	private BorderPane root;
	
	@Override
	public void start(Stage primaryStage) {
		
			this.primaryStage = primaryStage;
			initRoot();
			initDictionaire();
	}
	public void initRoot()
	{
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("../vue/Root_view.fxml"));
			root = (BorderPane) loader.load();
			//root = FXMLLoader.load(getClass().getResource("../Root_view.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Dictionaire");
		primaryStage.show();
	}
	public void initDictionaire()
	{
		try {
			AnchorPane rootDictionar = FXMLLoader.load(getClass().getResource("../vue/Dictionaire_view.fxml"));
			root.setCenter(rootDictionar);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}
