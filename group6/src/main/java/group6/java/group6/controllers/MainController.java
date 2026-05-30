package group6.java.group6.controllers;
import group6.java.group6.HelloApplication;
import group6.java.group6.enumerations.Genre;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.Library;
import group6.java.group6.models.LibraryObserver;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Track;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.function.Consumer;

// questa classe rappresenta il concreteObserver per il pattern Observer applicato con Library
public class MainController implements LibraryObserver{

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
    @FXML private ComboBox<String> genreFilter;
    @FXML private ComboBox<String> yearFilter;
    @FXML private Button addTrackBtn;

    // ── Tabella tracce ────────────────────────────────────────────────────────
    @FXML private TableView<Track> tracksTableView;
    @FXML private TableColumn<Track, Integer> colNow;
    @FXML private TableColumn<Track, String> colTitle;
    @FXML private TableColumn<Track, String> colAuthor;
    @FXML private TableColumn<Track, Genre> colGenre;
    @FXML private TableColumn<Track, Integer> colYear;
    @FXML private TableColumn<Track, Double> colLength;
    @FXML private TableColumn<Track, String> colTags;
    @FXML private TableColumn<Track, String> colActions;
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

        if (addTrackBtn != null)   addTrackBtn.setDisable(false);
        if (RenamePlaylist != null) RenamePlaylist.setDisable(false);
        if (editTrackBtn != null)  editTrackBtn.setDisable(false);

        // collegamento tra le colonne e gli attributi della classe Track
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        colTags.setCellValueFactory(new PropertyValueFactory<>("tag"));

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
                    TagEnum.REMEMBER_ME
            );

            // 2. Aggiungiamo la traccia al Singleton
            // Questo farà scattare automaticamente l'Observer e aggiornerà la tabella!
            ConcreteLibrary.getInstance().addTrack(newTrack);
        });
    }

    @FXML
    protected void handleEditTrack() {
        showDialog("TrackDialog.fxml", "Modifica Traccia",null);
    }

    @FXML protected void handleDeleteTrack() {}
    @FXML protected void handleRemoveFromPlaylist() {}

    @FXML protected void handleFilter() {}
    @FXML protected void handleResetFilter() {}

    @FXML protected void handlePlayAll() {}
    @FXML protected void handleShuffle() {}
    @FXML protected void handlePrev() {}
    @FXML protected void handlePlayPause() {}
    @FXML protected void handleNext() {}

    @FXML protected void handleTagChange() {}

    //  metodo per mostrare i DialogPane ed effettuare operazioni nel momento in cui si cliccano i btn associati ad essa
    private <T> void showDialog(String fxmlFile, String title, Consumer<T> onOkAction) {
        try {
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
    public void onLibraryChanged() { // metodo ricavato da LibraryObserver per il pattern Observer
        updateTracksTable();
    }

    private void updateTracksTable() {
        // Recupera le tracce e le inserisce nella tabella
        tracksTableView.getItems().setAll(ConcreteLibrary.getInstance().getTracks());

    }
}