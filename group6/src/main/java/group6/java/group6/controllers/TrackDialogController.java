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
}