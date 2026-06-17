package group6.java.group6.helper;

import java.io.IOException;
import java.util.function.Consumer;

import group6.java.group6.Application;
import group6.java.group6.controllers.TrackDialogController;
import group6.java.group6.models.Track;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

/**
 * Gestisce l'apertura e la chiusura dei dialog FXML.
 * Estrae la logica generica di caricamento dialog dal MainController.
 */
public class DialogHelper {

    /**
     * Apre un dialog FXML ed esegue l'azione al click su OK.
     */
    public <T> void showDialog(String fxmlFile, String title, Consumer<T> onOkAction) {
        showDialog(fxmlFile, title, null, onOkAction);
    }

    /**
     * Apre un dialog FXML con una pre-azione (prima di mostrare) e un'azione al click su OK.
     */
    public <T> void showDialog(String fxmlFile, String title, Consumer<T> preShowAction, Consumer<T> onOkAction) {
        try {
            var url = Application.class.getResource(fxmlFile);
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

    /**
     * Apre il dialog di modifica traccia, pre-popolando i campi con i dati della traccia.
     */
    public void showEditTrackDialog(String fxmlFile, String title, Track track,
                                     Consumer<TrackDialogController> onOkAction) {
        try {
            var url = Application.class.getResource(fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            DialogPane dialogPane = fxmlLoader.load();

            TrackDialogController controller = fxmlLoader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(title);

            controller.setAuthorField(track.getAuthor());
            controller.setGenreCombo(track.getGenre());
            controller.setTitleField(track.getTitle());
            controller.setFileNameLabel(track.getFilePath());
            controller.setToggleGroup(track.getTag());
            controller.setYearSpinner(track.getYear());

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
}
