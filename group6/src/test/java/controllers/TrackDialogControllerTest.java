package controllers;

import group6.java.group6.controllers.TrackDialogController;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per il funzionamento logico di TrackDialogController.
 * Poiché i componenti grafici JavaFX non vengono caricati dal file FXML,
 * li creiamo a mano e li inseriamo nel controller simulando il caricamento
 * dell'interfaccia.
 */
public class TrackDialogControllerTest {

    private TrackDialogController controller;

    private TextField titleField;
    private TextField authorField;
    private ComboBox<GenreEnum> genreCombo;
    private Spinner<Integer> yearSpinner;
    private RadioButton starredBtn;
    private RadioButton chillBtn;
    private RadioButton workoutBtn;

    // Avvia l'ambiente JavaFX una sola volta per tutta la sessione di test
    @BeforeAll
    static void inizializzaAmbienteJavaFX() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException giaAvviato) {
            // Se è già attivo, non facciamo nulla
        }
    }

    // Prima di ogni test, creiamo un controller nuovo con elementi grafici puliti
    @BeforeEach
    void preparaController() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> {
            controller = new TrackDialogController();

            // Inizializziamo i componenti grafici reali
            titleField = new TextField();
            authorField = new TextField();
            genreCombo = new ComboBox<>();
            yearSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2100, 2024));
            starredBtn = new RadioButton("Preferiti");
            chillBtn = new RadioButton("Chill");
            workoutBtn = new RadioButton("Workout");

            // Li colleghiamo alle variabili interne del controller
            iniettaCampoPrivato("titleField", titleField);
            iniettaCampoPrivato("authorField", authorField);
            iniettaCampoPrivato("genreCombo", genreCombo);
            iniettaCampoPrivato("yearSpinner", yearSpinner);
            iniettaCampoPrivato("starredBtn", starredBtn);
            iniettaCampoPrivato("chillBtn", chillBtn);
            iniettaCampoPrivato("workoutBtn", workoutBtn);

            // inizializzazione del controller
            controller.initialize();
        });
    }

    @Test
    void getTitle() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> titleField.setText("Bohemian Rhapsody"));
        assertEquals("Bohemian Rhapsody", controller.getTitle());
    }

    @Test
    void getAuthor() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> authorField.setText("Queen"));
        assertEquals("Queen", controller.getAuthor());
    }

    @Test
    void getGenre() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> genreCombo.setValue(GenreEnum.METAL));
        assertEquals(GenreEnum.METAL, controller.getGenre());
    }

    @Test
    void getYear() throws InterruptedException {
        assertEquals(Integer.valueOf(2024), controller.getYear());
    }

    @Test
    void initialize() throws InterruptedException {
        assertEquals(GenreEnum.values().length, genreCombo.getItems().size());
    }

    @Test
    void nessunTagSelezionato() throws InterruptedException {
        assertNull(controller.getOptionSelected());
        assertNull(controller.getTag());
    }

    @Test
    void selezioneTagChill() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> chillBtn.setSelected(true));
        assertEquals("Chill", controller.getOptionSelected());
    }

    @Test
    void selezioneTagPreferiti() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> starredBtn.setSelected(true));
        assertEquals(TagEnum.Preferiti, controller.getTag());
    }

    @Test
    void selezioneBottoni() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> {
            starredBtn.setSelected(true);
            chillBtn.setSelected(true); // Selezionando questo, l'altro deve disattivarsi
        });
        assertFalse(starredBtn.isSelected());
        assertTrue(chillBtn.isSelected());
    }

    @Test
    void convalidaForm_conCampiTuttiValidi() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> compila("Titolo", "Artista", GenreEnum.METAL, new File("canzone.mp3")));
        assertTrue(controller.validate());
    }

    @Test
    void convalidaForm_conTitoloVuoto() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> compila("   ", "Artista", GenreEnum.METAL, new File("canzone.mp3")));
        assertFalse(controller.validate());
    }

    @Test
    void convalidaForm_conAutoreMancante() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> compila("Titolo", "", GenreEnum.METAL, new File("canzone.mp3")));
        assertFalse(controller.validate());
    }

    @Test
    void convalidaForm_conGenereMancante() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> compila("Titolo", "Artista", null, new File("canzone.mp3")));
        assertFalse(controller.validate());
    }

    @Test
    void convalidaForm_conFileMancante() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> compila("Titolo", "Artista", GenreEnum.METAL, null));
        assertFalse(controller.validate());
    }

    @Test
    void getSelectedFile() throws InterruptedException {
        File fileMock = new File("traccia.wav");
        eseguiSuThreadJavaFX(() -> iniettaCampoPrivato("selectedFile", fileMock));
        assertEquals(fileMock, controller.getSelectedFile());
    }

    private void compila(String titolo, String autore, GenreEnum genere, File file) {
        titleField.setText(titolo);
        authorField.setText(autore);
        genreCombo.setValue(genere);
        iniettaCampoPrivato("selectedFile", file);
    }

    // Inietta un oggetto dentro un campo privato del controller
    private void iniettaCampoPrivato(String nomeCampo, Object valore) {
        try {
            Field field = TrackDialogController.class.getDeclaredField(nomeCampo);
            field.setAccessible(true);
            field.set(controller, valore);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Errore di configurazione del test. Non trovo il campo: " + nomeCampo, e);
        }
    }

    // Interagire con i componenti grafici di JavaFX
    private void eseguiSuThreadJavaFX(Runnable azione) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                azione.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}