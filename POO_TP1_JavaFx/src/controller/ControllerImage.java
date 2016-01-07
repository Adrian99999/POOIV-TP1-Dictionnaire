package controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observer;
import java.util.ResourceBundle;








import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Path;
import vue.ModifiableByDragListCell;

	public class ControllerImage {

	    @FXML
	    private ImageView imageView;
	    private String cheminImage;
		private Observer observer;
		private static final String CHEMIN_PAR_DEFAUT = "vue/dictionnaire.png";
		private BooleanProperty imageAChange = new SimpleBooleanProperty();
	    
	    @FXML
	    void onDragDetected(MouseEvent event) {
	    	Dragboard dragboard =((ImageView)event.getSource()).startDragAndDrop(TransferMode.COPY);
	    	ClipboardContent content = new ClipboardContent();
//	    	System.out.println("Image ?");
//	    	System.out.println(imageView.getImage());
//	    	content.putImage(imageView.getImage());
	    	content.put(ModifiableByDragListCell.IMAGE_PATH, cheminImage);
	    	dragboard.setContent(content);
//	    	System.out.println(dragboard.hasImage());
	    	
	    	event.consume();
	    }

	    @FXML
	    void onDragDone(DragEvent event) {
	    	if(event.getTransferMode() == TransferMode.MOVE)
	    	{
	    		((ImageView)event.getSource()).setImage(null);
	    	}
	    	event.consume();
	    }

		@FXML
	    void onDragDropped(DragEvent event) {
	    	Dragboard dragboard = event.getDragboard();
	    	boolean success = false;
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
	    		this.setImage(path, true);
	    	}
	    	event.setDropCompleted(path == null);
	    	event.consume();
	    }

	    @FXML
	    void onDragEntered(DragEvent event) {
	    	if (clipboardValidePourImage(event.getDragboard())) {
	    		imageView.setEffect(new Glow());
	    	}
	    }

	    @FXML
	    void onDragExited(DragEvent event) {
	    	imageView.setEffect(null);
	    }

	    @FXML
	    void onDragOver(DragEvent event) {
	    	if (clipboardValidePourImage(event.getDragboard()))
	    	{
	    		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	    	}
	    }
	    
	    public void setImage(String path, boolean modification) {
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
	    		System.out.println("Erreur dans setimage");
	    	}
	    	
	    }
	    
	    private boolean clipboardValidePourImage(Clipboard cb) {
	    	if (cb.hasFiles()) {
	    		return true;
	    	}
	    	if (cb.hasString() && !cb.getString().equals(this.cheminImage)) {
	    		return true;
	    	}
	    	return false;
	    }
	    
	    private void setImage(Image image, String path, boolean modification) {
	    	imageView.setImage(image);
	    	this.cheminImage = path;
			this.imageAChange.set(modification);
	    }
		
//		public void addObserver(Observer observer) {
//			this.observer = observer;
//		}
		public ReadOnlyBooleanProperty imageAChangeProperty() {
			return this.imageAChange;
		}
		
		public boolean imageAEteModifie() {
			return this.imageAChange.get();
		}

		public String getCheminImage() {
			return this.cheminImage;
		}
		
		private void clearImage() {
			this.setImage(null, "", false);
		}

	}

