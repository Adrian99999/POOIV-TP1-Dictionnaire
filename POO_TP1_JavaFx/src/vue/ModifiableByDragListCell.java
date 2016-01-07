package vue;

import java.util.Observer;

import javafx.scene.control.ListCell;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ModifiableByDragListCell extends ListCell<String> {
	public static final DataFormat DEFINITION = new  DataFormat("DEFINITION");
	public static final DataFormat IMAGE_PATH = new  DataFormat("IMAGE_PATH");
	private Observer observer;
	
	public ModifiableByDragListCell(Observer observer) {
		this.observer = observer;
		
		this.setOnDragEntered(e -> {
			this.setStyle("-fx-background-color: lightgrey");
		});
		
		this.setOnDragExited(e -> {
			this.setStyle("");
		});
		
		this.setOnDragOver(e -> {
			Dragboard dragboard = e.getDragboard();
//			System.out.println("over");
//			System.out.println("Image = " + dragboard.hasImage());
//            System.out.println("String = " + dragboard.getString());
//            System.out.println("URL = " + dragboard.getUrl());
//            System.out.println("--------");
			if (e.getDragboard().hasContent(IMAGE_PATH) || e.getDragboard().hasContent(DEFINITION)) {
//				System.out.println("Accept");
				e.acceptTransferModes(TransferMode.COPY);
			}
			e.consume();
		});
		
		this.setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();
//			System.out.println("dropped");
//			System.out.println("Image = " + dragboard.hasImage());
//            System.out.println("String = " + dragboard.getString());
//            System.out.println("URL = " + dragboard.getUrl());
//            System.out.println("--------");
            if (event.getTransferMode() == TransferMode.COPY && 
            		(dragboard.hasContent(IMAGE_PATH) || dragboard.hasContent(DEFINITION))) {
//                System.out.println("Image = " + dragboard.hasImage());
//                System.out.println("String = " + dragboard.getString());
//                System.out.println("URL = " + dragboard.getUrl());
            	this.observer.update(
                		null, 
                		new Object[]{
                				new String(this.getText()),
                				dragboard
                				});
                event.setDropCompleted(true);
            }
            event.consume();
		});
	
	}

	@Override
     public void updateItem(String item, boolean empty) {
         super.updateItem(item, empty);
         this.setText(item);
     }
}
