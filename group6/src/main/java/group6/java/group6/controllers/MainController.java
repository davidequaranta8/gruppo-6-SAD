package group6.java.group6.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {
    @FXML
    private TextField searchField;
    @FXML
    private Button undoBtn;
    @FXML
    private ListView<String> playlistListView;
    @FXML
    private ListView<String> autoPlaylistListView;
    @FXML
    private Button newPlaylistBtn;
    @FXML
    private Label playlistNameLabel;
    @FXML
    private Button playAllBtn;
    @FXML
    private Button shuffleBtn;
    @FXML
    private Button renamePlaylistBtn;
    @FXML
    private Button deletePlaylistBtn;
    @FXML
    private ComboBox<String> genreFilter;
    @FXML
    private ComboBox<String> yearFilter;
    @FXML
    private Button addTrackBtn;
    @FXML
    private TableView<String> tracksTableView;
    @FXML
    private TableColumn<String, String> colNow;
    @FXML
    private TableColumn<String, String> colTitle;
    @FXML
    private TableColumn<String, String> colAuthor;
    @FXML
    private TableColumn<String, String> colGenre;
    @FXML
    private TableColumn<String, String> colYear;
    @FXML
    private TableColumn<String, String> colLength;
    @FXML
    private TableColumn<String, String> colTags;
    @FXML
    private TableColumn<String, String> colActions;
    @FXML
    private VBox homeSectionBox;
    @FXML
    private ListView<String> topTracksListView;
    @FXML
    private VBox playerBar;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private ToggleGroup playModeGroup;
    @FXML
    private ToggleButton seqBtn;
    @FXML
    private ToggleButton shuffleToggleBtn;
    @FXML
    private ToggleButton loopBtn;
    @FXML
    private Button prevBtn;
    @FXML
    private Button playPauseBtn;
    @FXML
    private Button nextBtn;
    @FXML
    private Label nowPlayingTitle;
    @FXML
    private Label nowPlayingAuthor;
    @FXML
    private VBox detailPanel;
    @FXML
    private Label detailTitle;
    @FXML
    private Label detailAuthor;
    @FXML
    private Label detailGenre;
    @FXML
    private Label detailYear;
    @FXML
    private Label detailLength;
    @FXML
    private CheckBox tagFavourite;
    @FXML
    private CheckBox tagExplicit;
    @FXML
    private CheckBox tagNewRelease;
    @FXML
    private Button editTrackBtn;
    @FXML
    private Button removeFromPlaylistBtn;
    @FXML
    private Button deleteTrackBtn;

    @FXML
    public void initialize() {
        // Abilita i bottoni altrimenti bloccati per poter testare le viste
        if (addTrackBtn != null) addTrackBtn.setDisable(false);
        if (renamePlaylistBtn != null) renamePlaylistBtn.setDisable(false);
        if (editTrackBtn != null) editTrackBtn.setDisable(false);
    }

    @FXML
    protected void handleUndo() {}

    @FXML
    protected void handleNewPlaylist() {
        showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Nuova Playlist");
    }

    @FXML
    protected void handleGenerateByGenre() {
        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist Automatica");
    }



    @FXML
    protected void handleGenerateByYear() {
        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist Automatica");
    }

    @FXML
    protected void handleRenamePlaylist() {
        showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Rinomina Playlist");
    }

    @FXML
    protected void handleAddTrack() {
        showDialog("TrackDialog.fxml", "Aggiungi Traccia");
    }

    @FXML
    protected void handleEditTrack() {
        showDialog("TrackDialog.fxml", "Modifica Traccia");
    }

    private void showDialog(String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            DialogPane dialogPane = fxmlLoader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(title);

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handlePlayAll() {}
    @FXML
    protected void handleShuffle() {}
    @FXML
    protected void handleDeletePlaylist() {}
    @FXML
    protected void handleFilter() {}
    @FXML
    protected void handleResetFilter() {}
    @FXML
    protected void handlePrev() {}
    @FXML
    protected void handlePlayPause() {}
    @FXML
    protected void handleNext() {}
    @FXML
    protected void handleTagChange() {}
    @FXML
    protected void handleRemoveFromPlaylist() {}
    @FXML
    protected void handleDeleteTrack() {}

}