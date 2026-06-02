package group6.java.group6.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.utils.TimeUtils;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.javafx.FontIcon;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import static group6.java.group6.utils.TimeUtils.formatTime;


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
    @FXML private Label curretTimeLabel;
    @FXML private Label detailTag;
    @FXML private CheckBox tagFavourite;
    @FXML private CheckBox tagExplicit;
    @FXML private CheckBox tagNewRelease;
    @FXML private Button editTrackBtn;
    @FXML private Button removeFromPlaylistBtn;
    @FXML private Button deleteTrackBtn;
    private Track currentPlayingTrack = null;

    // ═════════════════════════════════════════════════════════════════════════
    //  INITIALIZE
    // ═════════════════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        // tramite questa istruzione mostriamo nella tendina dei generi musicali quelli della enumerazione
        genreFilter.getItems().setAll(GenreEnum.values());
        //tell to the player what Runnable has to execute when the media ends playing
        audioPlayer.setOnEndOfMedia(() -> {
            FontIcon icon = (FontIcon) playPauseBtn.getGraphic();
            icon.setIconLiteral("fas-play"); // rimette l'icona play quando la canzone finisce
            currentTimeLabel.setText("0:00");
            totalTimeLabel.setText("0:00");
            progressSlider.setValue(0);
        });
        //Pass the callback to the audioPlayer that will be executed everytime the audio goes on
        audioPlayer.setOnTimeChanged((current, total) -> {
            if (total > 0) {
                // aggiorna lo slider (0–100)
                progressSlider.setValue((current / total) * 100);
            }
            // formatta mm:ss per le label
            currentTimeLabel.setText(formatTime(current));
            totalTimeLabel.setText(formatTime(total));
        });

        // collegamento tra le colonne e gli attributi della classe Track
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));

        // registrazione dell'observer
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



    @FXML
    protected void handleAddTrack() {
        // definiamo il metodo accept() del Consumer
        showDialog("TrackDialog.fxml", "Aggiungi Traccia", (TrackDialogController controller) -> {
            if (!controller.validate()) {
                controller.showValidationError();
                return;
            }

            saveTrackFromDialog(controller, null);
        });
    }

    @FXML
    protected void handleEditTrack() {
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
        if (selectedTrack == null) return;

        
        showDialog("TrackDialog.fxml", "Modifica Traccia", (TrackDialogController controller) -> {
            
            if (!controller.validate()) {
                controller.showValidationError();
                return;
            }
            ConcreteLibrary.getInstance().removeTrack(selectedTrack);
            saveTrackFromDialog(controller, selectedTrack);
        });
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
    protected void handleTrackSelected() {
        showTrackDetails(tracksTableView.getSelectionModel().getSelectedItem());
    }


    @FXML
    protected void handlePlayPause() {
        FontIcon icon = (FontIcon) playPauseBtn.getGraphic();
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
        //check to see if the track selected has been changed
        boolean isNewTrack = selectedTrack != null && !selectedTrack.equals(currentPlayingTrack);

        if (audioPlayer.isPlaying()) {
            //if audio was playing but selectedTrack has been changed play the currenTrack
            if (isNewTrack) {
                audioPlayer.play(selectedTrack);
                currentPlayingTrack = selectedTrack;
                icon.setIconLiteral("fas-pause");
            } else {
                //audio was playing and track was the same , stop it
                audioPlayer.pause();
                icon.setIconLiteral("fas-play");
            }
        } else if (audioPlayer.isPaused() && !isNewTrack) {
            //resume only if the selected track was the same
            audioPlayer.resume();
            icon.setIconLiteral("fas-pause");
        } else {
            //no track playing
            if (selectedTrack != null) {
                audioPlayer.play(selectedTrack);
                currentPlayingTrack = selectedTrack;
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

@FXML public void handleSeekTrack(MouseEvent mouseEvent) {
    double seekSeconds = (progressSlider.getValue() / 100) * audioPlayer.getTotalDuration();
    audioPlayer.seekTo(seekSeconds);

}

    @Override
    public void onLibraryChanged(){ // metodo ricavato da LibraryObserver per il pattern Observer
        updateTracksTable();
    }


    // Metodo per prelevare la durata delle Track dal file
    public void setDuration(File audioFile,Track track) {
        // correggere la vista e il salvataggio su DB perchè non viene aggiornato con la setLength
        String uriString = audioFile.toURI().toString(); // permette di trasformare il path relativo in URI

        // 1. Creiamo l'oggetto Media
        Media media = new Media(uriString);

        // 2. Lo "avvolgiamo" nel MediaPlayer per utilizzare setOnReady
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Utilizziamo il listener SetOnReady, siccome la lettura dei metadati del file è asincronala, leggiamo quando il valore diventa valido, non al primo onReady (che a volte arriva troppo presto).
        media.durationProperty().addListener((obs, oldDur, newDur) -> {
            if (newDur != null && !newDur.isUnknown() && newDur.toSeconds() > 0) {

                double totalSeconds = media.getDuration().toSeconds();

                // 2. Calcoliamo i minuti interi dividendo per 60
                int minutes = (int) (totalSeconds / 60);

                // 3. Calcoliamo i secondi rimanenti usando l'operatore modulo (%)
                int seconds = (int) (totalSeconds % 60);

                // 4. Creiamo il valore decimale (es. 3 + (10 / 100) = 3.1)
                double formattedDuration = minutes + (seconds / 100.0); // in questo modo rappresentiamo x minuti : y secondi

                track.setLength(formattedDuration);
                ConcreteLibrary.getInstance().updateTrack(track);
                mediaPlayer.dispose();
            }
        });
    }

    // metodo privato comune 
    private void saveTrackFromDialog(TrackDialogController controller, Track oldTrack) {
        // 1. Preleviamo i dati usando i getter del TrackDialogController
        Track track = new Track(
                controller.getTitle(),
                controller.getAuthor(),
                controller.getGenre(),
                controller.getYear(),
                TagEnum.valueOf(controller.getOptionSelected())
        );

        // 2. Aggiungiamo la traccia al Singleton
        // Questo farà scattare automaticamente l'Observer e aggiornerà la tabella
        //ConcreteLibrary.getInstance().addTrack(newTrack); //chiama internamente il trackDao che salva nel db e costruisce il filepath della track
        //prendiamoci il file selezionato nel dialog e

        try {
            ConcreteLibrary.getInstance().addTrack(track);
        } catch (DuplicateTitleTrackException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Traccia duplicata");
            alert.setContentText("Hai giá una traccia con titolo: " + e.getMessage());
            alert.showAndWait();
        }

        File selectedFile = controller.getSelectedFile();
        //TODO: compute the length of the track here and update in db

        if (selectedFile != null) {
            try {
                Path dest = Paths.get(track.getFilePath()); // es. "music/42.mp3"
                Files.createDirectories(dest.getParent());     // crea la cartella "music/" se non esiste
                Files.copy(selectedFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setDuration(selectedFile, track);

    }


    private void updateTracksTable() {
        tracksTableView.getItems().setAll(ConcreteLibrary.getInstance().getTracks());
    }


    // Riempie il pannello di dettaglio con i dati della traccia selezionata
    private void showTrackDetails(Track track) {
        if (track == null) {            // nessuna traccia selezionata: svuota tutto
            detailTitle.setText("");
            detailAuthor.setText("");
            detailGenre.setText("");
            detailYear.setText("");
            detailTag.setText("");
            return;
        }

        detailTitle.setText(track.getTitle());
        detailAuthor.setText(track.getAuthor());
        detailGenre.setText(track.getGenre() != null ? track.getGenre().toString() : "");
        detailYear.setText(String.valueOf(track.getYear()));
        detailLength.setText(formatTime(TimeUtils.parseFormattedDuration(track.getLength())));
        detailTag.setText(track.getTag() != null ? track.getTag().toString() : "");
        totalTimeLabel.setText(formatTime(TimeUtils.parseFormattedDuration(track.getLength())));


    }

}