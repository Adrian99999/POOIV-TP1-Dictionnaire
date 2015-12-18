package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    void ajouterMot(ActionEvent event) {

    }

    @FXML
    void effacerMot(ActionEvent event) {

    }

    @FXML
    void modifierMot(ActionEvent event) {

    }
    private ObservableList<String> observableList = FXCollections.observableArrayList();
    private FabriqueMotSingleton singleton = FabriqueMotSingleton.getInstance();
    
    private void afficcherListViewDictionaire()
    {
//		for(Map.Entry<String,Mot> entree : singleton.getDictionaire().entrySet()){
//			entree.getValue();
//			observableList.add(entree.getKey());
//			entree.getKey();
//		}
		
		
		for(String mot : singleton.getTreeSetDictionaire())
		{
			observableList.add(mot);
		}
		
		listViewMots.setItems(observableList);
		
    }
    
    private void afficherInfoMot(Mot mot)
    {
    	
    	if(mot != null)
    	{
    		textFieldDateModificationMot.setText(mot.getDateModificationMot().toString());
    		textFieldDateSaisieMot.setText(mot.getDateSaisieMot().toString());
    		textFieldFichierMot.setText(mot.getNomFichier());
    		textAreaDifinition.setText(mot.getDefinition());
    	}
    	else
    	{
    		textFieldDateModificationMot.setText("");
    		textFieldDateSaisieMot.setText("");
    		textFieldFichierMot.setText("");
    		textAreaDifinition.setText("");
    	}
    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		afficcherListViewDictionaire();
		//ajouter le listener a la listView 
		listViewMots.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.out.println(newValue);
				textFieldAffichageMot.setText(newValue);
				afficherInfoMot(singleton.getDictionaire().get(newValue));
				
			}
		});
	}

}

