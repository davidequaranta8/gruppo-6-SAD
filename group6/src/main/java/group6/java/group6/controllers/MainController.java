package group6.java.group6.controllers;

import java.util.List;

import group6.java.group6.dao.PlaylistDao;
import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Library;
import group6.java.group6.models.LibraryObserver;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.PlaylistObserver;
import group6.java.group6.models.Track;
import group6.java.group6.services.PlayerService;
import group6.java.group6.services.TrackService;
import group6.java.group6.utils.CommandInvoker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


// Questa classe è il concreteObserver per il pattern Observer applicato con Library e Playlist.
// Funge da thin coordinator: delega la logica ai vari Helper.
public class MainController implements LibraryObserver, PlaylistObserver {

    // ── Utils ─────────────────────────────────────────────────────────
    private final TrackDao trackDao = new TrackDao();
    private final CommandInvoker invoker = new CommandInvoker();
    private final TrackService trackService = new TrackService();
    private final PlaylistDao playlistDao = new PlaylistDao();
    public Button mostPlayedTracksButton;
    private PlayerService playerService;

    // ── State pattern ─────────────────────────────────────────────────
    private MainViewContext viewContext;

    // ── Helper instances ──────────────────────────────────────────────
    private DialogHelper dialogHelper;
    private PlaybackHelper playbackHelper;
    private PlaylistHelper playlistHelper;
    private TrackHelper trackHelper;
    private FilterHelper filterHelper;
    private AutoPlaylistHelper autoPlaylistHelper;

    // ── Top bar ──────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private Button mostPlayedPlaylistButton;
    @FXML private Button undoBtn;

    // ── Sidebar sinistra ─────────────────────────────────────────────
    @FXML private ListView<String> playlistListView;
    @FXML private ListView<String> autoPlaylistListView;
    @FXML private Button newPlaylistBtn;
    @FXML private Button generateByGenreBtn;
    @FXML private Button generateByYearBtn;

    // ── Barra azioni playlist ────────────────────────────────────────
    @FXML private Button RenamePlaylist;
    @FXML private Button deletePlaylistBtn;
    @FXML private Button addToPlaylistBtn;

    // ── Titolo playlist attiva ───────────────────────────────────────
    @FXML private Label playlistTitleLabel;

    // ── Filtri ───────────────────────────────────────────────────────
    @FXML private HBox filterBar;
    @FXML private ComboBox<GenreEnum> genreFilter;
    @FXML private ComboBox<String> yearFilter;
    @FXML private Button addTrackBtn;

    // ── Tabella tracce ───────────────────────────────────────────────
    @FXML private TableView<Track> tracksTableView;
    @FXML private TableColumn<Track, Integer> colNow;
    @FXML private TableColumn<Track, String> colTitle;
    @FXML private TableColumn<Track, String> colAuthor;
    @FXML private TableColumn<Track, GenreEnum> colGenre;
    @FXML private TableColumn<Track, Double> colLength;

    // ── Sezione home ─────────────────────────────────────────────────
    @FXML private javafx.scene.layout.VBox homeSectionBox;
    @FXML private ListView<String> topTracksListView;

    // ── Player bar ───────────────────────────────────────────────────
    @FXML private javafx.scene.layout.VBox playerBar;
    @FXML private Label currentTimeLabel;
    @FXML private Label currentTitle;
    @FXML private Label currentPlaylist;
    @FXML private Label currentAuthor;
    @FXML private Slider progressSlider;
    @FXML private Label totalTimeLabel;
    @FXML private ToggleButton shuffleToggleBtn;
    @FXML private ToggleButton loopBtn;
    @FXML private Button prevBtn;
    @FXML private Button playPauseBtn;
    @FXML private Button nextBtn;
    @FXML private Label nowPlayingTitle;
    @FXML private Label nowPlayingAuthor;

    // ── Pannello dettaglio ────────────────────────────────────────────
    @FXML private javafx.scene.layout.VBox detailPanel;
    @FXML private Label detailTitle;
    @FXML private Label detailAuthor;
    @FXML private Label detailGenre;
    @FXML private Label detailYear;
    @FXML private Label detailLength;
    @FXML private Label curretTimeLabel;
    @FXML private Label detailTag;
    @FXML private Label tagLabel;
    @FXML private javafx.scene.control.CheckBox tagFavourite;
    @FXML private javafx.scene.control.CheckBox tagExplicit;
    @FXML private javafx.scene.control.CheckBox tagNewRelease;
    @FXML private Button editTrackBtn;
    @FXML private Button removeFromPlaylistBtn;
    @FXML private Button deleteTrackBtn;


    // ═════════════════════════════════════════════════════════════════
    //  INITIALIZE
    // ═════════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        // State pattern
        viewContext = new MainViewContext(filterBar, addTrackBtn, addToPlaylistBtn,
                deleteTrackBtn, removeFromPlaylistBtn, RenamePlaylist, playlistTitleLabel, undoBtn);
        viewContext.setState(MainViewContext.LIBRARY_STATE);

        // Generi nella combo
        genreFilter.getItems().setAll(GenreEnum.values());

        // Player service
        playerService = new PlayerService(trackService, currentTimeLabel, totalTimeLabel,
                progressSlider, playPauseBtn, currentTitle, currentAuthor, currentPlaylist);
        playerService.setOnTrackEnd(() -> handleNext());

        // Inizializza la tabella
        initTable();

        // ── Crea gli helper ──
        dialogHelper = new DialogHelper();

        playbackHelper = new PlaybackHelper(playerService, playlistDao,
                tracksTableView, currentTitle, currentAuthor);

        playlistHelper = new PlaylistHelper(dialogHelper, playerService, playlistDao,
                invoker, tracksTableView, playlistListView, playlistTitleLabel,
                currentTitle, currentAuthor, viewContext);

        trackHelper = new TrackHelper(dialogHelper, trackService, trackDao, playerService,
                tracksTableView, playPauseBtn,
                detailTitle, detailAuthor, detailGenre, detailYear, detailLength,
                detailTag, tagLabel, totalTimeLabel, currentTitle, currentAuthor);

        filterHelper = new FilterHelper(trackDao, genreFilter, yearFilter, tracksTableView, searchField);

        autoPlaylistHelper = new AutoPlaylistHelper(dialogHelper);

        // ── Observer ──
        Library myLibrary = ConcreteLibrary.getInstance();
        myLibrary.addObserver(this);
        updateTracksTable();

        PlaylistManager.getInstance().addObserver(this);
        playlistHelper.updatePlaylistSidebar();
        filterHelper.initFilters();
    }

    private void initTable() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
    }


    // ═════════════════════════════════════════════════════════════════
    //  HANDLERS — ogni metodo delega all'helper appropriato
    // ═════════════════════════════════════════════════════════════════

    @FXML
    protected void handleUndo() {
        boolean success = invoker.undoLastCommand();
        if (!success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Annulla operazione");
            alert.setHeaderText(null);
            alert.setContentText("Non ci sono operazioni recenti da annullare.");
            alert.showAndWait();
        }
    }

    // ── Playlist ─────────────────────────────────────────────────────

    @FXML
    protected void handleNewPlaylist() {
        playlistHelper.handleNewPlaylist();
    }

    @FXML
    protected void handleShowTopPlayedPlaylist() {
        playlistHelper.handleShowTopPlayedPlaylist();
    }

    @FXML
    protected void handleRenamePlaylist() {
        playlistHelper.handleRenamePlaylist(playbackHelper);
    }

    @FXML
    protected void handleDeletePlaylist() {
        playlistHelper.handleDeletePlaylist(playbackHelper, this::handleShowAllTracks);
    }

    @FXML
    protected void handleAddToPlaylist() {
        playlistHelper.handleAddToPlaylist(playbackHelper::syncQueue);
        filterHelper.initFilters();
    }

    @FXML
    protected void handleRemoveFromPlaylist() {
        playlistHelper.handleRemoveFromPlaylist(playbackHelper::syncQueue);
    }

    @FXML
    protected void handleSidebarClick(MouseEvent event) {
        playlistHelper.handleSidebarClick(event);
    }

    @FXML
    protected void handleShowAllTracks() {
        playlistHelper.handleShowAllTracks(mostPlayedTracksButton, mostPlayedPlaylistButton,
                () -> filterHelper.handleResetFilter());
    }

    // ── Tracce ───────────────────────────────────────────────────────

    @FXML
    protected void handleAddTrack() {
        trackHelper.handleAddTrack();
        filterHelper.initFilters();
    }

    @FXML
    protected void handleEditTrack() {
        trackHelper.handleEditTrack(playbackHelper::syncQueue);
        filterHelper.initFilters();
    }

    @FXML
    protected void handleDeleteTrack() {
        trackHelper.handleDeleteTrack(playbackHelper::syncQueue);
    }

    @FXML
    protected void handleTrackSelected() {
        trackHelper.showTrackDetails(tracksTableView.getSelectionModel().getSelectedItem());
    }

    // ── Filtri ───────────────────────────────────────────────────────

    @FXML
    protected void handleSearch() {
        filterHelper.handleSearch();
    }

    @FXML
    protected void handleFilter() {
        filterHelper.handleFilter();
        playbackHelper.syncQueue();
    }

    @FXML
    protected void handleResetFilter() {
        filterHelper.handleResetFilter();
        playbackHelper.syncQueue();
    }

    // ── Player ───────────────────────────────────────────────────────

    @FXML
    protected void handlePlayAll() {
        playbackHelper.playAll();
    }

    @FXML
    protected void handleShuffle() {
        playbackHelper.handleShuffle(shuffleToggleBtn, loopBtn);
    }

    @FXML
    protected void handleLoop() {
        playbackHelper.handleLoop(loopBtn, shuffleToggleBtn);
    }

    @FXML
    protected void handlePlayPause() {
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
        playbackHelper.handlePlayPause(selectedTrack);
        playbackHelper.syncQueue();
        trackHelper.showTrackDetails(selectedTrack);
        if (playbackHelper.getActivePlaylist() == null) {
            filterHelper.handleResetFilter();
        }
    }

    @FXML
    protected void handleNext() {
        playbackHelper.next();
    }

    @FXML
    protected void handlePrev() {
        playbackHelper.prev();
    }

    @FXML
    public void handleSeekTrack(MouseEvent mouseEvent) {
        playerService.seekTrack();
    }

    // ── Generazione automatica ───────────────────────────────────────

    @FXML
    protected void handleGeneratePlaylist() {
        autoPlaylistHelper.handleGeneratePlaylist();
    }

    // ── Top played tracks ────────────────────────────────────────────

    @FXML
    protected void handleShowTopPlayedTracks(ActionEvent event) {
        List<Track> topTracks = trackDao.getTopPlayedTracks(10);
        tracksTableView.getItems().setAll(topTracks);
        playlistTitleLabel.setText("Brani più ascoltati");
        playlistTitleLabel.setVisible(true);
        playlistTitleLabel.setManaged(true);
    }


    // ═════════════════════════════════════════════════════════════════
    //  OBSERVER CALLBACKS
    // ═════════════════════════════════════════════════════════════════

    @Override
    public void onLibraryChanged() {
        Playlist selectedPlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
        if (selectedPlaylist != null) {
            playlistHelper.showPlaylistContent(selectedPlaylist);
        } else {
            updateTracksTable();
        }
        playbackHelper.syncQueue();
    }

    @Override
    public void onPlaylistChanged() {
        playlistHelper.updatePlaylistSidebar();
        Playlist selectedPlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
        if (selectedPlaylist != null) {
            playlistHelper.showPlaylistContent(selectedPlaylist);
        } else {
            viewContext.setState(MainViewContext.LIBRARY_STATE);
        }
        playbackHelper.syncQueue();
    }


    // ═════════════════════════════════════════════════════════════════
    //  METODI PRIVATI DI SUPPORTO
    // ═════════════════════════════════════════════════════════════════

    private void updateTracksTable() {
        tracksTableView.getItems().setAll(ConcreteLibrary.getInstance().getTracks());
    }
}