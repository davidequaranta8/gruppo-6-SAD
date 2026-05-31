package group6.java.group6.controllers;

import java.io.IOException;
import java.util.Optional;

import group6.java.group6.HelloApplication;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Library;
import group6.java.group6.models.LibraryFacade;
import group6.java.group6.models.LibraryObserver;
import group6.java.group6.models.Track;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

// questa classe rappresenta il concreteObserver per il pattern Observer applicato con Library
public class MyMainController implements LibraryObserver{

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
    @FXML private TableColumn<Track, String> colGenre;
    @FXML private TableColumn<Track, String> colYear;
    @FXML private TableColumn<Track, String> colLength;
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

    private Track selectedTrack;
    
    private final LibraryFacade facade = LibraryFacade.getInstance();
    
    
    
    
    @FXML
    public void initialize() {
        
        if (addTrackBtn != null) 
            addTrackBtn.setDisable(false);
        if (RenamePlaylist != null)
             RenamePlaylist.setDisable(false);
        
        if (editTrackBtn != null)
            editTrackBtn.setDisable(true); 
        if (deleteTrackBtn != null)
             deleteTrackBtn.setDisable(true); 

        configureTableColumns();

        configureTableSelection();

        // registrazione l'observer
        Library myLibrary = ConcreteLibrary.getInstance();
        myLibrary.addObserver(this); // inserisco l'observer nella lista degli osservatori da aggiornare
        updateTracksTable(); // effettuo uno primo aggiornamento della tabella

    }
        
    //  HANDLERS
    
    @FXML protected void handleUndo() {}

    // Playlist
    
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
    
    @FXML protected void handleGeneratePlaylist() {}
    
    // Tracce
    @FXML
    protected void handleAddTrack() {
        showConfiguredDialog("TrackDialog.fxml", "Aggiungi Traccia", null);
        /*try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("TrackDialog.fxml"));
            DialogPane dialogPane = loader.load();
            TrackDialogController controllerTrackDialog = loader.getController();

            Dialog<Track> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Aggiungi Traccia");

            dialog.setResultConverter(buttonType -> {
                if (buttonType != null &&
                        buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return new Track(
                            controllerTrackDialog.getTitle(),
                            controllerTrackDialog.getAuthor(),
                            controllerTrackDialog.getLength(),
                            controllerTrackDialog.getGenre(),
                            controllerTrackDialog.getYear(),

                    );
                }
                return null;
            });

            dialog.showAndWait().ifPresent(track -> {
                ConcreteLibrary.getInstance().addTrack(track); // notifica Observer automaticamente
            });

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @FXML
    protected void handleEditTrack() {
        if (selectedTrack == null) {
            showWarning("Nessuna traccia selezionata",
                        "Seleziona una traccia dalla tabella prima di modificarla.");
            return;
        }
        showConfiguredDialog("TrackDialog.fxml", "Modifica Traccia", selectedTrack);
        updateDetailPanel(selectedTrack);
    }

    @FXML protected void handleDeleteTrack() {

        if (selectedTrack == null) {
            showWarning("Nessuna traccia selezionata",
                    "Seleziona una traccia dalla tabella prima di eliminarla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Elimina traccia");
        confirm.setHeaderText("Eliminare \"" + selectedTrack.getTitle() + "\"?");
        confirm.setContentText("L'operazione non può essere annullata.");
    
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            facade.removeTrack(selectedTrack);
            selectedTrack = null;
            updateDetailPanel(null);
        }

    }
    
    
    @FXML protected void handleRemoveFromPlaylist() {}

    @FXML protected void handleFilter() {}
    @FXML protected void handleResetFilter() {}

    @FXML protected void handlePlayAll() {}
    @FXML protected void handleShuffle() {}
    @FXML protected void handlePrev() {}
    @FXML protected void handlePlayPause() {}
    @FXML protected void handleNext() {}

    @FXML protected void handleTagChange() {}




    private void configureTableColumns() {
        if (tracksTableView == null) return;


        // collegamento tra le colonne e gli attributi della classe Track
        if (colTitle != null) colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (colAuthor != null) colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        if (colYear != null) colYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        // Colonne che richiedono una trasformazione del valore
        if (colGenre != null) {
            colGenre.setCellValueFactory(cell -> {
                Track track = cell.getValue();
                return new SimpleStringProperty(track != null && track.getGenre() != null 
                        ? track.getGenre().name() : "");
            });
        }

        if (colLength != null) {
            colLength.setCellValueFactory(cell -> {
                Track track = cell.getValue();
                return new SimpleStringProperty(track != null ? formatLength(track.getLength()) : "");
            });
        }

        if (colTags != null) {
            colTags.setCellValueFactory(cell -> {
                Track track = cell.getValue();
                return new SimpleStringProperty(track != null && track.getTag() != null 
                        ? track.getTag().name() : "");
            });
        }
    }

    private void configureTableSelection() {
        if (tracksTableView == null) return;

        tracksTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        selectedTrack = newVal;
        
        // Aggiorna il pannello laterale dei dettagli
        updateDetailPanel(newVal);
        
        boolean trackIsSelected = (newVal != null);
        if (editTrackBtn != null) editTrackBtn.setDisable(!trackIsSelected);
        if (deleteTrackBtn != null) deleteTrackBtn.setDisable(!trackIsSelected);
        });
    }

    private void showConfiguredDialog(String fxmlFile, String title, Track track) {
        try {
            FXMLLoader loader = buildLoader(fxmlFile);
            DialogPane pane   = loader.load();

            TrackDialogController ctrl = loader.getController();
            ctrl.setFacade(facade);
            ctrl.setTrackToEdit(track);

            Button okButton = (Button) pane.getButtonTypes().stream()
                .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .findFirst()
                .map(pane::lookupButton)
                .orElse(null);

            if (okButton != null) {
                okButton.addEventFilter(ActionEvent.ACTION, event -> {
                    if (!ctrl.handleSave()) {
                        event.consume();
                    }
                });
            }

            showDialog(pane, title);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  private void showDialog(DialogPane pane, String title) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(pane);
        dialog.setTitle(title);
        dialog.showAndWait();
    }

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

     private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private FXMLLoader buildLoader(String fxmlFile) {
        return new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
    }

    //Aggiorno pannello affianco
    private void updateDetailPanel(Track track) {
        if (detailPanel != null) detailPanel.setVisible(track != null);
        if (track == null) return;
        if (detailTitle  != null) detailTitle.setText(track.getTitle());
        if (detailAuthor != null) detailAuthor.setText(track.getAuthor());
        if (detailGenre  != null) detailGenre.setText(
            track.getGenre() != null ? track.getGenre().name() : "");
        if (detailYear   != null) detailYear.setText(String.valueOf(track.getYear()));
        if (detailLength != null) detailLength.setText(formatLength(track.getLength()));
    }

    @Override
    public void onLibraryChanged() { // metodo ricavato da LibraryObserver per il pattern Observer
        updateTracksTable();
    }

    private void updateTracksTable() {
        // Recupera le tracce e le inserisce nella tabella
        tracksTableView.getItems().setAll(ConcreteLibrary.getInstance().getTracks());
    }

    private String formatLength(double seconds) {
        int total = (int) seconds;
        return String.format("%d:%02d", total / 60, total % 60);
    }
}