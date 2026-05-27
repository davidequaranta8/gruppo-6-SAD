package group6.java.group6.controllers;

import group6.java.group6.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

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
    @FXML private TableView<String> tracksTableView;
    @FXML private TableColumn<String, String> colNow;
    @FXML private TableColumn<String, String> colTitle;
    @FXML private TableColumn<String, String> colAuthor;
    @FXML private TableColumn<String, String> colGenre;
    @FXML private TableColumn<String, String> colYear;
    @FXML private TableColumn<String, String> colLength;
    @FXML private TableColumn<String, String> colTags;
    @FXML private TableColumn<String, String> colActions;

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
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HANDLERS
    // ═════════════════════════════════════════════════════════════════════════

    @FXML protected void handleUndo() {}

    // ── Playlist ──────────────────────────────────────────────────────────────
    @FXML
    protected void handleNewPlaylist() {
        showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Nuova Playlist");
    }

    @FXML
    protected void handleGenerateByGenre() {
        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist da Genere");
    }

    @FXML
    protected void handleGenerateByYear() {
        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist da Anno");
    }

    @FXML
    protected void handleRenamePlaylist() {
        showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Rinomina Playlist");
    }

    @FXML protected void handleDeletePlaylist() {}

    // ── Tracce ────────────────────────────────────────────────────────────────
    @FXML
    protected void handleAddTrack() {
        showDialog("TrackDialog.fxml", "Aggiungi Traccia");
    }

    @FXML
    protected void handleEditTrack() {
        showDialog("TrackDialog.fxml", "Modifica Traccia");
    }

    @FXML protected void handleDeleteTrack() {}
    @FXML protected void handleRemoveFromPlaylist() {}

    // ── Filtri ────────────────────────────────────────────────────────────────
    @FXML protected void handleFilter() {}
    @FXML protected void handleResetFilter() {}

    // ── Player ────────────────────────────────────────────────────────────────
    @FXML protected void handlePlayAll() {}
    @FXML protected void handleShuffle() {}
    @FXML protected void handlePrev() {}
    @FXML protected void handlePlayPause() {}
    @FXML protected void handleNext() {}

    // ── Tag ───────────────────────────────────────────────────────────────────
    @FXML protected void handleTagChange() {}

    // ═════════════════════════════════════════════════════════════════════════
    //  UTILITY
    // ═════════════════════════════════════════════════════════════════════════
    private void showDialog(String fxmlFile, String title) {
        try {
            var url = HelloApplication.class.getResource(fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            DialogPane dialogPane = fxmlLoader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(title);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}