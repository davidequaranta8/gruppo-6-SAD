package controllers;

import group6.java.group6.controllers.MyMainController;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Track;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class MyMainControllerTest {

    private MyMainController controller;
    private ComboBox<GenreEnum> genreFilter;
    private TableView<Track> tracksTableView;
    private Label detailTitle;
    private Label detailGenre;
    private Button playPauseBtn;

    @BeforeAll
    static void inizializzaAmbienteJavaFX() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException giaAvviato) {
        }
    }

    @BeforeEach
    void preparaController() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> {
            controller = new MyMainController();

            genreFilter = new ComboBox<>();
            tracksTableView = new TableView<>();
            detailTitle = new Label("");
            detailGenre = new Label("");

            playPauseBtn = new Button();
            playPauseBtn.setGraphic(new FontIcon("fas-play"));

            iniettaCampoPrivato("searchField", new TextField());
            iniettaCampoPrivato("genreFilter", genreFilter);
            iniettaCampoPrivato("tracksTableView", tracksTableView);
            iniettaCampoPrivato("playPauseBtn", playPauseBtn);
            iniettaCampoPrivato("colTitle", new TableColumn<>());
            iniettaCampoPrivato("colAuthor", new TableColumn<>());
            iniettaCampoPrivato("colGenre", new TableColumn<>());
            iniettaCampoPrivato("colLength", new TableColumn<>());

            iniettaCampoPrivato("detailTitle", detailTitle);
            iniettaCampoPrivato("detailAuthor", new Label(""));
            iniettaCampoPrivato("detailGenre", detailGenre);
            iniettaCampoPrivato("detailYear", new Label(""));
            iniettaCampoPrivato("detailLength", new Label(""));
            iniettaCampoPrivato("detailTag", new Label(""));

            // Svuota la libreria e database, così ogni test parte pulito
            svuotaLibreria();
            controller.initialize();
        });
    }

    @AfterEach
    void pulisciAmbiente() throws InterruptedException {
        eseguiSuThreadJavaFX(this::svuotaLibreria);
    }

    @Test
    void initialize() throws InterruptedException {
        eseguiSuThreadJavaFX(() -> {
            assertEquals(GenreEnum.values().length, genreFilter.getItems().size());
        });
    }

    @Test
    void onLibraryChanged() throws InterruptedException {
        Track tracciaMock = new Track("Billie Jean", "Michael Jackson", GenreEnum.POP, 1982, TagEnum.Preferiti);

        eseguiSuThreadJavaFX(() -> {
            tracksTableView.getItems().clear();

            try {
                ConcreteLibrary.getInstance().addTrack(tracciaMock);
            } catch (DuplicateTitleTrackException e) {
                throw new RuntimeException(e);
            }

            controller.onLibraryChanged();

            assertEquals(1, tracksTableView.getItems().size());
            assertEquals("Billie Jean", tracksTableView.getItems().get(0).getTitle());
        });
    }

    @Test
    void addTrack_lanciaExceptionSeTracciaDuplicata() {
        Track traccia1 = new Track("Billie Jean", "Michael Jackson", GenreEnum.POP, 1982, TagEnum.Preferiti);
        Track tracciaDuplicata = new Track("Billie Jean", "Michael Jackson", GenreEnum.POP, 1982, TagEnum.Preferiti);

        ConcreteLibrary libreria = ConcreteLibrary.getInstance();

        // Primo inserimento: la libreria è stata svuotata, quindi non deve lanciare
        // eccezioni
        assertDoesNotThrow(() -> libreria.addTrack(traccia1));

        // Secondo inserimento con stesso titolo: deve lanciare l'eccezione
        assertThrows(DuplicateTitleTrackException.class, () -> libreria.addTrack(tracciaDuplicata));
    }

    @Test
    void handleTrackSelected() throws InterruptedException {
        Track tracciaSelezionata = new Track("Billie Jean", "Michael Jackson", GenreEnum.POP, 1982,
                TagEnum.Allenamento);

        eseguiSuThreadJavaFX(() -> {
            tracksTableView.getItems().add(tracciaSelezionata);
            tracksTableView.getSelectionModel().select(tracciaSelezionata);
            eseguiMetodoProtetto("handleTrackSelected");
        });

        assertEquals("Billie Jean", detailTitle.getText());
        assertEquals(GenreEnum.POP.toString(), detailGenre.getText());
    }

    @Test
    void handleDeleteTrack() throws InterruptedException {
        Track tracciaDaRimuovere = new Track("Billie Jean", "Michael Jackson", GenreEnum.POP, 1982, TagEnum.Chill);

        eseguiSuThreadJavaFX(() -> {
            try {
                ConcreteLibrary.getInstance().addTrack(tracciaDaRimuovere);
            } catch (DuplicateTitleTrackException e) {
                throw new RuntimeException(e);
            }

            controller.onLibraryChanged();
            tracksTableView.getSelectionModel().select(tracciaDaRimuovere);
            eseguiMetodoProtetto("handleDeleteTrack");
        });

        assertFalse(ConcreteLibrary.getInstance().getTracks().contains(tracciaDaRimuovere));
    }

    @Test
    void handlePlayPause() throws InterruptedException {
        Track tracciaMock = new Track("Billie Jean", "Michael Jackson", GenreEnum.POP, 1982, TagEnum.Chill);
        eseguiSuThreadJavaFX(() -> {
            tracksTableView.getItems().add(tracciaMock);
            tracksTableView.getSelectionModel().select(tracciaMock);
            assertDoesNotThrow(() -> eseguiMetodoProtetto("handlePlayPause"));
        });
    }

    // Svuota libreria e database rimuovendo ogni traccia (removeTrack cancella
    // anche dal DB)
    private void svuotaLibreria() {
        ConcreteLibrary libreria = ConcreteLibrary.getInstance();
        for (Track t : new HashSet<>(libreria.getTracks())) {
            libreria.removeTrack(t);
        }
    }

    private void eseguiMetodoProtetto(String nomeMetodo) {
        try {
            java.lang.reflect.Method metodo = MyMainController.class.getDeclaredMethod(nomeMetodo);
            metodo.setAccessible(true);
            metodo.invoke(controller);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void iniettaCampoPrivato(String nomeCampo, Object valore) {
        try {
            Field field = MyMainController.class.getDeclaredField(nomeCampo);
            field.setAccessible(true);
            field.set(controller, valore);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

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