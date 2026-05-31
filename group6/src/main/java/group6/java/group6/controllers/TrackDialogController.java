package group6.java.group6.controllers;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.LibraryFacade;
import group6.java.group6.models.Track;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class TrackDialogController {

    @FXML private Button chooseFileBtn;
    @FXML private Label fileNameLabel;
    private File selectedFile;
    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private Label dialogTitle;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private ComboBox<GenreEnum> genreCombo;

    @FXML
    private Spinner<Integer> yearSpinner;

    @FXML
    private TextField lengthField;

    @FXML 
    private CheckBox tagPreferiti;
    
    @FXML
    private CheckBox tagChill;      
    
    @FXML
    private CheckBox tagAllenamento;

    @FXML
    private Label errorLabel;

    @FXML
    private ButtonType saveBtn;

    @FXML
    private ButtonType cancelBtn;

    private LibraryFacade facade;

    private Track trackToEdit;
    
     @FXML
    public void initialize() {
        if (genreCombo != null) {
            genreCombo.getItems().addAll(GenreEnum.values()); 
        }
        int currentYear = java.time.Year.now().getValue();
        if (yearSpinner != null) {
            yearSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, currentYear, currentYear));
        }
        hideError();
    }
 
    public void setFacade(LibraryFacade facade) {
        this.facade = facade;
    }
 
    public void setTrackToEdit(Track track) {
        this.trackToEdit = track;
        if (track == null) {
            if (dialogTitle != null) 
                dialogTitle.setText("Aggiungi Traccia");
        } else {
            if (dialogTitle != null) 
                dialogTitle.setText("Modifica Traccia");
            populateForm(track);
        }
    }
 
    @FXML
    protected boolean  handleSave() {
        hideError();
 
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String lengthStr = lengthField.getText().trim();
        GenreEnum genre = genreCombo.getValue();
        int year = yearSpinner.getValue() != null ? yearSpinner.getValue() : 0;
        TagEnum selectedTag = getSelectedTagEnum();
 
        String error = validate(title, author, lengthStr, genre, year);
        if (error != null) {
            showError(error);
            return false;
        }
 
        // Converte la durata in secondi
        double length = toSeconds(lengthStr);
        if (trackToEdit == null) {
            facade.addTrack(title, author, length, genre, year, selectedTag);  // US-01
        } else {
            facade.updateTrack(trackToEdit, title, author, length, genre, year, selectedTag);  // US-02
        }
        return true;
    }
 
    private String validate(String title, String author,
                            String length, GenreEnum genre, int year) {
        if (title.isBlank())
            return "Il titolo non può essere vuoto.";
        if (author.isBlank())
            return "L'autore non può essere vuoto.";
        if (genre == null)
            return "Seleziona un genere.";
        int currentYear = java.time.Year.now().getValue();
        if (year < 1900 || year > currentYear)
            return "L'anno deve essere compreso tra 1900 e " + currentYear + ".";
        if (length.isBlank())
            return "La durata non può essere vuota.";
        try {
            double seconds = toSeconds(length);
            if (seconds <= 0)
                return "La durata deve essere maggiore di zero.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return null;
    }
 
    //Così nell'edit ha già form compilato, mentre nell'add è tutto vuoto
    private void populateForm(Track track) {
        if (titleField != null) titleField.setText(track.getTitle());
        if (authorField != null) authorField.setText(track.getAuthor());
        
        if (genreCombo != null && track.getGenre() != null) {
            genreCombo.setValue(track.getGenre());
        }
        
        if (yearSpinner != null) yearSpinner.getValueFactory().setValue(track.getYear());
        
        if (lengthField != null) {
            int total = (int) track.getLength();
            lengthField.setText(String.format("%d:%02d", total / 60, total % 60));
        }
        
         TagEnum currentTag = track.getTag();
        if (tagPreferiti   != null) tagPreferiti.setSelected(currentTag == TagEnum.Preferiti);
        if (tagChill       != null) tagChill.setSelected(currentTag == TagEnum.Chill);
        if (tagAllenamento != null) tagAllenamento.setSelected(currentTag == TagEnum.Allenamento);
    }
 
    private TagEnum getSelectedTagEnum() {
        if (tagPreferiti   != null && tagPreferiti.isSelected())   return TagEnum.Preferiti;
        if (tagChill       != null && tagChill.isSelected())       return TagEnum.Chill;
        if (tagAllenamento != null && tagAllenamento.isSelected()) return TagEnum.Allenamento;
        return null;
    }
 
    private double toSeconds(String input) {
        try {
            if (input.contains(":")) {
                String[] p = input.split(":");
                int min = Integer.parseInt(p[0].trim());
                int sec = Integer.parseInt(p[1].trim());
                if (sec < 0 || sec >= 60)
                    throw new IllegalArgumentException(
                        "I secondi devono essere compresi tra 0 e 59.");
                return min * 60.0 + sec;
            }
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Formato durata non valido. Usa mm:ss (es. 3:45) o secondi (es. 225).");
        }
    }
 
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
 
    private void hideError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }

    @FXML
    public void handleChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona file audio");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("File Audio", "*.mp3", "*.wav")
        );
        Stage stage = (Stage) chooseFileBtn.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName()); // mostra il nome del file scelto
        }
    }

    // getter per il MainController
    public File getSelectedFile() {
        return selectedFile;
    }
}