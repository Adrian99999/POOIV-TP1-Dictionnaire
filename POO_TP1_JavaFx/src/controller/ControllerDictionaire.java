package controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import vue.ParametresAffichage;







import javafx.application.Platform;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
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
import vue.ModifiableByDragListCell;

public class ControllerDictionaire implements Initializable {
	
	public enum MODE {
		CHARGEMENT, NORMAL, RECHERCHE;
	}
	
	private ObservableList<String> listeDesMotsAffiches = FXCollections.observableArrayList();
    private Dictionnaire dictionnaire;
    private FiltreDeRecherche parametresDeRecherche = FiltreDeRecherche.getDefault();
    private boolean recherchePermise;
    private Mot dernierMotAffiche;
    
    @FXML
    private ControllerImage imageController;
	
    @FXML
    private TextField champRecherche;

    @FXML
    private CheckBox dansLeMotChBox;

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
    	
//    @FXML
//    private MenuItem fermerApplication;
    
    @FXML
    private Text definitionFiltreText;
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		lancerLeChargementDuDictionnaire();
		finaliserLAffichageDeDepart();
		setBindings();
		setListenersAndEventHandlers();
		setListViewModifiableAvecDrag();
		setDraggableDefinition();
//		this.definitionFiltreText.getStyleClass().addListener(new ListChangeListener<String>() {
//
//			@Override
//			public void onChanged(
//					ListChangeListener.Change<? extends String> c) {
//						System.out.println(String.join(", ", c.getList()));
//					}
//		});
	}

	private void setDraggableDefinition() {
		this.textAreaDifinition.setOnDragDetected(e -> {
			Dragboard db = ((TextArea) e.getSource()).startDragAndDrop(TransferMode.COPY);
			ClipboardContent cbc = new ClipboardContent();
			cbc.put(ModifiableByDragListCell.DEFINITION, this.textAreaDifinition.getText());
			db.setContent(cbc);
		});
		
	}

	private void setListViewModifiableAvecDrag() {
		listViewMots.setCellFactory(listViewMots -> {
			return new ModifiableByDragListCell((observable, args) -> {
				Object[] arguments = (Object[]) args;
				String libelle = (String) arguments[0];
				Dragboard dragboard = (Dragboard) arguments[1];
				String pathNouvelle  = null;
				String definitionNouvelle = null;
			
				if (dragboard.hasContent(ModifiableByDragListCell.IMAGE_PATH))
				{
					String pathOriginale = dictionnaire.get(libelle).getNomFichier();
					pathNouvelle = (String) dragboard
							.getContent(ModifiableByDragListCell.IMAGE_PATH);
					if (pathNouvelle.equals(pathOriginale)) {
						pathNouvelle = null;
					}
				}
				
				if (dragboard.hasContent(ModifiableByDragListCell.DEFINITION))
				{
					String definitionOriginale = dictionnaire.get(libelle).getDefinition();
					definitionNouvelle = (String) dragboard
							.getContent(ModifiableByDragListCell.DEFINITION);
					if (definitionNouvelle.equals(definitionOriginale)) {
						definitionNouvelle = null;
					}
				}
				
				if (definitionNouvelle != null || pathNouvelle != null) {
					ButtonType reponse = confirmerModificationObligatoire(
							Mot.capitalize(libelle)
							);
					if (reponse == ButtonType.YES) {
						if (definitionNouvelle != null) 
							dictionnaire.get(libelle).setImageAssocieAuMot(definitionNouvelle);
						if (pathNouvelle != null) {
							dictionnaire.get(libelle).setImageAssocieAuMot(definitionNouvelle);
						}
					}
				}
			});
		});
		
	}

	private void lancerLeChargementDuDictionnaire() {
		setMode(MODE.CHARGEMENT);
		new Thread(() -> {
			creerLeDictionnaire();
			Platform.runLater(() -> {
				setMode(MODE.NORMAL);
				lancerRechercheOuverte();
			});
		}).start();
	}
	
	private void creerLeDictionnaire() {
		FabriqueMotSingleton fabriqueDic = FabriqueMotSingleton.getInstance();
	    dictionnaire = fabriqueDic.getDictionnaire();
	}
	
	 private void setListenersAndEventHandlers() {
		listViewMots.setItems(listeDesMotsAffiches);
		lierSelectionMotEtAffichage();
		setOnChangeTexteChampDeRecherche();
		dansLeMotChBox.setOnAction((e) -> {
			 this.parametresDeRecherche.rechercheDansContenuDemandeProperty().set(dansLeMotChBox.isSelected());
			 lancerRechercheOuverte();
		 });
		setOnDisablePropertyChangePourDansLeMotChexbox();
		listViewMots.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			listViewMots.requestFocus();
			listViewMots.getSelectionModel().select(-1);
			});
		this.textFieldAffichageMot.focusedProperty().addListener((obs, o, nouvelleValeurDeFocus) -> {
			if (!nouvelleValeurDeFocus) {
				this.textFieldAffichageMot.setEditable(false);
				if (isMotAEteModifie()) {
					buttonModifier.setDisable(false);
					buttonAnnuler.setDisable(false);
				}
			}
		});
		this.textAreaDifinition.focusedProperty().addListener((obs, o, nouvelleValeurDeFocus) -> {
			if (!nouvelleValeurDeFocus) {
				this.textAreaDifinition.setEditable(false);
				afficherDefinition(this.textAreaDifinition.getText().trim());
				if (isMotAEteModifie()) {
					buttonModifier.setDisable(false);
					buttonAnnuler.setDisable(false);
				}
			}
		});
		
		this.textFieldAffichageMot.setOnAction(e -> this.sectionDefinition.requestFocus());
		
		this.textAreaDifinition.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				this.sectionDefinition.requestFocus();
				e.consume();
			}
		});
		
//		this.imageController.addObserver((observable, args) -> {
//			this.dernierMotAffiche.setImageAssocieAuMot((String) args); 
//		});
		
		this.imageController.imageAChangeProperty().addListener((obs, old, ne) -> {
			if (ne) {
				buttonModifier.setDisable(false);
				buttonAnnuler.setDisable(false);
			}
		});

	 }
	 
	 private void lierSelectionMotEtAffichage() {
			listViewMots.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					buttonEffacer.setDisable(newValue != null);
					if (listViewMots.isFocused() && newValue != null) {
						afficherInfoMot(dictionnaire.get(newValue), false);
						buttonEffacer.setDisable(false);
					}
				}
			});
		}
	 
	private void setOnChangeTexteChampDeRecherche() {
		 champRecherche.textProperty().addListener((obs, old, ne)->{
				buttonAjouter.setDisable(!motPeutEtreAjoute(ne));
				lancerRechercheOuverte();
				});
	}
	 
	 private boolean motPeutEtreAjoute(String expression) {
		return !this.champRecherche.getText().isEmpty() &&
				!this.dansLeMotChBox.isSelected() &&
				!expressionExacteExisteDansDictionnaire(expression);
	}

	private void setOnDisablePropertyChangePourDansLeMotChexbox() {
		 this.dansLeMotChBox.disabledProperty().addListener((obs, o, newBooleanValue) -> {
				if (newBooleanValue == false) {
					this.dansLeMotChBox.setSelected(this.parametresDeRecherche.rechercheDansContenuDemandeProperty().get());
				} else {
					this.dansLeMotChBox.setSelected(false);
				}
			});
	}

	private void setBindings() {
		gererAutomatiquementLaLargeurDeLaLegendeDuFiltre();
		 parametresDeRecherche.expressionProperty().bind(
				 champRecherche.textProperty());
		dansLeMotChBox.disableProperty().bind(
				parametresDeRecherche.recherchePermiseDansContenuProperty().not()
				);

	}

	private void gererAutomatiquementLaLargeurDeLaLegendeDuFiltre() {
			definitionFiltreText.wrappingWidthProperty().bind(
					((Pane) definitionFiltreText.getParent()).widthProperty().subtract(20));
	}
    
    @FXML
    private Pane conteneurImage;
	private boolean expressionExacteExisteDansDictionnaire(String expressionDuChamp) {
		if (dictionnaire != null) { 
			return dictionnaire.containsKey(expressionDuChamp.toLowerCase());
		} else {
			return false;
		}
	 }
	
	private void lancerRechercheOuverte() {
		this.parametresDeRecherche.setRechercheOuverte(true);
		this.lancerRecherche();
	}
	
	private void lancerRecherche() {
		if (this.recherchePermise()) {
			setMode(MODE.RECHERCHE);
			afficherLaListeDesMots(dictionnaire.rechercher(parametresDeRecherche));
			setMode(MODE.NORMAL);
			champRecherche.end();
		}
	}
	
	private void afficherLaListeDesMots(Collection<String> collection) {
		listeDesMotsAffiches.clear();
		listeDesMotsAffiches.addAll(collection);
		listViewMots.getSelectionModel().selectFirst();
	}
	
	private void setMode(MODE mode) {
		
		switch (mode) {
		case CHARGEMENT:
			setRecherchePermise(false);
			listViewMots.setDisable(true);
			listeDesMotsAffiches.setAll("Chargement...");
			break;
		case NORMAL:
			this.recherchePermise = true;
			listViewMots.setDisable(false);
			break;
		case RECHERCHE:
			listViewMots.setDisable(true);
			listeDesMotsAffiches.setAll("Rechreche en cours...");
			break;
		}
	}
	
    private void setRecherchePermise(boolean b) {
		this.recherchePermise = b;
	}
    
	private boolean recherchePermise() {
		return this.recherchePermise;
//				&& this.parametresDeRecherche.validePourLaRecherche();
	}
	
	private void finaliserLAffichageDeDepart() {
		buttonAjouter.setDisable(true);
		buttonEffacer.setDisable(true);
		afficherDefinitionFiltre();
		sectionDefinition.setVisible(false);
	}

    @FXML
    void gererFiltreHyperlink(ActionEvent event) {
		try {
			
			ControllerFiltreFenetre controleurFiltre = afficherFenetreDeFiltre();
    		
    		controleurFiltre.addObserver((o, args) -> {
				Object[] objs = (Object[]) args;
    			this.parametresDeRecherche.setFiltreParDate((FiltreParDate) objs[0]);
    			this.parametresDeRecherche.setFiltreParDateActif((boolean) objs[1]);
				this.parametresDeRecherche.setDoitContenirImage((boolean) objs[2]);
    			this.afficherDefinitionFiltre();
    			this.lancerRechercheOuverte();
    		});
		
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	private ControllerFiltreFenetre afficherFenetreDeFiltre() throws IOException {
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
		controleurFiltre.setValeurSelonFiltre(this.parametresDeRecherche);
		return controleurFiltre;
	}

	@FXML
    void gererTouchesDuChampDeRecherche(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			afficherMotSelectionne();
			event.consume();
		} else if (event.getCode() == KeyCode.DOWN) {
			afficherMotSelectionne();
			listViewMots.requestFocus();
			event.consume();
		}
    }
	
	private void afficherMotSelectionne() {
		if (!listViewMots.getSelectionModel().isEmpty()) {
			String libelle = listViewMots.getSelectionModel().getSelectedItem();
			afficherInfoMot(dictionnaire.get(libelle), false);
		} else {
			sectionDefinition.setVisible(false);
		}
	}
       
    private void afficherInfoMot(Mot mot, boolean ecraserLesModifications)
    {
    	boolean afficherMot = ecraserLesModifications;
    	
    	if (this.isMotAEteModifie() && !ecraserLesModifications)
    	{
    		ButtonType reponse = confirmerModificationAvecAnnulation(
    				this.dernierMotAffiche.toString()
    				);
			
    		if (reponse == ButtonType.YES) {
				enregistrerLesModifications();
				afficherMot = true;
			} else if (reponse == ButtonType.NO) {
				afficherMot = true;
			} else {
				afficherMot = false;
			}
    	} 
    	else 
    	{
    		afficherMot = true;
    	}
    	
    	if (afficherMot) {
    		if(mot != null) {
	    		setValeursMot(mot);
	        	setAffichageMot();
	    	} else {
	    		sectionDefinition.setVisible(false);
	    	}
			this.dernierMotAffiche = mot;
    	}
    }
    
    private ButtonType confirmerModificationAvecAnnulation(String libelle) {
		return confirmerModification(libelle, true);
	}
    
    private ButtonType confirmerModificationObligatoire(String libelle) {
    	return confirmerModification(libelle, false);
    }

	private ButtonType confirmerModification(String libelle, boolean optionAnnuler) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Enregistrer les modifications");
		alert.setHeaderText("Le mot " + libelle + " a été modifié.");
		alert.setContentText("Voulez-vous les sauvegarder les modifications?");
		alert.getButtonTypes().removeAll(
				ButtonType.CANCEL, 
				ButtonType.OK);
		if (optionAnnuler) alert.getButtonTypes().add(ButtonType.CANCEL);
		alert.getButtonTypes().addAll(
				ButtonType.NO,
				ButtonType.YES);
		Optional<ButtonType> response = alert.showAndWait();
		if (response.isPresent()) {
			return response.get();
		} else {
			return ButtonType.NO;
		}
    }
	
    private void setValeursMot(Mot mot) {
    	textFieldAffichageMot.setText(mot.toString());
    	textFieldDateSaisieMot.setText(
    			"Saisi le " + mot.getDateSaisieMot().toString() + ". ");
    	textFieldDateSaisieMot.appendText(
    			mot.getDateModificationMot() == null ?
    					"Jamais modifié." : 
    						"Modifié le " + mot.getDateModificationMot() +
    						"."
    			);
    	afficherDefinition(mot.getDefinition());
		imageController.setImage(mot.getNomFichier(), false);
		
	}
    
//    private void afficherDefaultDefinition(){
//    	textAreaDifinition.setText(DEFINITION_DEFAUT);
//		textAreaDifinition.getStyleClass().add("defaultDefinition");
//    }
    
    private void afficherDefinition(String definition) {
    	if (definition.isEmpty()) {
    		textAreaDifinition.getStyleClass().add("defaultDefinition");
    		textAreaDifinition.setText(ParametresAffichage.DEFINITION_DEFAUT);
    	} else {
    		 textAreaDifinition.getStyleClass().removeAll("defaultDefinition");
    		 textAreaDifinition.setText(definition);
    	}
    }
	
    private void setAffichageMot() {
		sectionDefinition.setVisible(true);
		textFieldAffichageMot.setEditable(false);
		textAreaDifinition.setEditable(false);
		buttonModifier.setDisable(true);
		buttonAnnuler.setDisable(true);
	}	private void afficherDefinitionFiltre() {
		definitionFiltreText.setText(this.parametresDeRecherche.getDefinition());
		if (!this.parametresDeRecherche.hasFiltreParDateOuParImage()) {
			definitionFiltreText.setFill(Color.GREEN);
		} else {
			definitionFiltreText.setFill(Color.ORANGE);
		}
	}
	
    @FXML
    void modifierMot(ActionEvent event) {
//    	Mot motAAfficher;
    	//si le text du mot n'a pas été modiffié
    	if(!isLibelleMotAEteModifie())
    	{
    		Mot motModifie = enregistrerLesModifications();
    		this.afficherInfoMot(motModifie, true);
    	}
    	else
    	{
    		Mot nouveauMotCree = creerNouveauMot();
    		this.afficherInfoMot(nouveauMotCree, true);
    	}
//    	this.lancerRechercheOuverte();
//		this.afficherInfoMot(motAAfficher);
//		this.champRecherche.requestFocus();
//		this.champRecherche.end();
    	
    }
    
    private Mot creerNouveauMot() {
    	Mot newMot = new Mot(textFieldAffichageMot.getText().toLowerCase());
		newMot.setDateSaisieMot(LocalDate.now());
		if(!definitionEstNulle()) {
			newMot.setDefinition(textAreaDifinition.getText());
		}
		newMot.setImageAssocieAuMot(imageController.getCheminImage());
		dictionnaire.ajouter(newMot);
		return newMot;
	}

	private Mot enregistrerLesModifications() {
		Mot motModifie = this.dernierMotAffiche;
    	motModifie.setDefinition(this.definitionEstNulle() ? "" : textAreaDifinition.getText());
    	motModifie.setDateModificationMot(LocalDate.now());
		motModifie.setImageAssocieAuMot(imageController.getCheminImage());
		return motModifie;
	}

	private boolean isLibelleMotAEteModifie() {
			return !this.dernierMotAffiche.getMot().equals(textFieldAffichageMot.getText().toLowerCase());
    }
	
	private boolean isMotAEteModifie() {
		if (this.dernierMotAffiche != null) {
			return isLibelleMotAEteModifie() ||
					definitionAEteModifie() ||
					imageAEteModifie();
		} else {
			return false;
		}
	}
	
	private boolean imageAEteModifie() {
		return !this.dernierMotAffiche.getNomFichier()
				.equals(imageController.getCheminImage());
	}

	private boolean definitionEstNulle() {
		return textAreaDifinition.getText().equals(ParametresAffichage.DEFINITION_DEFAUT);
	}
	
	private boolean definitionAEteModifie() {
		String definitionOriginale = this.dernierMotAffiche.getDefinition();
		
		if (definitionEstNulle() && definitionOriginale.isEmpty()) {
			return false;
		}
		
		if (textAreaDifinition.getText().equals(definitionOriginale)) {
			return false;
		}
		
		return true;
	}

	@FXML
    void ajouterMot(ActionEvent event) {
    	
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
    		String motAEffacer = listViewMots.getSelectionModel().getSelectedItem();
    		listeDesMotsAffiches.remove(motAEffacer);
    		dictionnaire.remove(motAEffacer);
    		listViewMots.requestFocus();
    		
//    		afficherInfoMot(
//    				dictionnaire.get(
//    						listViewMots.getSelectionModel().getSelectedItem()
//    						),
//    				true
//    				);
    	}
    }
	
    @FXML
    void annulerModification(ActionEvent event) {
    	afficherInfoMot(this.dernierMotAffiche, true);
    }

//    @FXML
//    void gererCliqueDefinition(ActionEvent event) {
//
//    }

    @FXML
    void gererCliqueSurMot(MouseEvent event) {
    	gererCliqueSurTextInputControl(event, false);
    }
    
    @FXML
    void gererCliqueSurDefinition(MouseEvent event) {
    	gererCliqueSurTextInputControl(event, true);
    }
    
    private void gererCliqueSurTextInputControl(MouseEvent event, boolean effacement) {
    	if(event.getButton().equals(MouseButton.PRIMARY))
		{
			if(event.getClickCount() == 2)
			{
				TextInputControl tic = (TextInputControl) event.getSource();
				tic.setEditable(true);
			
				if (effacement && this.textAreaDifinition.getText().equals(ParametresAffichage.DEFINITION_DEFAUT)) {
					tic.clear();
				}
			}
		}
    	
    }

    @FXML
    void gererExpressionDansMotChBox(ActionEvent event) {

    }
    
    @FXML
    void fermerApplication(ActionEvent event) {
    	Platform.exit();
    }

    @FXML
    void rechercherAChaqueLettre(ActionEvent event) {

    }

    @FXML
    void rechercherLeMotComplet(ActionEvent event) {

    }

}

