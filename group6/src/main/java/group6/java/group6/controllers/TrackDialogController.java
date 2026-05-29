package group6.java.group6.controllers;

import group6.java.group6.enumerations.Genre;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TrackDialogController {

    @FXML
    private Label dialogTitle;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private ComboBox<Genre> genreCombo;

    @FXML
    private Spinner<Integer> yearSpinner;

    @FXML
    private TextField lengthField;

    @FXML
    private CheckBox tagFavourite;

    @FXML
    private CheckBox tagExplicit;

    @FXML
    private CheckBox tagNewRelease;

    @FXML
    private Label errorLabel;

    @FXML
    private ButtonType saveBtn;

    @FXML
    private ButtonType cancelBtn;

    @FXML
    public void initialize() {
        // tramite questa istruzione mostriamo nella tendina dei generi musicali quelli della enumerazione
        genreCombo.getItems().setAll(Genre.values());

    }

    public String getTitle() {
        return titleField.getText();
    }
    public String getAuthor() {
        return authorField.getText();
    }
    public Genre getGenre() {
        return genreCombo.getValue();
    }

    public Integer getYear() {

        return yearSpinner.getValue();
    }
    public Double getLength() {
        return Double.parseDouble(lengthField.getText());
    }
}