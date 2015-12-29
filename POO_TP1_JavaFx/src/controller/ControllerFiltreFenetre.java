package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import model.FiltreDeRecherche.FILTRE_PAR_DATE_DE;
import model.FiltreDeRecherche.MOMENT_PAR_RAPPORT_A_DATE;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class ControllerFiltreFenetre implements Initializable {

    @FXML
    private CheckBox filtreDateChBox;

    @FXML
    private ComboBox<String> typeFiltreDateComBox;

    @FXML
    private ComboBox<String> typeDateComBox;

    @FXML
    private DatePicker dateFiltrePicker;

    @FXML
    private CheckBox imagePresenteChBox;
    
    @FXML
    private Button annulerBtn;

    @FXML
    private Button appliquerBtn;
    
    @FXML
    void annuler(ActionEvent event) {
    	((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    void appliquer(ActionEvent event) {

    }

    @FXML
    void gererFiltreDateChBox(ActionEvent event) {

    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		bindControls();
		
		populateComboBoxes();
		
		}
    
    private void bindControls() {
    	typeFiltreDateComBox.disableProperty().bind(filtreDateChBox.selectedProperty().not());
		typeDateComBox.disableProperty().bind(filtreDateChBox.selectedProperty().not());
		dateFiltrePicker.disableProperty().bind(filtreDateChBox.selectedProperty().not());
		appliquerBtn.disableProperty()
			.bind(
					filtreDateChBox.selectedProperty().not()
					.and(
					imagePresenteChBox.selectedProperty().not()
					)
			);

    }
    
    private void populateComboBoxes() {
    	Map<String, MOMENT_PAR_RAPPORT_A_DATE> affichageMoment = 
    			new HashMap<>();
    	affichageMoment.put("avant le", MOMENT_PAR_RAPPORT_A_DATE.AVANT);
    	affichageMoment.put("après le", MOMENT_PAR_RAPPORT_A_DATE.APRES);
    	
    	Map<String, FILTRE_PAR_DATE_DE> affichageTypeDate = 
    			new HashMap<>();
    	affichageTypeDate.put("Mot modifié", FILTRE_PAR_DATE_DE.MODIFICATION);
    	affichageTypeDate.put("Mot saisi", FILTRE_PAR_DATE_DE.SAISIE);
    	
    	typeFiltreDateComBox.setItems(
    			FXCollections.observableArrayList(
    					affichageMoment.keySet()));
    	
    	typeDateComBox.setItems(
    			FXCollections.observableArrayList(
    					affichageTypeDate.keySet()));
    	
    	typeFiltreDateComBox.getSelectionModel().select("avant le");
    	typeDateComBox.getSelectionModel().select("Mot modifié");
    	
    	dateFiltrePicker.setValue(LocalDate.now());
    	dateFiltrePicker.setEditable(false);
    	
    }

}
