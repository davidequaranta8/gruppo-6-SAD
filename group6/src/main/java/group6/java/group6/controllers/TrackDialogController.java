package group6.java.group6.controllers;

import group6.java.group6.enumerations.GenreEnum;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TrackDialogController {
    @FXML private Label dialogTitle;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<GenreEnum> genreCombo;
    @FXML private Spinner<Integer> yearSpinner;
    @FXML private TextField lengthField;
    @FXML private RadioButton starredBtn;
    @FXML private RadioButton chillBtn;
    @FXML private RadioButton workoutBtn;
    @FXML private Label errorLabel;
    @FXML private ButtonType saveBtn;
    @FXML private ButtonType cancelBtn;

    @FXML
    public void initialize() {

        // tramite questa istruzione mostriamo nella tendina dei generi musicali quelli della enumerazione
        genreCombo.getItems().setAll(GenreEnum.values());

        // Crei il gruppo
        ToggleGroup gruppo = new ToggleGroup();

        // Assegni i radio button al gruppo
        starredBtn.setToggleGroup(gruppo);
        chillBtn.setToggleGroup(gruppo);
        workoutBtn.setToggleGroup(gruppo);
    }


    public String getTitle() {
        return titleField.getText();
    }
    public String getAuthor() {
        return authorField.getText();
    }
    public GenreEnum getGenre() {
        return genreCombo.getValue();
    }

    public Integer getYear() {

        return yearSpinner.getValue();
    }
    public Double getLength() {
        return Double.parseDouble(lengthField.getText());
    }
}