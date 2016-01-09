package controller;

import java.net.MalformedURLException;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import vue.ModifiableByDragListCell;

/**
 * Classe qui gère l'affichage des images liées aux mots.
 * @author François Lefebvre & Arian Pinzaru
 *
 */
public class ControllerImage {

    @FXML
    private ImageView imageView;
    private String cheminImage;
	private static final String CHEMIN_PAR_DEFAUT = "vue/dictionnaire.png";
	/**
	 * Propriété booléenne qui permet d'être écoutée par d'autres classes et
	 * avertir si un changement d'image est survenu.
	 */
	private BooleanProperty imageAChange = new SimpleBooleanProperty();
    
	/**
	 * Gère le drag detected.
	 * @param event
	 */
    @FXML
    void onDragDetected(MouseEvent event) {
    	Dragboard dragboard =((ImageView)event.getSource()).startDragAndDrop(TransferMode.COPY);
    	ClipboardContent content = new ClipboardContent();
    	content.put(ModifiableByDragListCell.IMAGE_PATH, cheminImage);
    	dragboard.setContent(content);
    	event.consume();
    }
    
    /**
     * Gère le drag done.
     * @param event
     */
    @FXML
    void onDragDone(DragEvent event) {
    	if(event.getTransferMode() == TransferMode.MOVE)
    	{
    		((ImageView)event.getSource()).setImage(null);
    	}
    	event.consume();
    }

    /**
     * Gère le drag dropped
     * @param event
     */
	@FXML
    void onDragDropped(DragEvent event) {
    	Dragboard dragboard = event.getDragboard();
    	String path = null;
    	
    	if(dragboard.hasFiles() && dragboard.getFiles().size() == 1)
    	{
    		try {
				path = dragboard.getFiles().get(0)
						.toURI().toURL().toString();
			} catch (MalformedURLException e) {}
    	} else if (dragboard.hasString()) {
			path = dragboard.getString();
		}
    	if (path != null) {
    		this.demanderSetImage(path, true);
    	}
    	event.setDropCompleted(path == null);
    	event.consume();
    }
	
	/**
	 * Gère le drag entered
	 * @param event
	 */
    @FXML
    void onDragEntered(DragEvent event) {
    	if (clipboardValidePourImage(event.getDragboard())) {
    		imageView.setEffect(new Glow());
    	}
    }
    
    /**
     * Gère le drag exited
     * @param event
     */
    @FXML
    void onDragExited(DragEvent event) {
    	imageView.setEffect(null);
    }
    
    /**
     * Gère le drag over
     * @param event
     */
    @FXML
    void onDragOver(DragEvent event) {
    	if (clipboardValidePourImage(event.getDragboard()))
    	{
    		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    	}
    }
    
    /**
     * Vérifie si le clipboard est valide pour un drop sur l'image.
     * @param cb
     * @return
     */
    private boolean clipboardValidePourImage(Clipboard cb) {
    	if (cb.hasFiles()) {
    		return true;
    	}
    	if (cb.hasString() && !cb.getString().equals(this.cheminImage)) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Demande d'afficher une image à partir du chemin d'accès de l'image. 
     * Si aucun chemin, affiche l'image par défaut.
     * Si l'image est déjà affichée, ne charge pas de nouveau l'image.
     * Un thread s'occupe du chargement de l'image.
     * @param path
     * @param modification
     */
    public void demanderSetImage(String path, boolean modification) {
    	try {
    		if (!path.equals(this.cheminImage))
    		{
    			if (path.isEmpty()) {
    				this.setImage(new Image(CHEMIN_PAR_DEFAUT), "", modification);
    			} else {
    				clearImage();
	    			new Thread(() -> {
		    			Image imageThread = new Image(path);
		    			
		    			Platform.runLater(() -> {
		    				this.setImage(imageThread, path, modification);
		    			});
		    		}).start();
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Méthode pour l'affichage de l'image.
     * @param image Image à afficher
     * @param path Chemin d'accès à l'image
     * @param modification définie si l'image affichée fait suite à une 
     * modification (true) ou si elle fait suite à l'affichage d'un
     * mot (false).
     */
    private void setImage(Image image, String path, boolean modification) {
    	imageView.setImage(image);
    	this.cheminImage = path;
		this.imageAChange.set(modification);
    }
	
    /**
     * Retourne le BooleanProperty lié à l'état de modification de l'image.
     * @return
     */
	public ReadOnlyBooleanProperty imageAChangeProperty() {
		return this.imageAChange;
	}
	
	/**
	 * Méthode auxilière pour vérifier si l'image a été modifiée.
	 * @return
	 */
	public boolean imageAEteModifie() {
		return this.imageAChange.get();
	}
	
	/**
	 * Retourne le chemin de l'image affichée.
	 * @return
	 */
	public String getCheminImage() {
		return this.cheminImage;
	}
	
	/**
	 * Efface l'image.
	 */
	private void clearImage() {
		this.setImage(null, "", false);
	}

}

