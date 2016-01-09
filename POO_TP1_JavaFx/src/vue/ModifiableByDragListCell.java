package vue;

import java.util.Observer;
import javafx.scene.control.ListCell;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
/**
 * Classe qui est la "Cell factory" pour le listView.
 * @author François Lefebvre & Adrian Pinzaru
 *
 */
public class ModifiableByDragListCell extends ListCell<String> {
	/**
	 * Type de contenu "Définition"
	 */
	public static final DataFormat DEFINITION = new  DataFormat("DEFINITION");
	
	/**
	 * Type de contenu de chemin vers ressource graphique
	 */
	public static final DataFormat IMAGE_PATH = new  DataFormat("IMAGE_PATH");
	
	/**
	 * L'obervateur qui sera alerté lors de changements.
	 */
	private Observer observer;
	
	/**
	 * Constructeur de la factory de cellule. Définie les actions à faire
	 * lors des événements de drag et de drop sur les cellules de 
	 * la listView.
	 * @param observer Observateur alerté lors de changements
	 */
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
			if (e.getDragboard().hasContent(IMAGE_PATH) || e.getDragboard().hasContent(DEFINITION)) {
				e.acceptTransferModes(TransferMode.COPY);
			}
			e.consume();
		});
		
		// Alerte l'observateur lors du drop
		this.setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();
            if (event.getTransferMode() == TransferMode.COPY && 
            		(dragboard.hasContent(IMAGE_PATH) || dragboard.hasContent(DEFINITION))) {
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
	
	/**
	 * Méthode de l'interface ListCell
	 */
	@Override
     public void updateItem(String item, boolean empty) {
         super.updateItem(item, empty);
         this.setText(item);
     }
}
