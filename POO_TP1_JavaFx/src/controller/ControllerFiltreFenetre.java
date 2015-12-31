package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import model.FiltreDeRecherche;
import model.FiltreParDate;
import model.FiltreParDate.FILTRE_PAR_DATE_DE;
import model.FiltreParDate.MOMENT_PAR_RAPPORT_A_DATE;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

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
	
	private FiltreDeRecherche filtre = FiltreDeRecherche.getNull();
	
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
    
    private void fermerFenetre(Event event) {
    	((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    @FXML
    void annuler(ActionEvent event) {
//    	this.setChanged();
//    	this.notifyObservers(null);
    	this.fermerFenetre(event);
    }

    @FXML
    void appliquer(ActionEvent event) {
    	FiltreParDate filtreDate = null;
    	
    	if (filtreDateChBox.isSelected()) {
    		filtreDate = new FiltreParDate(
    			affichageMoment.get(typeFiltreDateComBox.getValue()),
    			affichageTypeDate.get(typeDateComBox.getValue()),
    			dateFiltrePicker.getValue()
    			);
    	}
    	List<Object> args = new ArrayList<Object>();
    	args.add(filtreDate);
    	args.add(new Boolean(imagePresenteChBox.isSelected()));
    	this.setChanged();
    	this.notifyObservers(args);
    	this.fermerFenetre(event);
    }

    @FXML
    void gererFiltreDateChBox(ActionEvent event) {

    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		bindControls();
		
		afficherValeurs();
		
    	dateFiltrePicker.setEditable(false);

		
		}
    
    private void bindControls() {
    	typeFiltreDateComBox.disableProperty().bind(filtreDateChBox.selectedProperty().not());
		typeDateComBox.disableProperty().bind(filtreDateChBox.selectedProperty().not());
		dateFiltrePicker.disableProperty().bind(filtreDateChBox.selectedProperty().not());
//		appliquerBtn.disableProperty()
//			.bind(
//					filtreDateChBox.selectedProperty().not()
//					.and(
//					imagePresenteChBox.selectedProperty().not()
//					)
//			);

    }
    
    private void afficherValeurs() {
    	typeFiltreDateComBox.setItems(
    			FXCollections.observableArrayList(
    					affichageMoment.keySet()));
    	
    	typeDateComBox.setItems(
    			FXCollections.observableArrayList(
    					affichageTypeDate.keySet()));
    	
    	dateFiltrePicker.setEditable(false);
    }
    
    private void setValeursDeDateParDefaut() {
    	typeFiltreDateComBox.getSelectionModel().select("après le");
    	typeDateComBox.getSelectionModel().select("Mot saisi");
    	dateFiltrePicker.setValue(LocalDate.now());
    }
    
    private void setVeleurImageParDefaut() {
    	imagePresenteChBox.setSelected(false);
    }
    
    private void setValeursParDefaut() {
    	setValeursDeDateParDefaut();
    	setVeleurImageParDefaut();
    }
    
    public void setValeurSelonFiltre(FiltreDeRecherche filtreReference) {
    	if (filtreReference.estNull()) {
    		setValeursParDefaut();
    	} else {
    		FiltreParDate filtreDate = filtreReference.getFiltreParDate();
    		if (filtreDate != null) {
    			setValeursDeDate(filtreDate);
    			this.filtreDateChBox.setSelected(true);
    		} else {
    			setValeursDeDateParDefaut();
    		}
    		this.imagePresenteChBox.setSelected(filtreReference.getDoitContenirImage());
    	}
    	

    }

	private void setValeursDeDate(FiltreParDate filtreDate) {
		typeFiltreDateComBox.getSelectionModel().select(
				getMomentString(
						filtreDate.getMoment()));
    	typeDateComBox.getSelectionModel().select(
    			getTypeFiltreString(
    					filtreDate.getTypeDate()));
    	dateFiltrePicker.setValue(filtreDate.getDate());
	}

	private String getTypeFiltreString(FILTRE_PAR_DATE_DE typeDate) {
		return affichageTypeDate.entrySet()
				.stream()
				.filter((e) -> {return typeDate == e.getValue();})
				.map((e)->{return e.getKey();})
				.findFirst()
				.get();
	}

	private String getMomentString(MOMENT_PAR_RAPPORT_A_DATE moment) {
		return affichageMoment.entrySet()
			.stream()
			.filter((e) -> {return moment == e.getValue();})
			.map((e)->{return e.getKey();})
			.findFirst()
			.get();
	}
    
}
