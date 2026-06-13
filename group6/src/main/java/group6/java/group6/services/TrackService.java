package group6.java.group6.services;

import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Track;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TrackService {

    public void saveTrack(Track track, File audioFile) throws DuplicateTitleTrackException {
        //Salva nel DB e aggiorna i metadati
        ConcreteLibrary.getInstance().addTrack(track);

        //Copia il file fisico con path pari a quello della track passata
        if (audioFile != null) {
            copyFileToMusicFolder(audioFile, track.getFilePath());
            setDuration(audioFile, track);
        }
    }

    public void updateTrack(Track track, File audioFile) {
        //Aggiorna nel DB
        ConcreteLibrary.getInstance().updateTrack(track);

        //Se c'è un nuovo file, lo sostituiamo
        if (audioFile != null) {
            copyFileToMusicFolder(audioFile, track.getFilePath());
            setDuration(audioFile, track);
        }
    }

    public void deleteTrack(Track track) {
        //Rimuovi dalla libreria e dal database
        ConcreteLibrary.getInstance().removeTrack(track);

        //Elimina il file fisicamente dal disco
        File file = new File(track.getFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    //copia il file fisico nel path specificato per quella traccia
    private void copyFileToMusicFolder(File sourceFile, String destPath) {
        try {
            Path dest = Paths.get(destPath);
            Files.createDirectories(dest.getParent());
            Files.copy(sourceFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //calcola in maniera formattata la durata di una traccia
    private void setDuration(File audioFile, Track track) {
        String uriString = audioFile.toURI().toString();
        Media media = new Media(uriString);
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        media.durationProperty().addListener((obs, oldDur, newDur) -> {
            if (newDur != null && !newDur.isUnknown() && newDur.toSeconds() > 0) {
                double totalSeconds = media.getDuration().toSeconds();
                int minutes = (int) (totalSeconds / 60);
                int seconds = (int) (totalSeconds % 60);
                double formattedDuration = minutes + (seconds / 100.0);

                track.setLength(formattedDuration);
                ConcreteLibrary.getInstance().updateTrack(track);
                mediaPlayer.dispose();
            }
        });
    }
}