package group6.java.group6.helper;

import java.util.ArrayList;
import java.util.List;

import group6.java.group6.dao.PlaylistDao;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import group6.java.group6.player.LoopStrategy;
import group6.java.group6.player.PlaybackStrategy;
import group6.java.group6.player.SequentialStrategy;
import group6.java.group6.player.ShuffleStrategy;
import group6.java.group6.services.PlayerService;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;

/**
 * Gestisce tutta la logica di riproduzione: queue, strategy, play all, next, prev, shuffle, loop.
 * Estrae la logica di playback dal MainController.
 */
public class PlaybackHelper {

    private final PlayerService playerService;
    private final PlaylistDao playlistDao;
    private final TableView<Track> tracksTableView;
    private final Label currentTitle;
    private final Label currentAuthor;

    private PlaybackStrategy strategy = new SequentialStrategy();
    private List<Track> playbackQueue = new ArrayList<>();
    private Playlist activePlaylist = null;

    public PlaybackHelper(PlayerService playerService, PlaylistDao playlistDao,
                          TableView<Track> tracksTableView,
                          Label currentTitle, Label currentAuthor) {
        this.playerService = playerService;
        this.playlistDao = playlistDao;
        this.tracksTableView = tracksTableView;
        this.currentTitle = currentTitle;
        this.currentAuthor = currentAuthor;
    }

    /**
     * Riproduce tutte le tracce attualmente visibili nella TableView.
     */
    public void playAll() {
        List<Track> currentList = tracksTableView.getItems();

        if (currentList == null || currentList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Riproduzione");
            alert.setHeaderText("Nessuna traccia da riprodurre");
            alert.showAndWait();
            return;
        }

        activePlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
        if (activePlaylist != null) playlistDao.incrementCountPlayed(activePlaylist);

        playbackQueue = strategy.buildQueue(currentList, null);
        Track firstTrack = playbackQueue.get(0);
        changeTrack(firstTrack);
    }

    /**
     * Gestisce il toggle dello shuffle.
     */
    public void handleShuffle(ToggleButton shuffleToggleBtn, ToggleButton loopBtn) {
        if (shuffleToggleBtn.isSelected()) {
            loopBtn.setSelected(false);
            strategy = new ShuffleStrategy();
        } else {
            strategy = new SequentialStrategy();
        }
    }

    /**
     * Gestisce il toggle del loop.
     */
    public void handleLoop(ToggleButton loopBtn, ToggleButton shuffleToggleBtn) {
        if (loopBtn.isSelected()) {
            shuffleToggleBtn.setSelected(false);
            strategy = new LoopStrategy();
        } else {
            strategy = new SequentialStrategy();
        }
    }

    /**
     * Passa alla traccia successiva nella coda.
     */
    public void next() {
        Track current = playerService.getCurrentPlayingTrack();
        if (current == null || playbackQueue.isEmpty()) return;
        Track next = strategy.nextTrack(playbackQueue, current);
        if (next != null)
            changeTrack(next);
        else
            stopPlayback();
    }

    /**
     * Torna alla traccia precedente nella coda.
     */
    public void prev() {
        Track current = playerService.getCurrentPlayingTrack();
        if (current == null || playbackQueue.isEmpty()) return;
        Track prev = strategy.prevTrack(playbackQueue, current);
        if (prev != null)
            changeTrack(prev);
    }

    /**
     * Gestisce play/pause sulla traccia selezionata.
     */
    public void handlePlayPause(Track selectedTrack) {
        if (selectedTrack != null && selectedTrack != playerService.getCurrentPlayingTrack()) {
            activePlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
            playerService.setCurrentPlaylistLabel(activePlaylist != null ? activePlaylist.getTitle() : "Libreria");
        }
        playerService.handlePlayPause(selectedTrack);
    }

    /**
     * Cambia la traccia in riproduzione, selezionandola nella tabella.
     */
    public void changeTrack(Track nuovaTraccia) {
        if (tracksTableView.getItems().contains(nuovaTraccia)) {
            tracksTableView.getSelectionModel().select(nuovaTraccia);
        }
        playerService.forcePlayTrack(nuovaTraccia);
        playerService.setCurrentPlaylistLabel(activePlaylist != null ? activePlaylist.getTitle() : "Libreria");
    }

    /**
     * Ferma la riproduzione e pulisce lo stato.
     */
    public void stopPlayback() {
        playerService.stopAndClearIfPlaying(playerService.getCurrentPlayingTrack());
        tracksTableView.getSelectionModel().clearSelection();
        currentTitle.setText("");
        currentAuthor.setText("");
    }

    /**
     * Sincronizza la coda di riproduzione con la sorgente corrente.
     */
    public void syncQueue() {
        Track current = playerService.getCurrentPlayingTrack();

        List<Track> sourceTracks;
        if (activePlaylist != null) {
            sourceTracks = new ArrayList<>(activePlaylist.getTracks());
        } else {
            sourceTracks = new ArrayList<>(ConcreteLibrary.getInstance().getTracks());
        }

        playbackQueue = strategy.buildQueue(sourceTracks, current);

        if (current != null && !sourceTracks.contains(current)) {
            stopPlayback();
            playbackQueue.clear();
            activePlaylist = null;
        }
    }

    public Playlist getActivePlaylist() {
        return activePlaylist;
    }

    public void setActivePlaylist(Playlist activePlaylist) {
        this.activePlaylist = activePlaylist;
    }
}
