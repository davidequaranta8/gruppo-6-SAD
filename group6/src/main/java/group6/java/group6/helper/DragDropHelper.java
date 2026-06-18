package group6.java.group6.helper;

import java.util.ArrayList;
import java.util.List;

import group6.java.group6.dao.PlaylistDao;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragDropHelper {

   
    private static final DataFormat TRACK_INDEX =
            new DataFormat("application/x-group6-track-index");
 
    private final TableView<Track> tracksTableView;
    private final PlaylistDao playlistDao;
    private final PlaybackHelper playbackHelper;
    private final ToggleButton shuffleToggleBtn;
 
    public DragDropHelper(TableView<Track> tracksTableView, PlaylistDao playlistDao, PlaybackHelper playbackHelper, ToggleButton shuffleToggleBtn) {
        this.tracksTableView = tracksTableView;
        this.playlistDao = playlistDao;
        this.playbackHelper = playbackHelper;
        this.shuffleToggleBtn = shuffleToggleBtn;
    }
 
    public void enable() {
        tracksTableView.setRowFactory(tv -> {
            TableRow<Track> row = new TableRow<>();
 
            row.setOnDragDetected(event -> {
                if (row.isEmpty()) {
                    return;
                }
                Playlist selected = PlaylistManager.getInstance().getSelectedPlaylist();
                if (selected == null) {
                    return;
                }
                if (isViewFiltered(selected)) {
                    showFilterWarning();
                    return;
                }
                if (isShuffleActive()) {
                    showShuffleWarning();
                    return;
                }
 
                Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(TRACK_INDEX, row.getIndex());
                db.setContent(content);
                event.consume();
            });
 
            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(TRACK_INDEX)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            });
 
            row.setOnDragEntered(event -> {
                if (event.getDragboard().hasContent(TRACK_INDEX) && !row.isEmpty()) {
                    row.setOpacity(0.6);
                }
            });
 
            row.setOnDragExited(event -> {
                if (event.getDragboard().hasContent(TRACK_INDEX)) {
                    row.setOpacity(1.0);
                }
            });
 
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (!db.hasContent(TRACK_INDEX)) {
                    return;
                }
 
                Playlist selected = PlaylistManager.getInstance().getSelectedPlaylist();
                if (selected == null || isViewFiltered(selected)) {
                    event.setDropCompleted(false);
                    event.consume();
                    return;
                }
 
                ObservableList<Track> items = tracksTableView.getItems();
                int draggedIndex = (Integer) db.getContent(TRACK_INDEX);
                int dropIndex = row.isEmpty() ? items.size() : row.getIndex();
 
                if (draggedIndex != dropIndex) {
                    Track draggedTrack = items.remove(draggedIndex);
                    if (dropIndex > items.size()) {
                        dropIndex = items.size();
                    }
                    items.add(dropIndex, draggedTrack);
                    tracksTableView.getSelectionModel().select(draggedTrack);
 
                    persistNewOrder(selected);   
                }
 
                event.setDropCompleted(true);
                event.consume();
            });
 
            row.setOnDragDone(event -> row.setOpacity(1.0));
 
            return row;
        });
    }
 
    private void persistNewOrder(Playlist selected) {
        // Riallinea il LinkedHashSet della playlist all'ordine mostrato in tabella.
        List<Track> newOrder = new ArrayList<>(tracksTableView.getItems());
        selected.getTracks().clear();
        selected.getTracks().addAll(newOrder);
 
        playlistDao.updateTrackOrder(selected);
 
        playbackHelper.syncQueue();
    }
    private boolean isViewFiltered(Playlist selected) {
        return tracksTableView.getItems().size() != selected.getTracks().size();
    }
 
    private boolean isShuffleActive() {
        return shuffleToggleBtn != null && shuffleToggleBtn.isSelected();
    }
 
    private void showFilterWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Riordinamento non disponibile");
        alert.setHeaderText(null);
        alert.setContentText("Il riordinamento non è disponibile mentre una ricerca o un filtro è attivo.\n"
                + "Azzera la ricerca e i filtri per riordinare tutte le tracce della playlist.");
        alert.showAndWait();
    }
 
    private void showShuffleWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Riordinamento non disponibile");
        alert.setHeaderText(null);
        alert.setContentText("Il riordinamento manuale non è disponibile in modalità shuffle.\n"
                + "Disattiva lo shuffle per riordinare le tracce della playlist.");
        alert.showAndWait();
    }
}