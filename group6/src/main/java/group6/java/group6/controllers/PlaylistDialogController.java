package group6.java.group6.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PlaylistDialogController {

    @FXML
    private Label dialogTitle;

    @FXML
    private TextField nameField;

    @FXML
    private Label errorLabel; 

    public String getName() {
        return nameField.getText() == null ? "" : nameField.getText().trim();
    }
 
    // Pre-popola il campo nome  in modalità rinomina
    public void setName(String name) {
        nameField.setText(name);
    }
 
    public void setDialogTitle(String title) {
        if (dialogTitle != null) dialogTitle.setText(title);
    }
 
    // Valida i campi
    public boolean validate() {
        return !getName().isEmpty();
    }
 
    // Mostra il messaggio di errore di validazione
    public void showValidationError() {
        if (errorLabel != null) {
            errorLabel.setText("Il nome della playlist non può essere vuoto.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
}