package group6.java.group6.controllers;

import group6.java.group6.HelloApplication;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Library;
import group6.java.group6.models.LibraryObserver;
import group6.java.group6.models.Track;
import group6.java.group6.player.AudioPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.function.Consumer;

// questa classe rappresenta il concreteObserver per il pattern Observer applicato con Library
public class MyMainController implements LibraryObserver{

    private final AudioPlayer audioPlayer = new AudioPlayer();

    // ── Top bar ──────────────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private Button undoBtn;

    // ── Sidebar sinistra ─────────────────────────────────────────────────────
    @FXML private ListView<String> playlistListView;
    @FXML private ListView<String> autoPlaylistListView;
    @FXML private Button newPlaylistBtn;
    @FXML private Button generateByGenreBtn;   // mancava
    @FXML private Button generateByYearBtn;    // mancava

    // ── Barra azioni playlist ────────────────────────────────────────────────
    // Nel FXML il bottone Rinomina ha fx:id="RenamePlaylist"
    @FXML private Button RenamePlaylist;       // nome esatto come nel FXML
    @FXML private Button deletePlaylistBtn;

    // ── Filtri ───────────────────────────────────────────────────────────────
    @FXML private ComboBox<GenreEnum> genreFilter;
    @FXML private ComboBox<String> yearFilter;
    @FXML private Button addTrackBtn;

    // ── Tabella tracce ────────────────────────────────────────────────────────
    @FXML private TableView<Track> tracksTableView;
    @FXML private TableColumn<Track, Integer> colNow;
    @FXML private TableColumn<Track, String> colTitle;
    @FXML private TableColumn<Track, String> colAuthor;
    @FXML private TableColumn<Track, GenreEnum> colGenre;
    @FXML private TableColumn<Track, Double> colLength;
    /*
    * VEDI DI RIMUOVERE QUALCHE COLONNA DELLA TABELLA DELLE TRACCE,
    * SICCOME NON VENGONO MOSTRATE TUTTE
     */

    // ── Sezione home ──────────────────────────────────────────────────────────
    @FXML private VBox homeSectionBox;
    @FXML private ListView<String> topTracksListView;

    // ── Player bar ────────────────────────────────────────────────────────────
    @FXML private VBox playerBar;
    @FXML private Label currentTimeLabel;
    @FXML private Slider progressSlider;
    @FXML private Label totalTimeLabel;
    @FXML private ToggleButton shuffleToggleBtn;
    @FXML private ToggleButton loopBtn;
    @FXML private Button prevBtn;
    @FXML private Button playPauseBtn;
    @FXML private Button nextBtn;
    @FXML private Label nowPlayingTitle;
    @FXML private Label nowPlayingAuthor;

    // ── Pannello dettaglio ────────────────────────────────────────────────────
    @FXML private VBox detailPanel;
    @FXML private Label detailTitle;
    @FXML private Label detailAuthor;
    @FXML private Label detailGenre;
    @FXML private Label detailYear;
    @FXML private Label detailLength;
    @FXML private CheckBox tagFavourite;
    @FXML private CheckBox tagExplicit;
    @FXML private CheckBox tagNewRelease;
    @FXML private Button editTrackBtn;
    @FXML private Button removeFromPlaylistBtn;
    @FXML private Button deleteTrackBtn;

    // ═════════════════════════════════════════════════════════════════════════
    //  INITIALIZE
    // ═════════════════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {

        // tramite questa istruzione mostriamo nella tendina dei generi musicali quelli della enumerazione
        genreFilter.getItems().setAll(GenreEnum.values());

        //if (addTrackBtn != null)   addTrackBtn.setDisable(false);
        if (RenamePlaylist != null) RenamePlaylist.setDisable(false);
        if (editTrackBtn != null)  editTrackBtn.setDisable(false);

        // collegamento tra le colonne e gli attributi della classe Track
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));

        // registrazione l'observer
        Library myLibrary = ConcreteLibrary.getInstance();
        myLibrary.addObserver(this); // inserisco l'observer nella lista degli osservatori da aggiornare
        updateTracksTable(); // effettuo uno primo aggiornamento della tabella


    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HANDLERS
    // ═════════════════════════════════════════════════════════════════════════

    @FXML protected void handleUndo() {}

    // ── Playlist ──────────────────────────────────────────────────────────────
    @FXML
    protected void handleNewPlaylist() {
        showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Nuova Playlist",null);
    }

    @FXML
    protected void handleGenerateByGenre() {
        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist da Genere",null);
    }

    @FXML
    protected void handleGenerateByYear() {
        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist da Anno",null);
    }

    @FXML
    protected void handleRenamePlaylist() {
        showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Rinomina Playlist",null);
    }

    @FXML protected void handleDeletePlaylist() {}

    // Tracce
    @FXML
    protected void handleAddTrack() {

        // definiamo il metodo accept() del Consumer
        showDialog("TrackDialog.fxml", "Aggiungi Traccia", (TrackDialogController controller) -> {

            // 1. Preleviamo i dati usando i getter del TrackDialogController
            Track newTrack = new Track(

                    controller.getTitle(),
                    controller.getAuthor(),
                    controller.getLength(),
                    controller.getGenre(),
                    controller.getYear(),
                    TagEnum.valueOf(controller.getOptionSelected())
            );

            // 2. Aggiungiamo la traccia al Singleton
            // Questo farà scattare automaticamente l'Observer e aggiornerà la tabella
            ConcreteLibrary.getInstance().addTrack(newTrack); //chiama internamente il trackDao che salva nel db e costruisce il filepath della track

            //prendiamoci il file selezionato nel dialog e
            File selectedFile = controller.getSelectedFile();
            if (selectedFile != null) {
                try {
                    Path dest = Paths.get(newTrack.getFilePath()); // es. "music/42.mp3"
                    Files.createDirectories(dest.getParent());     // crea la cartella "music/" se non esiste
                    Files.copy(selectedFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    protected void handleEditTrack() {
        showDialog("TrackDialog.fxml", "Modifica Traccia",null);
    }

    @FXML protected void handleDeleteTrack() {
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
        ConcreteLibrary.getInstance().removeTrack(selectedTrack);

    }
    @FXML protected void handleRemoveFromPlaylist() {}

    @FXML protected void handleFilter() {}
    @FXML protected void handleResetFilter() {}

    @FXML protected void handlePlayAll() {}
    @FXML protected void handleShuffle() {}
    @FXML protected void handlePrev() {}


    @FXML
    protected void handlePlayPause() {
        FontIcon icon = (FontIcon) playPauseBtn.getGraphic();

        if (audioPlayer.isPlaying()) {
            // sta suonando → metti in pausa
            audioPlayer.pause();
            icon.setIconLiteral("fas-play");
        } else if (audioPlayer.isPaused()) {
            // era in pausa → riprendi da dove era
            audioPlayer.resume();
            icon.setIconLiteral("fas-pause");
        } else {
            // non sta suonando nulla → carica e avvia la traccia selezionata
            Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
            if (selectedTrack != null) {
                audioPlayer.play(selectedTrack);
                icon.setIconLiteral("fas-pause");
            }
        }
    }
    @FXML protected void handleNext() {}

    @FXML protected void handleTagChange() {}

    @FXML
    protected void handleGeneratePlaylist(){

    }
    //  metodo per mostrare i DialogPane ed effettuare operazioni nel momento in cui si cliccano i btn associati ad essa
    private <T> void showDialog(String fxmlFile, String title, Consumer<T> onOkAction) {
        try{
            // carico la scena del TrackDialog
            var url = HelloApplication.class.getResource(fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            DialogPane dialogPane = fxmlLoader.load();

            // Recupera il controller TrackDialogController
            T controller = fxmlLoader.getController();

            // setto la scena del TrackDialog nel PopUp del DialogPane
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(title);

            // Mostriamo il dialog e aspettiamo che venga chiuso in qualche modo
            dialog.showAndWait().ifPresent(buttonType -> {
                // se premi il bottone SALVA sul popUp allora esegue accept del consumer
                if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    // Eseguiamo la Lambda Expression passandole il controller
                    if (onOkAction != null) {
                        onOkAction.accept(controller);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLibraryChanged(){ // metodo ricavato da LibraryObserver per il pattern Observer
        updateTracksTable();
    }

    private void updateTracksTable() {
        tracksTableView.getItems().setAll(ConcreteLibrary.getInstance().getTracks());
    }
}