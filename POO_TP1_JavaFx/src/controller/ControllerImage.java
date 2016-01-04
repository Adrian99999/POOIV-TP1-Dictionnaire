package controller;

import javafx.fxml.FXML;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

	public class ControllerImage {

	    @FXML
	    private ImageView imageView;
	    
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
	    	if(dragboard.hasImage() || dragboard.hasString())
	    	{
	    		imageView.setImage(dragboard.getImage());
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
	    	if(event.getGestureSource() != imageView && event.getDragboard().hasImage())
	    	{
	    		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	    	}
	    }
	   

	}

