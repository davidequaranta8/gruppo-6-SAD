package group6.java.group6.helper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import group6.java.group6.controllers.AutoPlaylistDialogController;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.exceptions.DuplicatePlaylistException;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import javafx.scene.control.Alert;

/**
 * Gestisce la generazione automatica di playlist per genere, anno o tag.
 * Estrae la logica generateBy* e createPlaylistWithTracks dal MainController.
 */
public class AutoPlaylistHelper {

    private final DialogHelper dialogHelper;

    public AutoPlaylistHelper(DialogHelper dialogHelper) {
        this.dialogHelper = dialogHelper;
    }

    /**
     * Apre il dialog per la generazione automatica di playlist.
     */
    public void handleGeneratePlaylist() {
        if (ConcreteLibrary.getInstance().getTracks().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Libreria vuota");
            alert.setHeaderText("Nessuna traccia disponibile");
            alert.setContentText("La libreria è vuota. Aggiungi delle tracce prima di generare una playlist.");
            alert.showAndWait();
            return;
        }

        dialogHelper.showDialog("AutoPlaylistDialog.fxml", "Genera Playlist Automatica",
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
}
