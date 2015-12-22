package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Dictionnaire;
import model.FabriqueMotSingleton;
import model.Mot;

public class ControllerDictionaire implements Initializable{
	@FXML
	private ListView<String> listViewMots;
	 
    @FXML
    private TextField textFieldFichierMot;

    @FXML
    private TextField textFieldDateModificationMot;

    @FXML
    private TextArea textAreaDifinition;

    @FXML
    private TextField textFieldDateSaisieMot;

    @FXML
    private TextField textFieldAffichageMot;
    
    @FXML
    private Button buttonAjouter;

    @FXML
    private Button buttonEffacer;

    @FXML
    private Button buttonModifier;
    
    @FXML
    private TextField champRecherche;

    @FXML
    private CheckBox dansLeMotChBox;
    
    @FXML
    private CheckBox filtreChBox;

    @FXML
    private Button buttonAnnuler;

    @FXML
    void ajouterMot(ActionEvent event) {

    }

    @FXML
    void effacerMot(ActionEvent event) {

    }

    @FXML
    void modifierMot(ActionEvent event) {

    }

    @FXML
    void annulerModification(ActionEvent event) {

    }

    @FXML
    void gererCliqueDefinition(ActionEvent event) {

    }

    @FXML
    void gererCliqueSurMot(ActionEvent event) {

    }

    @FXML
    void gererExpressionDansMotChBox(ActionEvent event) {

    }
    
    @FXML
    void fermerAppication(ActionEvent event) {
    	
    }

    @FXML
    void gererFiltreChBox(ActionEvent event) {
    	if (filtreChBox.isSelected()) {
    		try {
				Pane root = FXMLLoader.load(
						ControllerDictionaire.class.getResource(
								"../vue/FiltreFenetre.fxml"
								)
						);
				Stage filtreStage = new Stage();
	    		filtreStage.setTitle("Filtre");
	    		filtreStage.setScene(new Scene(root));
	    		filtreStage.show();
    		} catch (IOException e) {
				e.printStackTrace();
			}
    	} else {
    		desactiverLeFiltre();
    	}
    }

    @FXML
    void rechercherAChaqueLettre(ActionEvent event) {

    }

    @FXML
    void rechercherLeMotComplet(ActionEvent event) {

    }

    @FXML
    void supprimerMot(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Suppression mot");
    	alert.setHeaderText("Suppression du mot : " + listViewMots.getSelectionModel().getSelectedItem());
    	alert.setContentText("Etes vous sure de vouloir supprimer ce mot?");
    	Optional<ButtonType> result = alert.showAndWait();
    	if(result.get() == ButtonType.OK)
    	{
    		listeDesMotsAffiches.remove(listViewMots.getSelectionModel().getSelectedIndex());
    		dictionnaire.remove(listViewMots.getSelectionModel().getSelectedItem().toString());
    	}
    	else
    	{
    		
    	}
    }
    
    private ObservableList<String> listeDesMotsAffiches = FXCollections.observableArrayList();
    private Dictionnaire dictionnaire;
    
    private void afficherInfoMot(Mot mot)
    {
    	
    	if(mot != null)
    	{
    		textFieldDateModificationMot.setText(mot.getDateModificationMot().toString());
    		textFieldDateSaisieMot.setText(mot.getDateSaisieMot().toString());
    		//textFieldFichierMot.setText(mot.getNomFichier());
    		textAreaDifinition.setText(mot.getDefinition());
    		//pour ne pas avoir d'erreurs 
    		Platform.runLater(new Runnable() {
    		    @Override
    		    public void run() {
    		    	textAreaDifinition.requestFocus();
    		    }
    		});
    		
    	}
    	else
    	{
    		
    		textFieldDateModificationMot.setText("");
    		textFieldDateSaisieMot.setText("");
    		//textFieldFichierMot.setText("");
    		textAreaDifinition.setText("");
    	}
    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    
		lireLeDictionaireEtLAfficher();
		
		lierListeDesMotsEtAffichage();

	}
	
	private void lireLeDictionaireEtLAfficher() {
		FabriqueMotSingleton fabriqueDic = FabriqueMotSingleton.getInstance();
	    dictionnaire = fabriqueDic.getDictionnaire();
	    listeDesMotsAffiches.addAll(dictionnaire.keySet());
	    listViewMots.setItems(listeDesMotsAffiches);
	}
	
	private void lierListeDesMotsEtAffichage() {
		listViewMots.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.out.println(newValue);
				textFieldAffichageMot.setText(newValue);
				
				afficherInfoMot(dictionnaire.get(newValue));
			}
		});
	}
	
	private void desactiverLeFiltre() {
		
	}

}

