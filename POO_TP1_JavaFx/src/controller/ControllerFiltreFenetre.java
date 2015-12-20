package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class ControllerFiltreFenetre {

    @FXML
    private CheckBox filtreDateChBox;

    @FXML
    private ComboBox<?> typeDateFiltreComBox;

    @FXML
    private ComboBox<?> typeFiltreComBox;

    @FXML
    private DatePicker dateFiltrePicker;

    @FXML
    private CheckBox imagePresenteChBox;
    
    @FXML
    private Button annulerBtn;

    @FXML
    private Button appliquerBtn;

}
