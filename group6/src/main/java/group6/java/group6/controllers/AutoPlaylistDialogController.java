package group6.java.group6.controllers;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

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
    private CheckBox tagPreferiti;

    @FXML
    private CheckBox tagChill;

    @FXML
    private CheckBox tagAllenamento;

    @FXML
    private TextField playlistNameField;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        for (GenreEnum g : GenreEnum.values()) {
            genreCombo.getItems().add(g.name());
        }
    }

    @FXML
    private void handleCriterionChange() {
        boolean isGenre = byGenreRadio.isSelected();
        boolean isYear = byYearRadio.isSelected();
        boolean isTag = byTagRadio.isSelected();

        genrePane.setVisible(isGenre);
        genrePane.setManaged(isGenre);
        yearPane.setVisible(isYear);
        yearPane.setManaged(isYear);
        tagPane.setVisible(isTag);
        tagPane.setManaged(isTag);
    }

    public List<TagEnum> getSelectedTags() {
        List<TagEnum> selected = new ArrayList<>();
        if (tagPreferiti.isSelected()) selected.add(TagEnum.Preferiti);
        if (tagChill.isSelected()) selected.add(TagEnum.Chill);
        if (tagAllenamento.isSelected()) selected.add(TagEnum.Allenamento);
        return selected;
    }

    public String getPlaylistName() {
        return playlistNameField.getText() == null ? "" : playlistNameField.getText().trim();
    }

    public TextField getPlaylistNameField() {
        return playlistNameField;
    }

    public boolean isTagSelected() {
        return byTagRadio.isSelected();
    }

    public boolean isYearSelected() {
        return byYearRadio.isSelected();
    }

    public boolean isGenreSelected() {
        return byGenreRadio.isSelected();
    }

    public GenreEnum getSelectedGenre() {
        String selected = genreCombo.getValue();
        if (selected == null) return null;
        try {
            return GenreEnum.valueOf(selected);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public int getYearFrom() {
        return yearFromSpinner.getValue();
    }

    public int getYearTo() {
        return yearToSpinner.getValue();
    }

    public boolean validate() {
        if (byTagRadio.isSelected() && getSelectedTags().isEmpty()) {
            return false;
        }
        if (byGenreRadio.isSelected() && getSelectedGenre() == null) {
            return false;
        }
        return !getPlaylistName().isEmpty();
    }

    public void showValidationError() {
        if (byTagRadio.isSelected() && getSelectedTags().isEmpty()) {
            errorLabel.setText("Seleziona almeno un tag.");
        } else if (byGenreRadio.isSelected() && getSelectedGenre() == null) {
            errorLabel.setText("Seleziona un genere.");
        } else if (getPlaylistName().isEmpty()) {
            errorLabel.setText("Il nome della playlist non può essere vuoto.");
        }
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    public void setName(String name) {
        playlistNameField.setText(name);
    }
}
