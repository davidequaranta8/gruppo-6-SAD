package group6.java.group6.helper;

import group6.java.group6.controllers.TrackDialogController;
import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.Track;
import group6.java.group6.services.PlayerService;
import group6.java.group6.services.TrackService;
import group6.java.group6.utils.TimeUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.control.Button;

import java.util.Optional;

import static group6.java.group6.utils.TimeUtils.formatTime;

/**
 * Gestisce le operazioni CRUD sulle tracce: aggiunta, modifica, eliminazione,
 * visualizzazione dettagli e salvataggio da dialog.
 */
public class TrackHelper {

    private final DialogHelper dialogHelper;
    private final TrackService trackService;
    private final TrackDao trackDao;
    private final PlayerService playerService;
    private final TableView<Track> tracksTableView;
    private final Button playPauseBtn;

    // Label del pannello dettaglio
    private final Label detailTitle;
    private final Label detailAuthor;
    private final Label detailGenre;
    private final Label detailYear;
    private final Label detailLength;
    private final Label detailTag;
    private final Label tagLabel;
    private final Label totalTimeLabel;
    private final Label currentTitle;
    private final Label currentAuthor;

    public TrackHelper(DialogHelper dialogHelper, TrackService trackService, TrackDao trackDao,
                       PlayerService playerService, TableView<Track> tracksTableView,
                       Button playPauseBtn,
                       Label detailTitle, Label detailAuthor, Label detailGenre,
                       Label detailYear, Label detailLength, Label detailTag, Label tagLabel,
                       Label totalTimeLabel, Label currentTitle, Label currentAuthor) {
        this.dialogHelper = dialogHelper;
        this.trackService = trackService;
        this.trackDao = trackDao;
        this.playerService = playerService;
        this.tracksTableView = tracksTableView;
        this.playPauseBtn = playPauseBtn;
        this.detailTitle = detailTitle;
        this.detailAuthor = detailAuthor;
        this.detailGenre = detailGenre;
        this.detailYear = detailYear;
        this.detailLength = detailLength;
        this.detailTag = detailTag;
        this.tagLabel = tagLabel;
        this.totalTimeLabel = totalTimeLabel;
        this.currentTitle = currentTitle;
        this.currentAuthor = currentAuthor;
    }

    /**
     * Apre il dialog per aggiungere una nuova traccia.
     */
    public void handleAddTrack() {
        dialogHelper.showDialog("TrackDialog.fxml", "Aggiungi Traccia", (TrackDialogController controller) -> {
            if (!controller.validate(false)) {
                controller.showValidationError();
                return;
            }
            saveTrackFromDialog(controller);
        });
    }

    /**
     * Apre il dialog per modificare la traccia selezionata.
     */
    public void handleEditTrack(Runnable onSyncQueue) {
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();
        if (selectedTrack == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modifica Traccia");
            alert.setHeaderText("Nessuna traccia selezionata");
            alert.setContentText("Seleziona una traccia dalla tabella.");
            alert.showAndWait();
            return;
        }
        dialogHelper.showEditTrackDialog("TrackDialog.fxml", "Modifica Traccia", selectedTrack, (controller) -> {
            if (!controller.validate(true)) {
                controller.showValidationError();
                return;
            }

            if (trackDao.existsByAuthorAndTitleAndId(controller.getAuthor(), controller.getTitle(), selectedTrack.getId())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Errore");
                alert.setContentText("Esiste giá una traccia con titolo " + controller.getTitle() + " e autore " + controller.getAuthor());
                alert.showAndWait();
                return;
            }

            selectedTrack.updateTrack(controller.getTitle(), controller.getAuthor(), controller.getGenre(), controller.getYear(), controller.getTag());

            if (controller.getSelectedFile() != null && selectedTrack.equals(playerService.getCurrentPlayingTrack())) {
                playerService.stopAndClearIfPlaying(selectedTrack);
            }

            trackService.updateTrack(selectedTrack, controller.getSelectedFile());
            showTrackDetails(selectedTrack);
            onSyncQueue.run();
        });

        if (playerService.getCurrentPlayingTrack() != null && playerService.getCurrentPlayingTrack().equals(selectedTrack)) {
            currentTitle.setText(playerService.getCurrentPlayingTrack().getTitle());
            currentAuthor.setText(playerService.getCurrentPlayingTrack().getAuthor());
        }
    }

    /**
     * Elimina la traccia selezionata con conferma.
     */
    public void handleDeleteTrack(Runnable onSyncQueue) {
        Track selectedTrack = tracksTableView.getSelectionModel().getSelectedItem();

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Conferma eliminazione");
        confirmation.setHeaderText("Eliminazione traccia");
        confirmation.setContentText(
                "Sei sicuro di voler eliminare la traccia \"" +
                        selectedTrack.getTitle() + "\"?"
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            trackService.deleteTrack(selectedTrack);
            FontIcon icon = (FontIcon) playPauseBtn.getGraphic();
            icon.setIconLiteral("fas-play");
        }
        showTrackDetails(null);
        onSyncQueue.run();
    }

    /**
     * Mostra i dettagli di una traccia nel pannello laterale.
     */
    public void showTrackDetails(Track track) {
        if (track == null) {
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

    /**
     * Salva una nuova traccia dal dialog.
     */
    public void saveTrackFromDialog(TrackDialogController controller) {
        Track track;

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
            trackService.saveTrack(track, controller.getSelectedFile());
        } catch (DuplicateTitleTrackException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Traccia duplicata");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
