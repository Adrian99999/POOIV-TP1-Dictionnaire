package controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Platform;


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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Dictionnaire;
import model.FabriqueMotSingleton;
import model.FiltreDeRecherche;
import model.FiltreParDate;
import model.Mot;

public class ControllerDictionaire implements Initializable{
	
	private ObservableList<String> listeDesMotsAffiches = FXCollections.observableArrayList();
    private Dictionnaire dictionnaire;
    private FiltreDeRecherche filtre = FiltreDeRecherche.getNull();
    private boolean recherchePermise;
	
    @FXML
    private TextField champRecherche;

    @FXML
    private CheckBox dansLeMotChBox;

//    @FXML
//    private CheckBox filtreChBox;

    @FXML
    private ListView<String> listViewMots;

    @FXML
    private Button buttonAjouter;

    @FXML
    private Button buttonEffacer;

    @FXML
    private VBox sectionDefinition;

    @FXML
    private TextField textFieldAffichageMot;

    @FXML
    private TextArea textAreaDifinition;

    @FXML
    private TextField textFieldDateSaisieMot;

    @FXML
    private TextField textFieldDateModificationMot;

    @FXML
    private Button buttonAnnuler;

    @FXML
    private Button buttonModifier;
    
    @FXML
    private Button buttonRechercher;
    	
    @FXML
    private MenuItem fermerApplication;
    
    @FXML
    private Text definitionFiltreText;

    @FXML
    void ajouterMot(ActionEvent event) {
    	System.out.println(buttonAjouter);
    }

    @FXML
    void modifierMot(ActionEvent event) {
    	//si le mot n'a pas été modiffié
    	if(listViewMots.getSelectionModel().getSelectedItem().equals(textFieldAffichageMot.getText().toString()))
    	{
    		dictionnaire.get(listViewMots.getSelectionModel().getSelectedItem()).setDefinition(textAreaDifinition.getText());
    		dictionnaire.get(listViewMots.getSelectionModel().getSelectedItem()).setDateModificationMot(LocalDate.now());
    		
    	}
    }
    
    @FXML
    void rechercher(ActionEvent event) {
    	this.lancerRecherche(champRecherche.getText());
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
    	Platform.exit();
    }

    @FXML
    void gererFiltreHyperlink(ActionEvent event) {
//    	if (filtreChBox.isSelected()) {
    		try {
				FXMLLoader fxmlLoader = new FXMLLoader();
    			Pane root = fxmlLoader.load(
						ControllerDictionaire.class.getResource(
								"../vue/FiltreFenetre.fxml"
								).openStream()
						);
				Stage filtreStage = new Stage();
	    		filtreStage.setTitle("Filtre");
	    		filtreStage.setScene(new Scene(root));
	    		filtreStage.show();
	    		ControllerFiltreFenetre controleurFiltre = 
	    				(ControllerFiltreFenetre) fxmlLoader.getController();
	    		controleurFiltre.setFenetre(filtreStage);
	    		controleurFiltre.setValeurSelonFiltre(this.filtre);
	    		controleurFiltre.addObserver((o, args) -> {
    				Object[] objs = (Object[]) args;
	    			this.filtre.setFiltreParDate((FiltreParDate) objs[0]);
	    			this.filtre.setFiltreParDateActif((boolean) objs[1]);
    				this.filtre.setDoitContenirImage((boolean) objs[2]);
	    			this.afficherDefinitionFiltre();
	    		});
    		} catch (IOException e) {
				e.printStackTrace();
			}
    		
//    	} else {
//    		desactiverLeFiltre();
//    	}
    }

	private void lancerRecherche(String expression) {
		try {
			this.filtre.setExpression(
					expression,
					dansLeMotChBox.isSelected()
					);
			listViewMots.setDisable(true);
			listeDesMotsAffiches.setAll("Rechreche en cours...");
			afficherLaListeDesMots(dictionnaire.rechercher(this.filtre));
			champRecherche.end();
			listViewMots.setDisable(false);
		} catch (Exception e) {
			afficherException(e, "Erreur de paramètre de recherche");
		}
	}

	@FXML
    void gererTouchesDuChampDeRecherche(KeyEvent event) {
		System.out.println("key typed event " + event.getCode());
		if (event.getCode() == KeyCode.ENTER) {
			if (!listViewMots.getSelectionModel().isEmpty()) {
				String libelle = listViewMots.getSelectionModel().getSelectedItem();
				System.out.println(libelle);
				afficherInfoMot(dictionnaire.get(libelle));
			} else {
				sectionDefinition.setVisible(false);
			}
			event.consume();
//		} else if (event.getCode() == KeyCode.UP) {
//			listViewMots.getSelectionModel().selectPrevious();
//			champRecherche.end();
//			event.consume();
		} else if (event.getCode() == KeyCode.DOWN) {
			listViewMots.requestFocus();
			listViewMots.getSelectionModel().selectNext();
			event.consume();
		}
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
    
       
    private void afficherInfoMot(Mot mot)
    {
    	sectionDefinition.setVisible(mot != null);
    	
    	if(mot != null)
    	{
    		textFieldAffichageMot.setText(mot.getCapMot());
    		String dateModification = mot.getDateModificationMot() == null ?
    				"" : mot.getDateModificationMot().toString();
    		textFieldDateModificationMot.setText(dateModification);
    		textFieldDateSaisieMot.setText(mot.getDateSaisieMot().toString());
    		//textFieldFichierMot.setText(mot.getNomFichier());
    		textAreaDifinition.setText(mot.getDefinition());
    		//pour ne pas avoir d'erreurs 
//    		Platform.runLater(new Runnable() {
//    		    @Override
//    		    public void run() {
//    		    	textAreaDifinition.requestFocus();
//    		    	champRecherche.requestFocus();
//    		    	champRecherche.end();
//    		    }
//    		});
    		
    	}
//    	else
//    	{
//    		
//    		textFieldDateModificationMot.setText("");
//    		textFieldDateSaisieMot.setText("");
//    		//textFieldFichierMot.setText("");
//    		textAreaDifinition.setText("");
//    	}
    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		lancerLeChargementDuDictionnaire();

		lierLesElements();

	}
	
	private void lierLesElements() {
		listViewMots.setItems(listeDesMotsAffiches);
		lierListeDesMotsEtAffichage();
		buttonAjouter.disableProperty().bind(champRecherche.textProperty().isEmpty());
		definitionFiltreText.wrappingWidthProperty().bind(
				((Pane) definitionFiltreText.getParent()).widthProperty().subtract(20));
		champRecherche.textProperty().addListener((obs, old, ne)->{
			lancerRechercheDebutantPar(ne);
		});
//		listViewMots.getSelectionModel()
//			.selectedIndexProperty().addListener(
//				(obs, old, n) -> {
//					listViewMots.scrollTo((int) n);
//				});
	}
	
	private void lancerRechercheDebutantPar(String expressionSaisie) {
		
		if (recherchePermise) {
			if (FiltreDeRecherche.contientDesJokers(expressionSaisie)) {
				lancerRecherche(expressionSaisie);
			} else {
				String expression = expressionSaisie + "*";
				if (this.filtre.validerExpression(expression)) {
					lancerRecherche(expression);
				}
			}
		}
	}
	
	private void lancerLeChargementDuDictionnaire() {
		
		this.recherchePermise = false;
		setInterfaceEnModeChargement();
		
		new Thread(() -> {
			creerLeDictionnaire();
			Platform.runLater(() -> {
				setInterfacePret();
//				afficherLaListeDesMots(dictionnaire.keySet());
				lancerRechercheDebutantPar(champRecherche.getText());
				});
		}).start();
		
	}
	
	private void afficherLaListeDesMots(Collection<String> collection) {
		listeDesMotsAffiches.clear();
		listeDesMotsAffiches.addAll(collection);
		listViewMots.getSelectionModel().selectFirst();
	}
	
	private void setInterfaceEnModeChargement() {
		afficherDefinitionFiltre();
		listViewMots.setDisable(true);
		listeDesMotsAffiches.add("Chargement...");
		sectionDefinition.setVisible(false);
		buttonAjouter.setDisable(true);
		buttonEffacer.setDisable(true);
		buttonRechercher.setDisable(true);
	}
	
	private void afficherDefinitionFiltre() {
		definitionFiltreText.setText(this.filtre.getDefinition());
		if (this.filtre.estNull()) {
			definitionFiltreText.setFill(Color.GREEN);
		} else {
			definitionFiltreText.setFill(Color.ORANGE);
		}
	}

	private void setInterfacePret() {
		this.recherchePermise = true;
		listViewMots.setDisable(false);
		buttonRechercher.setDisable(false);
	}
	
	private void lierListeDesMotsEtAffichage() {
		listViewMots.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				buttonEffacer.setDisable(newValue != null);
				if (listViewMots.isFocused()) {
					afficherInfoMot(dictionnaire.get(newValue));
				}
			}
		});
	}
	
	private void creerLeDictionnaire() {
		FabriqueMotSingleton fabriqueDic = FabriqueMotSingleton.getInstance();
	    dictionnaire = fabriqueDic.getDictionnaire();
	}
	
	/**
	 * Méthode qui lit le fichier de configuration
	 */	
	private void desactiverLeFiltre() {
		this.filtre = FiltreDeRecherche.getNull();
	}
	
	private void afficherException(Exception e, String titre) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erreur");
		alert.setHeaderText(titre);
		alert.getDialogPane().setPrefSize(400, 200);
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}

}

