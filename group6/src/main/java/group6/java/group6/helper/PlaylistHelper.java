package group6.java.group6.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import group6.java.group6.states.MainViewContext;
import group6.java.group6.controllers.PlaylistDialogController;
import group6.java.group6.dao.PlaylistDao;
import group6.java.group6.exceptions.DuplicatePlaylistException;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import group6.java.group6.services.PlayerService;
import group6.java.group6.utils.AddTrackCommand;
import group6.java.group6.utils.Command;
import group6.java.group6.utils.CommandInvoker;
import group6.java.group6.utils.RemoveTrackCommand;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

/**
 * Gestisce tutte le operazioni CRUD sulle playlist: creazione, rinomina,
 * eliminazione, aggiunta/rimozione tracce, sidebar e navigazione.
 */
public class PlaylistHelper {

    private final DialogHelper dialogHelper;
    private final PlayerService playerService;
    private final PlaylistDao playlistDao;
    private final CommandInvoker invoker;
    private final TableView<Track> tracksTableView;
    private final ListView<String> playlistListView;
    private final Label playlistTitleLabel;
    private final Label currentTitle;
    private final Label currentAuthor;
    private final MainViewContext viewContext;

    public PlaylistHelper(DialogHelper dialogHelper, PlayerService playerService,
                          PlaylistDao playlistDao, CommandInvoker invoker,
                          TableView<Track> tracksTableView, ListView<String> playlistListView,
                          Label playlistTitleLabel, Label currentTitle, Label currentAuthor,
                          MainViewContext viewContext) {
        this.dialogHelper = dialogHelper;
        this.playerService = playerService;
        this.playlistDao = playlistDao;
        this.invoker = invoker;
        this.tracksTableView = tracksTableView;
        this.playlistListView = playlistListView;
        this.playlistTitleLabel = playlistTitleLabel;
        this.currentTitle = currentTitle;
        this.currentAuthor = currentAuthor;
        this.viewContext = viewContext;
    }

    /**
     * Apre il dialog per creare una nuova playlist.
     */
    public void handleNewPlaylist() {
        dialogHelper.showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Nuova Playlist", (PlaylistDialogController ctrl) -> {
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

    /**
     * Apre il dialog per rinominare la playlist selezionata.
     */
    public void handleRenamePlaylist(PlaybackHelper playbackHelper) {
        Playlist selected = PlaylistManager.getInstance().getSelectedPlaylist();
        if (selected == null) return;
        dialogHelper.showDialog("PlaylistDialogView/PlaylistDialog.fxml", "Rinomina Playlist",
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
                    Playlist active = playbackHelper.getActivePlaylist();
                    playerService.setCurrentPlaylistLabel(active != null ? active.getTitle() : "Libreria");
                });
    }

    /**
     * Elimina la playlist selezionata con conferma.
     */
    public void handleDeletePlaylist(PlaybackHelper playbackHelper, Runnable onShowAllTracks) {
        Playlist selected = PlaylistManager.getInstance().getSelectedPlaylist();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText("Eliminazione playlist");
        confirm.setContentText("Eliminare \"" + selected.getTitle() + "\"?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                if (selected.getTracks().contains(playerService.getCurrentPlayingTrack())) {
                    playerService.stopAndClearIfPlaying(playerService.getCurrentPlayingTrack());
                    currentAuthor.setText("");
                    currentTitle.setText("");
                    playerService.setCurrentPlaylistLabel("");
                }
                PlaylistManager.getInstance().deletePlaylist(selected);
                onShowAllTracks.run();
            }
        });
    }

    /**
     * Aggiunge una traccia dalla libreria alla playlist selezionata.
     */
    public void handleAddToPlaylist(Runnable onSyncQueue) {
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
            Command addCmd = new AddTrackCommand(trackToAdd, playlist);
            invoker.executeCommand(addCmd);
            onSyncQueue.run();
        });
    }

    /**
     * Rimuove la traccia selezionata dalla playlist con conferma.
     */
    public void handleRemoveFromPlaylist(Runnable onSyncQueue) {
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
                Command removeCmd = new RemoveTrackCommand(selectedTrack, playlist);
                invoker.executeCommand(removeCmd);
            }
            onSyncQueue.run();
        });
    }

    /**
     * Mostra le top 5 playlist più ascoltate nella sidebar.
     */
    public void handleShowTopPlayedPlaylist() {
        List<Playlist> topPlaylists = playlistDao.getTopPlayedPlaylist(5);
        List<String> topNames = topPlaylists.stream()
                .map(Playlist::getTitle)
                .collect(Collectors.toList());
        playlistListView.getItems().setAll(topNames);
    }

    /**
     * Gestisce il click sulla sidebar per selezionare una playlist.
     */
    public void handleSidebarClick(MouseEvent event) {
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

    /**
     * Torna alla vista libreria (tutte le tracce).
     */
    public void handleShowAllTracks(Button mostPlayedTracksButton,
                                     Button mostPlayedPlaylistButton,
                                     Runnable onResetFilter) {
        PlaylistManager.getInstance().setSelectedPlaylist(null);
        playlistListView.getSelectionModel().clearSelection();
        tracksTableView.getItems().setAll(ConcreteLibrary.getInstance().getTracks());
        viewContext.setState(MainViewContext.LIBRARY_STATE);
        mostPlayedTracksButton.setVisible(true);
        mostPlayedPlaylistButton.setVisible(true);
        onResetFilter.run();
    }

    /**
     * Mostra il contenuto di una playlist nella tabella.
     */
    public void showPlaylistContent(Playlist playlist) {
        tracksTableView.getSelectionModel().clearSelection();
        PlaylistManager.getInstance().loadTracksForPlaylist(playlist);
        tracksTableView.getItems().setAll(playlist.getTracks());
        playlistTitleLabel.setText(playlist.getTitle());
        viewContext.setState(MainViewContext.PLAYLIST_STATE);
    }

    /**
     * Aggiorna la sidebar delle playlist.
     */
    public void updatePlaylistSidebar() {
        if (playlistListView == null) return;
        Playlist p = PlaylistManager.getInstance().getSelectedPlaylist();
        String current = p != null ? p.getTitle() : null;
        List<String> names = PlaylistManager.getInstance().getPlaylists()
                .stream()
                .map(Playlist::getTitle)
                .collect(Collectors.toList());

        playlistListView.getItems().setAll(names);

        if (current != null && names.contains(current)) {
            playlistListView.getSelectionModel().select(current);
        }
    }
}
