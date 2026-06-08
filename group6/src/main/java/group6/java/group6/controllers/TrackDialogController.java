package group6.java.group6.controllers;

import java.io.File;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class TrackDialogController {
    @FXML private Label dialogTitle;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<GenreEnum> genreCombo;
    @FXML private Spinner<Integer> yearSpinner;
    @FXML private ToggleButton starredBtn;
    @FXML private ToggleButton chillBtn;
    @FXML private ToggleButton workoutBtn;
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
        // Assegno lo specifico elemento della enumerazione ad ogni radio button
        starredBtn.setUserData(TagEnum.Preferiti);
        chillBtn.setUserData(TagEnum.Chill);
        workoutBtn.setUserData(TagEnum.Allenamento);
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
            ToggleButton bottoneCliccato = (ToggleButton) selezionato;

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

    public void setSelectedFile(File file) {
        this.selectedFile = file;
    }

    public boolean validate() {
    return getTitle() != null && !getTitle().isBlank()
            && getAuthor() != null && !getAuthor().isBlank()
            && getGenre() != null
            && getSelectedFile() != null;
}

public void showValidationError() {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setHeaderText("Dati mancanti");
    alert.setContentText("Compila titolo, autore, genere e scegli un file audio.");
    alert.showAndWait();
}



    public void setTitleField(String title) {
        titleField.setText(title);
    }

    public void setAuthorField(String author) {
        authorField.setText(author);
    }

    public void setGenreCombo(GenreEnum genre) {
        genreCombo.setValue(genre);
    }

    public void setYearSpinner(Integer year) {
        yearSpinner.getValueFactory().setValue(year);
    }

    public void setFileNameLabel(String fileName) {
        fileNameLabel.setText(fileName);
    }

    public void setToggleGroup(TagEnum tag) {
        // mostra come selezionato il toggle salvato
        for (Toggle toggle : toggleGroup.getToggles()) {

            // Controlli se l'Enum nascosto (UserData) corrisponde a quello cercato
            if (toggle.getUserData() == tag) {

                // Trovato! Lo selezioni e interrompi il ciclo
                toggle.setSelected(true);
                break;

            }
        }
    }
}