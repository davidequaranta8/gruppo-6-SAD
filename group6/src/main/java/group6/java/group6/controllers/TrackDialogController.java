package group6.java.group6.controllers;

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
    private ComboBox<String> genreCombo;

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

}