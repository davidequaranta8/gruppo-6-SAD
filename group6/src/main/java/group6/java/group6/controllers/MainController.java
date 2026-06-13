package group6.java.group6.controllers;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import group6.java.group6.services.PlayerService;
import group6.java.group6.utils.*;
import group6.java.group6.services.TrackService;
import javafx.event.ActionEvent;
import org.kordamp.ikonli.javafx.FontIcon;

import group6.java.group6.HelloApplication;
import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.exceptions.DuplicatePlaylistException;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Library;
import group6.java.group6.models.LibraryObserver;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.PlaylistObserver;
import group6.java.group6.models.Track;

import static group6.java.group6.utils.TimeUtils.formatTime;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



// questa classe rappresenta il concreteObserver per il pattern Observer applicato con Library
public class MainController implements LibraryObserver, PlaylistObserver {

    // ── Utils ─────────────────────────────────────────────────────────
    private final TrackDao trackDao = new TrackDao();
    private final CommandInvoker invoker = new CommandInvoker();
    private final TrackService trackService = new TrackService();
    private PlayerService playerService;

    // ── State pattern ─────────────────────────────────────────────────────────
    private MainViewContext viewContext;

    // ── Top bar ──────────────────────────────────────────────────────────────
    @FXML
    private TextField searchField;
    @FXML
    private Button undoBtn;

    // ── Sidebar sinistra ─────────────────────────────────────────────────────
    @FXML
    private ListView<String> playlistListView;
    @FXML
    private ListView<String> autoPlaylistListView;
    @FXML
    private Button newPlaylistBtn;
    @FXML
    private Button generateByGenreBtn;   // mancava
    @FXML
    private Button generateByYearBtn;    // mancava

    // ── Barra azioni playlist ────────────────────────────────────────────────
    // Nel FXML il bottone Rinomina ha fx:id="RenamePlaylist"
    @FXML
    private Button RenamePlaylist;       // nome esatto come nel FXML
    @FXML
    private Button deletePlaylistBtn;
    @FXML
    private Button addToPlaylistBtn;

    // ── Titolo playlist attiva (sopra la tabella) ─────────────────────────────
    @FXML
    private Label playlistTitleLabel;

    // ── Filtri ───────────────────────────────────────────────────────────────
    @FXML
    private HBox filterBar;
    @FXML
    private ComboBox<GenreEnum> genreFilter;
    @FXML
    private ComboBox<String> yearFilter;
    @FXML
    private Button addTrackBtn;

    // ── Tabella tracce ────────────────────────────────────────────────────────
    @FXML
    private TableView<Track> tracksTableView;
    @FXML
    private TableColumn<Track, Integer> colNow;
    @FXML
    private TableColumn<Track, String> colTitle;
    @FXML
    private TableColumn<Track, String> colAuthor;
    @FXML
    private TableColumn<Track, GenreEnum> colGenre;
    @FXML
    private TableColumn<Track, Double> colLength;

    // ── Sezione home ──────────────────────────────────────────────────────────
    @FXML
    private VBox homeSectionBox;
    @FXML
    private ListView<String> topTracksListView;

    // ── Player bar ────────────────────────────────────────────────────────────
    @FXML
    private VBox playerBar;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label currentTitle;
    @FXML
    private Label currentAuthor;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label totalTimeLabel;
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

    // ── Pannello dettaglio ────────────────────────────────────────────────────
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
    private Label curretTimeLabel;
    @FXML
    private Label detailTag;
    @FXML
    private Label tagLabel;
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

    // ── Stato runtime ─────────────────────────────────────────────────────────
    private Track currentPlayingTrack = null;

    // Rappresenta la coda di riproduzione corrente in modo tale da poter salvare le tracce da dover riprodurre
    // e poter navigare liberamente
    private List<Track> playbackQueue = new ArrayList<>();


    // ═════════════════════════════════════════════════════════════════════════
    //  INITIALIZE
    // ═════════════════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        viewContext = new MainViewContext(filterBar, addTrackBtn, addToPlaylistBtn, deleteTrackBtn, removeFromPlaylistBtn, RenamePlaylist, playlistTitleLabel);
        viewContext.setState(MainViewContext.LIBRARY_STATE);
        // tramite questa istruzione mostriamo nella tendina dei generi musicali quelli della enumerazione
        genreFilter.getItems().setAll(GenreEnum.values());
        //istanziamo il service per il player che nel costruttore fa il setup delle callback
        playerService = new PlayerService(trackService , currentTimeLabel , totalTimeLabel ,progressSlider , playPauseBtn , currentTitle , currentAuthor);

        // passo la funzione handleNext al service cosi quando finisce la riproduzione esegue handleNext()
        playerService.setOnTrackEnd(() -> handleNext());

        // collegamento tra le colonne e gli attributi della classe Track
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));

        // registrazione dell'observer
        Library myLibrary = ConcreteLibrary.getInstance();
        myLibrary.addObserver(this); // inserisco l'observer nella lista degli osservatori da aggiornare
        updateTracksTable(); // effettuo uno primo aggiornamento della tabella

        //observer per playlist
        PlaylistManager.getInstance().addObserver(this);
        // effettuo uno primo aggiornamento della sidebar per caricare eventuali playlist già presenti
        updatePlaylistSidebar();


    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HANDLERS
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    protected void handleUndo() {
        boolean success = invoker.undoLastCommand();

        // Se la pila è vuota
        if (!success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Annulla operazione");
            alert.setHeaderText(null);
            alert.setContentText("Non ci sono operazioni recenti da annullare.");
            alert.showAndWait();
        }
    }

    // ── Playlist ──────────────────────────────────────────────────────────────
    @FXML
    protected void handleNewPlaylist() {
        showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Nuova Playlist", (PlaylistDialogController ctrl) -> {
            if (!ctrl.validate()) {
                ctrl.showValidationError();
                return;
            }
            try {
                PlaylistManager.getInstance().createPlaylist(ctrl.getName());
            } catch (DuplicatePlaylistException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Playlist duplicata");
                alert.setContentText("Hai giá una playlist con nome: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    @FXML
    protected void handleGenerateByGenre() {
        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist da Genere", null);
    }

    @FXML
    protected void handleGenerateByYear() {
        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist da Anno", null);
    }

    @FXML
    protected void handleRenamePlaylist() {
        Playlist selected = PlaylistManager.getInstance().getSelectedPlaylist();
        if (selected == null)
            return;
        showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Rinomina Playlist",
                (PlaylistDialogController ctrl) -> {
                    if (!ctrl.validate()) {
                        ctrl.showValidationError();
                        return;
                    }
                    try {
                        PlaylistManager.getInstance().renamePlaylist(selected, ctrl.getName());
                    } catch (DuplicatePlaylistException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Errore");
                        alert.setHeaderText("Playlist duplicata");
                        alert.setContentText("Hai giá una playlist con nome: " + e.getMessage());
                        alert.showAndWait();
                    }
                });
    }

    @FXML
    protected void handleDeletePlaylist() {
        Playlist selected = PlaylistManager.getInstance().getSelectedPlaylist();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText("Eliminazione playlist");
        confirm.setContentText("Eliminare \"" + selected.getTitle() + "\"?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                PlaylistManager.getInstance().deletePlaylist(selected);
                handleShowAllTracks(); //torno alla libreria originale
            }
        });
    }


    @FXML
    protected void handleAddTrack() {
        // definiamo il metodo accept() del Consumer
        showDialog("TrackDialog.fxml", "Aggiungi Traccia", (TrackDialogController controller) -> {
            if (!controller.validate(false)) {
                controller.showValidationError();
                return;
            }

            saveTrackFromDialog(controller);
        });
    }

    @FXML
    protected void handleEditTrack() {
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
        if (selectedTrack == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modifica Traccia");
            alert.setHeaderText("Nessuna traccia selezionata");
            alert.setContentText("Seleziona una traccia dalla tabella.");
            alert.showAndWait();
            return;
        }
        showEditTrackDialog("TrackDialog.fxml", "Modifica Traccia", selectedTrack, (controller) -> {

            if (!controller.validate(true)) {
                controller.showValidationError();
                return;
            }

            //before to update the model check first if user edited title or author in order to have a duplicate record
            if (trackDao.existsByAuthorAndTitleAndId(controller.getAuthor(), controller.getTitle(), selectedTrack.getId())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Errore");
                alert.setContentText("Esiste giá una traccia con titolo " + controller.getTitle() + " e autore " + controller.getAuthor());
                alert.showAndWait();
                return; //avoid to continue updating
            }
            //update model
            selectedTrack.updateTrack(controller.getTitle(), controller.getAuthor(), controller.getGenre(), controller.getYear(), controller.getTag());

            //Se aggiorniamo un file audio di una tracci che é attualmente in riproduzione
            if (controller.getSelectedFile() != null) {
                playerService.stopAndClearIfPlaying(selectedTrack);
            }
            //update db and file (if needed)
            trackService.updateTrack(selectedTrack, controller.getSelectedFile());
            showTrackDetails(selectedTrack); // aggiorna il pannello di dettaglio con i nuovi dati della traccia
        });
    }


    @FXML
    protected void handleDeleteTrack() {
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();

        // alert per la richiesta di conferma dell'eliminazione della traccia
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Conferma eliminazione");
        confirmation.setHeaderText("Eliminazione traccia");
        confirmation.setContentText(
                "Sei sicuro di voler eliminare la traccia \"" +
                        selectedTrack.getTitle() + "\"?"
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            playerService.stopAndClearIfPlaying(selectedTrack);
            trackService.deleteTrack(selectedTrack);
            clearDetails(); //clear all details aside to avoid inconsistencies
            FontIcon icon = (FontIcon) playPauseBtn.getGraphic();
            icon.setIconLiteral("fas-play");icon.setIconLiteral("fas-play");
        }
        showTrackDetails(null); // svuota il pannello di dettaglio dopo l'eliminazione
    }

    @FXML
    protected void handleAddToPlaylist() {
        Playlist playlist = PlaylistManager.getInstance().getSelectedPlaylist();
        if (playlist == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aggiungi a Playlist");
            alert.setHeaderText("Nessuna playlist selezionata");
            alert.setContentText("Seleziona una playlist dalla barra laterale.");
            alert.showAndWait();
            return;
        }

        List<Track> availableTracks = ConcreteLibrary.getInstance().getTracks().stream()
                .filter(t -> !playlist.getTracks().contains(t))
                .collect(Collectors.toList());

        if (availableTracks.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aggiungi a Playlist");
            alert.setHeaderText("Nessuna traccia disponibile");
            alert.setContentText("Tutte le tracce sono già nella playlist \"" + playlist.getTitle() + "\".");
            alert.showAndWait();
            return;
        }

        Map<String, Track> trackMap = new LinkedHashMap<>();
        for (Track t : availableTracks) {
            trackMap.put(t.getTitle() + " - " + t.getAuthor(), t);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(trackMap.keySet().iterator().next(), trackMap.keySet());
        dialog.setTitle("Aggiungi a Playlist");
        dialog.setHeaderText("Aggiungi traccia a \"" + playlist.getTitle() + "\"");
        dialog.setContentText("Traccia:");

        dialog.showAndWait().ifPresent(key -> {
            Track trackToAdd = trackMap.get(key);

            // Crei il comando e lo passi all'Invoker
            Command addCmd = new AddTrackCommand(trackToAdd,playlist);
            invoker.executeCommand(addCmd);
        });
    }

    @FXML
    protected void handleRemoveFromPlaylist() {
        Playlist playlist = PlaylistManager.getInstance().getSelectedPlaylist();
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
        if (playlist == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Rimuovi dalla Playlist");
            alert.setHeaderText("Nessuna playlist selezionata");
            alert.showAndWait();
            return;
        }
        if (selectedTrack == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Rimuovi dalla Playlist");
            alert.setHeaderText("Nessuna traccia selezionata");
            alert.setContentText("Seleziona una traccia dalla tabella.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Rimuovi dalla Playlist");
        confirm.setHeaderText("Rimuovere \"" + selectedTrack.getTitle() + "\" dalla playlist?");
        confirm.setContentText("La traccia rimarrà nella libreria.");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {

                // Creiamo il comando concreto per la rimozione
                Command removeCmd = new RemoveTrackCommand(selectedTrack,playlist);

                // Lo passiamo all'Invoker che lo esegue e lo salva nello storico
                invoker.executeCommand(removeCmd);

            }
        });
    }

    @FXML
    protected void handleFilter() {
    }

    @FXML
    protected void handleResetFilter() {
    }

    // collegato al tasto Riproduci, permette di riprodurre la collezione di tracce visualizzata nella TableView
    @FXML
    protected void handlePlayAll() {
        // Preleviamo tutte le tracce attualmente visibili (la playlist intera)
        List<Track> currentList = tracksTableView.getItems();

        if (currentList == null || currentList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Riproduzione");
            alert.setHeaderText("Nessuna traccia da riprodurre");
            alert.showAndWait();
            return;
        }

        // Facciamo la fotografia dell'intera lista salvandola nella coda
        playbackQueue = new ArrayList<>(currentList);

        // Preleviamo la primissima traccia (indice 0)
        Track firstTrack = playbackQueue.get(0);

        // Sfruttiamo il metodo che abbiamo creato prima per avviarla
        changeTrack(firstTrack);
    }

    @FXML
    protected void handleShuffle() {
    }




    @FXML
    protected void handleTrackSelected() {
        showTrackDetails(tracksTableView.getSelectionModel().getSelectedItem());
    }


    @FXML
    protected void handlePlayPause() {
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
        playerService.handlePlayPause(selectedTrack);
    }

    // Handler per riprodurre la traccia successiva
    @FXML
    protected void handleNext() {
        Track currentPlayingTrack = playerService.getCurrentPlayingTrack();
        if (currentPlayingTrack == null || playbackQueue == null || playbackQueue.isEmpty()) return;
        // Cerchiamo la traccia nella CODA
        int currentIndex = playbackQueue.indexOf(currentPlayingTrack);
        if (currentIndex == -1) return;
        // Prelevo la traccia successiva tramite l'index
        if (currentIndex < playbackQueue.size() - 1) {
            Track nextTrack = playbackQueue.get(currentIndex + 1);
            changeTrack(nextTrack);
        } else { // Se sono all'ultima track fermo la riproduzione
            stopPlayback();
        }
    }

    // Handler per riprodurre la traccia precedente
    @FXML
    protected void handlePrev() {
        Track currentPlayingTrack = playerService.getCurrentPlayingTrack();
        if (currentPlayingTrack == null || playbackQueue == null || playbackQueue.isEmpty()) return;
        // Cerchiamo la traccia nella CODA
        int currentIndex = playbackQueue.indexOf(currentPlayingTrack);
        if (currentIndex > 0) {
            Track prevTrack = playbackQueue.get(currentIndex - 1);
            changeTrack(prevTrack);
        }
    }

    private void changeTrack(Track newTrack) {
        // Troviamo il numero della riga (l'indice) che corrisponde alla canzone
        int index = tracksTableView.getItems().indexOf(newTrack);

        if (index >= 0) {
            tracksTableView.getSelectionModel().select(index);
            tracksTableView.scrollTo(index);
            showTrackDetails(newTrack);
        }

        playerService.changeTrack(newTrack);
    }
    private void stopPlayback() {
        // Usiamo il metodo stopAndClearIfPlaying del service, passandogli la traccia corrente
        playerService.stopAndClearIfPlaying(playerService.getCurrentPlayingTrack());
    }



    @FXML
    protected void handleTagChange() {
    }

    @FXML
    protected void handleGeneratePlaylist() {
        if (ConcreteLibrary.getInstance().getTracks().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Libreria vuota");
            alert.setHeaderText("Nessuna traccia disponibile");
            alert.setContentText("La libreria è vuota. Aggiungi delle tracce prima di generare una playlist.");
            alert.showAndWait();
            return;
        }

        showDialog("AutoPlaylistDialog.fxml", "Genera Playlist Automatica",
                (AutoPlaylistDialogController ctrl) -> ctrl.setName("Playlist Automatica"),
                (AutoPlaylistDialogController ctrl) -> {
                    if (ctrl.isTagSelected()) {
                        generateByTag(ctrl);
                    } else if (ctrl.isYearSelected()) {
                        generateByYear(ctrl);
                    } else if (ctrl.isGenreSelected()) {
                        generateByGenre(ctrl);
                    }
                });
    }

    private void generateByTag(AutoPlaylistDialogController ctrl) {
        List<TagEnum> selectedTags = ctrl.getSelectedTags();
        if (selectedTags.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessun tag selezionato");
            alert.setHeaderText("Seleziona almeno un tag");
            alert.setContentText("Devi selezionare almeno un tag per generare la playlist.");
            alert.showAndWait();
            return;
        }

        String name = ctrl.getPlaylistName();
        if (name.isEmpty()) {
            name = selectedTags.stream()
                    .map(TagEnum::name)
                    .collect(Collectors.joining(", "));
        }

        List<Track> matchingTracks = ConcreteLibrary.getInstance().getTracks().stream()
                .filter(t -> t.getTag() != null && selectedTags.contains(t.getTag()))
                .collect(Collectors.toList());

        if (matchingTracks.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generazione Playlist");
            alert.setHeaderText("Nessuna traccia trovata");
            alert.setContentText("Nessuna traccia nella libreria possiede i tag selezionati.");
            alert.showAndWait();
            return;
        }

        createPlaylistWithTracks(name, matchingTracks);
    }

    private void generateByYear(AutoPlaylistDialogController ctrl) {
        int yearFrom = Math.min(ctrl.getYearFrom(), ctrl.getYearTo());
        int yearTo = Math.max(ctrl.getYearFrom(), ctrl.getYearTo());

        String name = ctrl.getPlaylistName();
        if (name.isEmpty()) {
            if (yearFrom == yearTo) {
                name = String.valueOf(yearFrom);
            } else {
                name = yearFrom + " - " + yearTo;
            }
        }

        List<Track> matchingTracks = ConcreteLibrary.getInstance().getTracks().stream()
                .filter(t -> t.getYear() >= yearFrom && t.getYear() <= yearTo)
                .collect(Collectors.toList());

        if (matchingTracks.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generazione Playlist");
            alert.setHeaderText("Nessuna traccia trovata");
            alert.setContentText("Nessuna traccia nella libreria è stata pubblicata tra il " + yearFrom + " e il " + yearTo + ".");
            alert.showAndWait();
            return;
        }

        createPlaylistWithTracks(name, matchingTracks);
    }

    private void generateByGenre(AutoPlaylistDialogController ctrl) {
        GenreEnum selectedGenre = ctrl.getSelectedGenre();
        if (selectedGenre == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessun genere selezionato");
            alert.setHeaderText("Seleziona un genere");
            alert.setContentText("Devi selezionare un genere per generare la playlist.");
            alert.showAndWait();
            return;
        }

        String name = ctrl.getPlaylistName();
        if (name.isEmpty()) {
            name = selectedGenre.name();
        }

        List<Track> matchingTracks = ConcreteLibrary.getInstance().getTracks().stream()
                .filter(t -> t.getGenre() == selectedGenre)
                .collect(Collectors.toList());

        if (matchingTracks.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generazione Playlist");
            alert.setHeaderText("Nessuna traccia trovata");
            alert.setContentText("Nessuna traccia nella libreria appartiene al genere " + selectedGenre.name() + ".");
            alert.showAndWait();
            return;
        }

        createPlaylistWithTracks(name, matchingTracks);
    }

    private void createPlaylistWithTracks(String name, List<Track> matchingTracks) {
        try {
            String finalName = name;
            Set<String> existingNames = PlaylistManager.getInstance().getPlaylists().stream()
                    .map(Playlist::getTitle)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            int suffix = 1;
            while (existingNames.contains(finalName.toLowerCase())) {
                finalName = name + " (" + suffix + ")";
                suffix++;
            }
            Playlist playlist = PlaylistManager.getInstance().createPlaylist(finalName);
            for (Track t : matchingTracks) {
                PlaylistManager.getInstance().addTrackToPlaylist(playlist, t);
            }
        } catch (DuplicatePlaylistException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Playlist duplicata");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Seleziona una playlist dalla sidebar e ne mostra il contenuto.
    @FXML
    protected void handleSidebarClick(javafx.scene.input.MouseEvent event) {
        String clickedName = playlistListView.getSelectionModel().getSelectedItem();

        if (clickedName != null) {
            Set<Playlist> tutteLePlaylist = PlaylistManager.getInstance().getPlaylists();
            Playlist selezionata = tutteLePlaylist.stream()
                    .filter(p -> p.getTitle().trim().equalsIgnoreCase(clickedName.trim()))
                    .findFirst()
                    .orElse(null);

            if (selezionata != null) {
                PlaylistManager.getInstance().setSelectedPlaylist(selezionata);
                showPlaylistContent(selezionata);
            }
        }
    }

    //Torna a mostrare tutte le tracce nella tabella
    @FXML
    protected void handleShowAllTracks() {
        PlaylistManager.getInstance().setSelectedPlaylist(null);
        playlistListView.getSelectionModel().clearSelection();
        updateTracksTable();
        viewContext.setState(MainViewContext.LIBRARY_STATE);
    }


    @FXML
    protected void handleShowTopPlayedTracks(ActionEvent event) {
        List<Track> topTracks = trackDao.getTopPlayedTracks(10);
        tracksTableView.getItems().setAll(topTracks);
        playlistTitleLabel.setText("Brani più ascoltati");
        playlistTitleLabel.setVisible(true);
        playlistTitleLabel.setManaged(true);
    }


    private <T> void showDialog(String fxmlFile, String title, Consumer<T> onOkAction) {
        showDialog(fxmlFile, title, null, onOkAction);
    }

    private <T> void showDialog(String fxmlFile, String title, Consumer<T> preShowAction, Consumer<T> onOkAction) {
        try {
            var url = HelloApplication.class.getResource(fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            DialogPane dialogPane = fxmlLoader.load();
            T controller = fxmlLoader.getController();

            if (preShowAction != null) {
                preShowAction.accept(controller);
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(title);

            dialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    if (onOkAction != null) {
                        onOkAction.accept(controller);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showEditTrackDialog(String fxmlFile, String title, Track track, Consumer<TrackDialogController> onOkAction) {
        try {
            // carico la scena del TrackDialog
            var url = HelloApplication.class.getResource(fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            DialogPane dialogPane = fxmlLoader.load();

            // Recupera il controller TrackDialogController
            TrackDialogController controller = fxmlLoader.getController();

            // setto la scena del TrackDialog nel PopUp del DialogPane
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(title);

            controller.setAuthorField(track.getAuthor());
            controller.setGenreCombo(track.getGenre());
            controller.setTitleField(track.getTitle());
            controller.setFileNameLabel(track.getFilePath());
            controller.setToggleGroup(track.getTag());
            controller.setYearSpinner(track.getYear());

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

    @FXML
    public void handleSeekTrack(MouseEvent mouseEvent) {
        playerService.seekTrack();
    }


    @Override
    public void onLibraryChanged() {
        Playlist selectedPlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
        if (selectedPlaylist != null) {
            showPlaylistContent(selectedPlaylist);
        } else {
            updateTracksTable();
        }
    }


    @Override
    public void onPlaylistChanged() {
        updatePlaylistSidebar();
        Playlist selectedPlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
        if (selectedPlaylist != null) {
            showPlaylistContent(selectedPlaylist);
        } else {
            viewContext.setState(MainViewContext.LIBRARY_STATE);
        }
    }

    private void saveTrackFromDialog(TrackDialogController controller) {
        Track track;

        // Preleviamo i dati usando i getter del TrackDialogController
        if (controller.getOptionSelected() != null) {
            track = new Track(
                    controller.getTitle(),
                    controller.getAuthor(),
                    controller.getGenre(),
                    controller.getYear(),
                    TagEnum.valueOf(controller.getOptionSelected())
            );
        } else {
            track = new Track(
                    controller.getTitle(),
                    controller.getAuthor(),
                    controller.getGenre(),
                    controller.getYear()
            );
        }

        try {
            trackService.saveTrack(track ,controller.getSelectedFile());
        } catch (DuplicateTitleTrackException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Traccia duplicata");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }



    }


    private void updateTracksTable() {
        tracksTableView.getItems().setAll(ConcreteLibrary.getInstance().getTracks());
    }


    // Riempie il pannello di dettaglio con i dati della traccia selezionata
    private void showTrackDetails(Track track) {
        if (track == null) {            // nessuna traccia selezionata: svuota tutto
            clearDetails();
            return;
        }

        detailTitle.setText(track.getTitle());
        detailAuthor.setText(track.getAuthor());
        detailGenre.setText(track.getGenre() != null ? track.getGenre().toString() : "");
        detailYear.setText(String.valueOf(track.getYear()));
        detailLength.setText(formatTime(TimeUtils.parseFormattedDuration(track.getLength())));
        detailTag.setText(track.getTag() != null ? track.getTag().toString() : "");
        tagLabel.setText(track.getTag() != null ? "Tag" : "");

        totalTimeLabel.setText(formatTime(TimeUtils.parseFormattedDuration(track.getLength())));


    }


    private void updatePlaylistSidebar() {
        if (playlistListView == null) return;
        Playlist currentPlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
        String currentTitle = currentPlaylist != null ? currentPlaylist.getTitle() : null;
        List<String> names = PlaylistManager.getInstance().getPlaylists()
                .stream()
                .map(Playlist::getTitle)
                .collect(Collectors.toList());

        playlistListView.getItems().setAll(names);

        if (currentTitle != null && names.contains(currentTitle)) {
            playlistListView.getSelectionModel().select(currentTitle);
        }

    }

    private void showPlaylistContent(Playlist playlist) {
        tracksTableView.getSelectionModel().clearSelection();
        showTrackDetails(null);
        PlaylistManager.getInstance().loadTracksForPlaylist(playlist);
        tracksTableView.getItems().setAll(playlist.getTracks());
        playlistTitleLabel.setText(playlist.getTitle());
        viewContext.setState(MainViewContext.PLAYLIST_STATE);

    }


    private void clearDetails() {
        detailTitle.setText("");
        detailAuthor.setText("");
        detailGenre.setText("");
        detailLength.setText("");
        detailYear.setText("");
        detailTag.setText("");
        tagLabel.setText("");
        totalTimeLabel.setText("");

    }
}