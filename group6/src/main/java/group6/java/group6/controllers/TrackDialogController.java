package group6.java.group6.controllers;

import java.io.File;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


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
    @FXML private Button chooseFileBtn;
    @FXML private Label fileNameLabel;
    private File selectedFile;
    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    public void initialize() {

        // tramite questa istruzione mostriamo nella tendina dei generi musicali quelli della enumerazione
        genreCombo.getItems().setAll(GenreEnum.values());


        // Assegni i radio button al gruppo
        starredBtn.setToggleGroup(toggleGroup);
        chillBtn.setToggleGroup(toggleGroup);
        workoutBtn.setToggleGroup(toggleGroup);
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

    public TagEnum getTag() {
        String tagSelected = getOptionSelected();
        if (tagSelected == null) {
            return null;
        }
        return TagEnum.valueOf(tagSelected); // Assicurati che i nomi dei tag siano in maiuscolo
    }

    public String getOptionSelected() {
        // 1. Chiediamo al gruppo quale elemento è attualmente selezionato
        // Restituisce un oggetto generico di tipo "Toggle"
        Toggle selezionato = toggleGroup.getSelectedToggle();

        // 2. Controllo di sicurezza: l'utente potrebbe non aver cliccato nulla
        if (selezionato != null) {
            // 3. Facciamo il "cast" da Toggle a RadioButton per poter leggere il testo
            RadioButton bottoneCliccato = (RadioButton) selezionato;

            // 4. Restituiamo la stringa (es. "Rock", "Pop", "Preferiti")
            return bottoneCliccato.getText();
        }

        // Se nessun bottone è stato selezionato, restituiamo null o una stringa vuota
        return null;
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