package group6.java.group6;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AutoPlaylistDialogController {

    @FXML
    private ToggleGroup criterionGroup;

    @FXML
    private RadioButton byGenreRadio;

    @FXML
    private RadioButton byYearRadio;

    @FXML
    private RadioButton byTagRadio;

    @FXML
    private VBox genrePane;

    @FXML
    private ComboBox<String> genreCombo;

    @FXML
    private VBox yearPane;

    @FXML
    private Spinner<Integer> yearFromSpinner;

    @FXML
    private Spinner<Integer> yearToSpinner;

    @FXML
    private VBox tagPane;

    @FXML
    private CheckBox tagFavourite;

    @FXML
    private CheckBox tagExplicit;

    @FXML
    private CheckBox tagNewRelease;

    @FXML
    private TextField playlistNameField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleCriterionChange() {
        // Logica per mostrare/nascondere i pannelli in base al criterio
    }
}