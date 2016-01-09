package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;
import model.FiltreDeRecherche;
import model.FiltreParDate;
import model.FiltreParDate.FILTRE_PAR_DATE_DE;
import model.FiltreParDate.MOMENT_PAR_RAPPORT_A_DATE;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

/**
 * Classe qui gère la fenêtre secondaire des paramètres de filtre
 * @author François Lefebvre & Adrian Pinzaru
 *
 */
public class ControllerFiltreFenetre extends Observable implements Initializable {
	
	static private Map<String, MOMENT_PAR_RAPPORT_A_DATE> affichageMoment = 
			new HashMap<>();
	static private Map<String, FILTRE_PAR_DATE_DE> affichageTypeDate = 
			new HashMap<>();
	
	static {
	 	affichageMoment.put("avant le", MOMENT_PAR_RAPPORT_A_DATE.AVANT);
    	affichageMoment.put("après le", MOMENT_PAR_RAPPORT_A_DATE.APRES);
    	
    	affichageTypeDate.put("Mot modifié", FILTRE_PAR_DATE_DE.MODIFICATION);
    	affichageTypeDate.put("Mot saisi", FILTRE_PAR_DATE_DE.SAISIE);
	}	
	
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
    
    /**
     * Initialise le contrôleur.
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bindControls();
		afficherValeurs();
    	dateFiltrePicker.setEditable(false);
	}
	
	/**
	 * Crée les bindings.
	 */
    private void bindControls() {
    	typeFiltreDateComBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
    		filtreDateChBox.setSelected(true);
    	});
    	typeDateComBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
    		filtreDateChBox.setSelected(true);
    	});
    	dateFiltrePicker.valueProperty().addListener((obs, o, n) -> {
    		filtreDateChBox.setSelected(true);
    	});
    }
    
    /**
     * Crée les menus déroulant et définie autres paramètre d'affichage.
     */
    private void afficherValeurs() {
    	typeFiltreDateComBox.setItems(
    			FXCollections.observableArrayList(
    					affichageMoment.keySet()));
    	
    	typeDateComBox.setItems(
    			FXCollections.observableArrayList(
    					affichageTypeDate.keySet()));
    	
    	dateFiltrePicker.setEditable(false);
    }
	
    /**
     * Gestion du bouton annuler.
     * @param event
     */
    @FXML
    void annuler(ActionEvent event) {
    	this.appliquerBtn.getScene().getWindow().hide();
    }
    
    /**
     * Gère le bouton appliquer. Récupère le informations de l'interface
     * et alerte les observateur qu'en aux nouvelles valeurs du filtre.
     * @param event
     */
    @FXML
    void appliquer(ActionEvent event) {
    	FiltreParDate filtreDate = FiltreParDate.getDefault();
    	
    	if (filtreDateChBox.isSelected()) {
    		filtreDate = new FiltreParDate(
    			affichageMoment.get(typeFiltreDateComBox.getValue()),
    			affichageTypeDate.get(typeDateComBox.getValue()),
    			dateFiltrePicker.getValue()
    			);
    	}
    	this.setChanged();
    	this.notifyObservers(
    			new Object[]{
    					filtreDate,
    					filtreDateChBox.isSelected(),
    					imagePresenteChBox.isSelected()
    			});
    	this.appliquerBtn.getScene().getWindow().hide();
    }
    
    /**
     * Affiche les valeurs d'un filtre.
     * @param filtreReference
     */
    public void setValeurSelonFiltre(FiltreDeRecherche filtreReference) {
		this.setValeursDeDate(filtreReference.getFiltreParDate());
		this.filtreDateChBox.setSelected(filtreReference.filtreParDateEstActif());
		this.imagePresenteChBox.setSelected(filtreReference.getDoitContenirImage());
    }
    
    /**
     * Affiche les valeurs d'une filtre de date.
     * @param filtreDate
     */
	private void setValeursDeDate(FiltreParDate filtreDate) {
		typeFiltreDateComBox.getSelectionModel().select(
				getMomentString(
						filtreDate.getMoment()));
    	typeDateComBox.getSelectionModel().select(
    			getTypeFiltreString(
    					filtreDate.getTypeDate()));
    	dateFiltrePicker.setValue(filtreDate.getDate());
	}
	
	/**
	 * Revoie l'expression associée à la valeur du type de date.
	 * @param typeDate
	 * @return
	 */
	private String getTypeFiltreString(FILTRE_PAR_DATE_DE typeDate) {
		return affichageTypeDate.entrySet()
				.stream()
				.filter((e) -> {return typeDate == e.getValue();})
				.map((e)->{return e.getKey();})
				.findFirst()
				.get();
	}
	
	/**
	 * Renvoie l'expression associée à la valeur de type de filtre par rapport
	 * à a date (avant/après)
	 * @param moment Type de filtre sur date.
	 * @return
	 */
	private String getMomentString(MOMENT_PAR_RAPPORT_A_DATE moment) {
		return affichageMoment.entrySet()
			.stream()
			.filter((e) -> {return moment == e.getValue();})
			.map((e)->{return e.getKey();})
			.findFirst()
			.get();
	}
}
