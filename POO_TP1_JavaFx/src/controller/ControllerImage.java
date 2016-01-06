package controller;

import java.io.File;
import java.net.URL;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Path;

	public class ControllerImage {

	    @FXML
	    private ImageView imageView;
		private Observer observer;
		private static final String CHEMIN_PAR_DEFAUT = "vue/dictionnaire.png";
	    
	    @FXML
	    void onDragDetected(MouseEvent event) {
	    	Dragboard dragboard =((ImageView)event.getSource()).startDragAndDrop(TransferMode.COPY);
	    	ClipboardContent content = new ClipboardContent();
	    	content.putImage(imageView.getImage());
	    	content.putString("mot");
	    	dragboard.setContent(content);
	    	
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
//	    	if(dragboard.hasImage())
//	    	{
//	    		this.setImage(dragboard.getString());
//	    		success = true;
//	    	} else 
	    		if (dragboard.hasString()) {
//	    			System.out.println(dragboard.getString());
//	    			System.out.println(dragboard.getUrl());
	    			System.out.println("has image " + dragboard.hasImage());
	    			this.setImage(dragboard.getString(), true);
	    			success = true;
	    		}
	    	event.setDropCompleted(success);
	    	event.consume();
	    }

	    @FXML
	    void onDragEntered(DragEvent event) {
	    	imageView.setEffect(new Glow());
	    }

	    @FXML
	    void onDragExited(DragEvent event) {
	    	imageView.setEffect(null);
	    }

	    @FXML
	    void onDragOver(DragEvent event) {
//	    	System.out.println(event.getDragboard().hasUrl());
//	    	System.out.println(event.getDragboard().hasImage());
//	    	System.out.println(event.getDragboard().getString());
	    	
	    	if (event.getDragboard().hasImage() || event.getDragboard().hasString())
	    	{
	    		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
//	    		System.out.println("transfert mode copy or move");
	    	}
	    }
	    
	    public void setImage(String path, boolean modification) {
	    	try {
	    		path = path.isEmpty() ? CHEMIN_PAR_DEFAUT : path;
//	    		URL url = new File(path).toURI().toURL();
//	    		System.out.println(url);
	    		imageView.setImage(new Image(path));
	    		if (modification) observer.update(null, path);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	
	    }
		
		public void addObserver(Observer observer) {
			this.observer = observer;
		}

	}

