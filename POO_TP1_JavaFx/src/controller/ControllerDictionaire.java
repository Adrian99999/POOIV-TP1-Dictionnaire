package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.control.TextInputControl;
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
import vue.ParametresAffichage;

/**
 * Contolleur principal de l'application Dictionnaire.
 * </br></br>
 * (Classe beaucoup trop grande qui mériterait d'être
 * divisée en plusieurs contrôleurs secondaires.)
 * @author François Lefebvre & Adrian Pinzaru
 *
 */
public class ControllerDictionaire implements Initializable {
	
	/**
	 * Modes que peut prendre l'application.
	 */
	public enum MODE {
		CHARGEMENT, NORMAL, RECHERCHE;
	}
	
	public enum TYPE_MODIFICATION {
		MODIFICATION, CREATION, SUPPRESSION
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
    
    @FXML
    private Text definitionFiltreText;
    
    @FXML
    private Pane conteneurImage;
    
    /**
     * Initialization du contrôleur.
     * Chargement du dictionnaire, réglages de l'affichage et
     * réglage des fonctionnalités.
     */
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		lancerLeChargementDuDictionnaire();
		finaliserLAffichageDeDepart();
		setBindings();
		setListenersAndEventHandlers();
		setListViewModifiableAvecDrag();
	}
    
    /**
     * Lancement du dictionnaire sur un thread secondaire. Permet
     * d'interagir avec l'application.
     */
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
    
	/**
	 * Création du dictionnaire.
	 */
	private void creerLeDictionnaire() {
		FabriqueMotSingleton fabriqueDic = FabriqueMotSingleton.getInstance();
	    dictionnaire = fabriqueDic.getDictionnaire();
	}
    
	/**
	 * Lance une recherche par préfixe. La fin du mot n'a pas à être spécifiée.
	 */
	private void lancerRechercheOuverte() {
		this.parametresDeRecherche.setRechercheOuverte(true);
		this.lancerRecherche();
	}
	
	/**
	 * Lance la recherche si elle est permise.
	 */
	private void lancerRecherche() {
		if (this.recherchePermise()) {
			setMode(MODE.RECHERCHE);
			afficherLaListeDesMots(dictionnaire.rechercher(parametresDeRecherche));
			setMode(MODE.NORMAL);
			champRecherche.end();
		}
	}
	
	/**
	 * Affiche une liste de mot dans le menu de liste des résultats.
	 * @param collection Liste de mots à afficher.
	 */
	private void afficherLaListeDesMots(Collection<String> collection) {
		listeDesMotsAffiches.clear();
		listeDesMotsAffiches.addAll(collection);
		listViewMots.getSelectionModel().selectFirst();
	}
    
	/**
	 * Gère les actions à prendre quand les touches Retour et Flèche vers
	 * le bas sont actionnées dans le champ de recherche.
	 * @param event Événement de touche.
	 */
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
    
	/**
	 * Affiche le mot sélectionné dans la liste des résultats.
	 */
	private void afficherMotSelectionne() {
		if (!listViewMots.getSelectionModel().isEmpty()) {
			String libelle = listViewMots.getSelectionModel().getSelectedItem();
			demanderDAfficher(dictionnaire.get(libelle));
		} else {
			sectionDefinition.setVisible(false);
		}
	}
	
	/**
	 * Gère une demande d'affichage de mot.
	 * @param mot Mot à afficher.
	 */
	private void demanderDAfficher(Mot mot)
	{
		ButtonType reponse = sAssurerQueToutesLesModificationsSontSavegardeesSinonLeDemander();
		
		boolean annulerLAffichage = (reponse == ButtonType.CANCEL);
		
		if (!annulerLAffichage) afficher(mot);
	}
	
	/**
	 * Affiche un mot
	 * @param mot Mot à afficher.
	 */
	private void afficher(Mot mot) {
		if(mot != null) {
    		setValeursMot(mot);
        	setAffichageMot(mot);
    	} else {
    		sectionDefinition.setVisible(false);
    	}
		this.dernierMotAffiche = mot;
	}
	
	/**
	 * Définit les valeurs d'affichage du mot.
	 * @param mot Mot à afficher.
	 */
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
		imageController.demanderSetImage(mot.getNomFichier(), false);
	 }
	
	/**
	 * Réinitialise les paramètres d'affichage des mots et du filtre.
	 */
    private void setAffichageMot(Mot mot) {
		sectionDefinition.setVisible(true);
		textFieldAffichageMot.setEditable(false);
		textAreaDifinition.setEditable(false);
		buttonModifier.setDisable(dictionnaire.containsValue(mot));
		buttonAnnuler.setDisable(dictionnaire.containsValue(mot));
	}	private void afficherDefinitionFiltre() {
		definitionFiltreText.setText(this.parametresDeRecherche.getDefinition());
		if (!this.parametresDeRecherche.hasFiltreParDateOuParImage()) {
			definitionFiltreText.setFill(Color.GREEN);
		} else {
			definitionFiltreText.setFill(Color.ORANGE);
		}
	}
	   
	/**
	 * Affiche la définition.
	 * @param definition Définition à afficher.
	 */
	private void afficherDefinition(String definition) {
		if (definition.isEmpty()) {
			textAreaDifinition.getStyleClass().add("defaultDefinition");
			textAreaDifinition.setText(ParametresAffichage.DEFINITION_DEFAUT);
		} else {
			 textAreaDifinition.getStyleClass().removeAll("defaultDefinition");
			 textAreaDifinition.setText(definition);
		}
	}
	
	/**
	 * S'assure que toutes les modifications apportées sont sauvegardée. Si
	 * certaines modifications ne sont pas sauvegardées, on demande à
	 * l'utilisateur s'il souhaite le faire.
	 * @return ButtonType Choix de l'utilisateur.
	 */
    private ButtonType sAssurerQueToutesLesModificationsSontSavegardeesSinonLeDemander() {
    	ButtonType reponse = ButtonType.CANCEL;
    	
    	if (this.dictionnaireAEteModifie())
		{
    		TYPE_MODIFICATION typeModification;
			
    		if (!dictionnaire.containsKey(getMotTelQuAffiche().getMot())) {
				typeModification = TYPE_MODIFICATION.CREATION;
			} else {
				typeModification = TYPE_MODIFICATION.MODIFICATION;
			}
			
			reponse = confirmerModification(
						getMotTelQuAffiche().getMot(),
						true,
						typeModification
						);
			
			if (reponse == ButtonType.YES) {
				Mot motSauvegarde = enregistrerLesModificationsAuDictionnaire();
				reponse = motSauvegarde == null ? ButtonType.CANCEL : reponse;
			}
		} else {
			reponse = ButtonType.YES;
		}
		return reponse;
	}
    
    /**
     * Gère l'ajout de nouveaux mot par l'interface graphique.
     * @param event
     */
    @FXML
    void ajouterMot(ActionEvent event) {
		Mot motAAjouter = new Mot(this.champRecherche.getText());
    	dictionnaire.ajouter(motAAjouter);
    	lancerRechercheOuverte();
    	demanderDAfficher(motAAjouter);
    }
	
    /**
     * Gère la suppression de mot par l'interface graphique.
     * @param event
     */
    @FXML
    void supprimerMot(ActionEvent event) {
    	ButtonType reponse = confirmerModification(
    			listViewMots.getSelectionModel().getSelectedItem(),
    			false,
    			TYPE_MODIFICATION.SUPPRESSION
    			);

    	if(reponse == ButtonType.YES)
    	{
    		String motAEffacer = listViewMots.getSelectionModel().getSelectedItem();
    		listeDesMotsAffiches.remove(motAEffacer);
    		dictionnaire.remove(motAEffacer);
    		listViewMots.requestFocus();
    	}
    }
    
    /**
     * Demande l'enregistrement des modifications après avoir actionné
     * le bouton associé.
     * @param event Action sur bouton.
     */
	@FXML
    void modifierMot(ActionEvent event) {
		Mot motMisAJour = enregistrerLesModificationsAuDictionnaire();
		if (motMisAJour != null) afficher(motMisAJour);
    }
    
    /**
     * Enregistre toute modification au dictionnaire.
     * @return Mot Mot créé ou sauvegarder.
     */
    private Mot enregistrerLesModificationsAuDictionnaire() {
    	Mot motRetour;
    	try {
    		motRetour = dictionnaire.update(getMotTelQuAffiche());
    	} catch (Dictionnaire.DefinitionTropLongueException e) {
    		motRetour = null;
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setHeaderText("Définition trop longue.");
    		alert.setContentText("La définition ne doit pas dépasser "
    				+ dictionnaire.getMaxMotDef() + " mots.");
    		alert.showAndWait();
    		this.textAreaDifinition.setEditable(true);
    		this.textAreaDifinition.requestFocus();
    	}
    	return motRetour;
    }
	
	/**
	 * Récupère le mot tel qu'affiché.
	 * @return Mot tel qu'affiché.
	 */
    private Mot getMotTelQuAffiche() {
    	return new Mot(
    			this.textFieldAffichageMot.getText(),
    			(this.definitionEstNulle() ? "" : textAreaDifinition.getText()),
    			imageController.getCheminImage()
    			);
	}
    
    /**
     * Vérifie si la définition affichée est nulle.
     * @return Vrai ou faux.
     */
	private boolean definitionEstNulle() {
		return textAreaDifinition.getText().equals(ParametresAffichage.DEFINITION_DEFAUT);
	}
    
    /**
     * Vérifie si le dictionnaire a été modifié.
     * @return Vrai ou faux
     */
    private boolean dictionnaireAEteModifie() {
    	if (this.dernierMotAffiche == null) return false;
    	return !dernierMotAffiche.equals(getMotTelQuAffiche());
	}
    
    /**
     * Affiche un message d'alerte demandant à l'utilisateur d'accepter ou 
     * non une modification.
     * @param libelle Libellé du mot.
     * @param optionAnnuler Avec ou sans option d'annulation.
     * @param typeModification Type de modification effectuée.
     * @return ButtonType Bouton choisi par l'utilisateur.
     */
	private ButtonType confirmerModification(
			String libelle, 
			boolean optionAnnuler, 
			TYPE_MODIFICATION typeModification) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Enregistrer les modifications");
		
		String verbe = "";
		
		switch (typeModification) {
		case CREATION:
			verbe = "créé";
			break;
		case MODIFICATION:
			verbe = "modifié";
			break;
		case SUPPRESSION:
			verbe = "supprimé";
			break;
		}
		alert.setHeaderText("Le mot " + Mot.capitalize(libelle) + " a été " + verbe + ".");
		alert.setContentText("Voulez-vous sauvegarder cette modification?");
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
	
	/**
	 * Affiche la fenêtre des options de filtre pour la recherche et 
	 * y associe un observateur.
	 * @param event
	 */
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
    
    /**
     * Affiche la fenêtre des options du filtre.
     * @return Contrôleur de la fenêtre de filtre.
     * @throws IOException
     */
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
		controleurFiltre.setValeurSelonFiltre(this.parametresDeRecherche);
		return controleurFiltre;
	}
	
	/**
	 * Gère le bouton annuler. Demande d'afficher le dernier mot affiché.
	 * @param event
	 */
	 @FXML
	    void annulerModification(ActionEvent event) {
	    	afficher(this.dernierMotAffiche);
	    }
	 
	 /**
	  * Gère les cliques sur le libellé du mot.
	  * @param event
	  */
	@FXML
	void gererCliqueSurMot(MouseEvent event) {
		gererCliqueSurTextInputControl(event, false);
	}
	
	/**
	 * Gère les cliques sur la définition.
	 * @param event
	 */
	@FXML
	void gererCliqueSurDefinition(MouseEvent event) {
		gererCliqueSurTextInputControl(event, true);
	}
	
	/**
	 * Détermine la zone de texte a été doublement cliquée et efface ou pas
	 * le contenu de la zone.
	 * @param event
	 * @param effacement Effacer ou pas la zone de texte.
	 */
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
	
	/**
	 * Définie le comportement de la zone définition lors de la
	 * détection d'un événement drag et drop.
	 */
	private void setDraggableDefinition() {
		this.textAreaDifinition.setOnDragDetected(e -> {
			Dragboard db = ((TextArea) e.getSource()).startDragAndDrop(TransferMode.COPY);
			ClipboardContent cbc = new ClipboardContent();
			cbc.put(ModifiableByDragListCell.DEFINITION, this.textAreaDifinition.getText());
			db.setContent(cbc);
		});
	}
	
	/**
	 * Définie la Cell Factory associée à la listView. Y associe un observateur
	 * qui gère les données acquises lors d'un drag et drop.
	 */
	private void setListViewModifiableAvecDrag() {
		setDraggableDefinition();
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
					ButtonType reponse = confirmerModification(
							Mot.capitalize(libelle),
							false,
							TYPE_MODIFICATION.MODIFICATION
							);
					if (reponse == ButtonType.YES) {
						if (definitionNouvelle != null) 
							dictionnaire.get(libelle).setDefinition(definitionNouvelle);
						if (pathNouvelle != null) {
							dictionnaire.get(libelle).setImageAssocieAuMot(pathNouvelle);
						}
					}
				}
			});
		});
	}
	
	/**
	 * Définie une série d'observateurs pour différents éléments.
	 */
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
		
		textFieldAffichageMot.focusedProperty().addListener((obs, o, nouvelleValeurDeFocus) -> {
			if (!nouvelleValeurDeFocus) {
				this.textFieldAffichageMot.setEditable(false);
				if (dictionnaireAEteModifie()) {
					buttonModifier.setDisable(false);
					buttonAnnuler.setDisable(false);
				}
			}
		});
		
		textAreaDifinition.focusedProperty().addListener((obs, o, nouvelleValeurDeFocus) -> {
			if (!nouvelleValeurDeFocus) {
				this.textAreaDifinition.setEditable(false);
				afficherDefinition(this.textAreaDifinition.getText().trim());
				if (dictionnaireAEteModifie()) {
					buttonModifier.setDisable(false);
					buttonAnnuler.setDisable(false);
				}
			}
		});
		
		textFieldAffichageMot.setOnAction(e -> this.sectionDefinition.requestFocus());
		
		textAreaDifinition.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				this.sectionDefinition.requestFocus();
				e.consume();
			}
		});
		
		this.imageController.imageAChangeProperty().addListener((obs, old, ne) -> {
			if (ne) {
				buttonModifier.setDisable(false);
				buttonAnnuler.setDisable(false);
			}
		});
		
		this.listeDesMotsAffiches.addListener(
				(ListChangeListener.Change<? extends String> c) -> {
					buttonAjouter.setDisable(!motPeutEtreAjoute(this.champRecherche.getText()));
		});

	 }
	 
	 /**
	  * Lie l'affichage au mot sélectionné dans la ListView.
	  */
	 private void lierSelectionMotEtAffichage() {
			listViewMots.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if (listViewMots.isFocused() && newValue != null) {
						buttonEffacer.setDisable(false);
						demanderDAfficher(dictionnaire.get(newValue));
						buttonEffacer.setDisable(false);
					} else {
						buttonEffacer.setDisable(true);
					}
				}
			});
		}
	
	 /**
	  * Définie l'action à prendre à chaque modification du champ de
	  * recherche.
	  */
	private void setOnChangeTexteChampDeRecherche() {
		 champRecherche.textProperty().addListener((obs, old, ne)->{
				buttonAjouter.setDisable(!motPeutEtreAjoute(ne));
				lancerRechercheOuverte();
				});
	}
	 
	/**
	 * Vérifie si le mot peut être ajouté au dictionnaire.
	 * @param expression libelle du mot
	 * @return boolean
	 */
	 private boolean motPeutEtreAjoute(String expression) {
		return !this.champRecherche.getText().isEmpty() &&
				!this.dansLeMotChBox.isSelected() &&
				!expressionExacteExisteDansDictionnaire(expression);
	}
	 
	 /**
	  * Vérifie si l'expression existe ans le dictionnaire.
	  * @param expressionDuChamp Libellé du mot.
	  * @return boolean
	  */
   private boolean expressionExacteExisteDansDictionnaire(String expressionDuChamp) {
		if (dictionnaire != null) { 
			return dictionnaire.containsKey(expressionDuChamp.toLowerCase());
		} else {
			return false;
		}
	 }
   
   /**
    * Gère le changement de propriété de sélection du checkbox pour 
    * la recherche dans le mot.
    */
	private void setOnDisablePropertyChangePourDansLeMotChexbox() {
		 this.dansLeMotChBox.disabledProperty().addListener((obs, o, newBooleanValue) -> {
				if (newBooleanValue == false) {
					this.dansLeMotChBox.setSelected(this.parametresDeRecherche.rechercheDansContenuDemandeProperty().get());
				} else {
					this.dansLeMotChBox.setSelected(false);
				}
			});
	}
	
	/**
	 * Définie un ensemble de bindings.
	 */
	private void setBindings() {
		gererAutomatiquementLaLargeurDeLaLegendeDuFiltre();
		 parametresDeRecherche.expressionProperty().bind(
				 champRecherche.textProperty());
		dansLeMotChBox.disableProperty().bind(
				parametresDeRecherche.recherchePermiseDansContenuProperty().not()
				);
	}
	
	/**
	 * Permet d'ajuster de manière dynamique la largeur de la zone d'affichage
	 * des paramètres de filtre (Classe Text). La classe Text a été utilisée
	 * car elle permet de wrapper le texte tout en lui définissant une couleur.
	 */
	private void gererAutomatiquementLaLargeurDeLaLegendeDuFiltre() {
			definitionFiltreText.wrappingWidthProperty().bind(
					((Pane) definitionFiltreText.getParent()).widthProperty().subtract(20));
	}
    
	/**
	 * Définie les paramètres d'affichage selon le mode dans lequel se trouve
	 * l'application.
	 * @param mode Mode de l'application.
	 */
	private void setMode(MODE mode) {
		
		switch (mode) {
		case CHARGEMENT:
			setRecherchePermise(false);
			listViewMots.setDisable(true);
			listeDesMotsAffiches.setAll("Chargement...");
			break;
		case NORMAL:
			setRecherchePermise(true);
			listViewMots.setDisable(false);
			break;
		case RECHERCHE:
			listViewMots.setDisable(true);
			listeDesMotsAffiches.setAll("Rechreche en cours...");
			break;
		}
	}
	
	/**
	 * Définie si la recherche est permise.
	 * @param b
	 */
    private void setRecherchePermise(boolean b) {
		this.recherchePermise = b;
	}
    
    /**
     * Retourne vrai si la recherche est permise. Faux autrement.
     * @return
     */
	private boolean recherchePermise() {
		return this.recherchePermise;
	}
	
	/**
	 * Définie les propriétés d'affichage de départ non prises en charge
	 * dans le fxml.
	 */
	private void finaliserLAffichageDeDepart() {
		buttonAjouter.setDisable(true);
		buttonEffacer.setDisable(true);
		afficherDefinitionFiltre();
		sectionDefinition.setVisible(false);
	}
	
	/**
	 * Gère le menu fermer pour fermer l'application.
	 * @param event
	 */
    @FXML
    void fermerApplication(ActionEvent event) {
    	Platform.exit();
    }
}
