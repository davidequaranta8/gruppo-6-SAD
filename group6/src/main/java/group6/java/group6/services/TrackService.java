package group6.java.group6.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class TrackService {
private final TrackDao  trackDao = new TrackDao();


    public void saveTrack(Track track, File audioFile) throws DuplicateTitleTrackException {
        //Salva nel DB e aggiorna i metadati
        ConcreteLibrary.getInstance().addTrack(track);

        //Copia il file fisico con path pari a quello della track passata
        if (audioFile != null) {
            copyFileToMusicFolder(audioFile, track.getFilePath());
            setDuration(audioFile, track, () -> {
                ConcreteLibrary.getInstance().updateTrack(track);
            });
        }
    }

    public void updateTrack(Track track, File audioFile) {
        //Aggiorna nel DB
        ConcreteLibrary.getInstance().updateTrack(track);

        //Se c'è un nuovo file, lo sostituiamo
        if (audioFile != null) {
            copyFileToMusicFolder(audioFile, track.getFilePath());
            setDuration(audioFile, track, () -> {
                ConcreteLibrary.getInstance().updateTrack(track);
            });
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
    private void setDuration(File audioFile, Track track, Runnable onDisposed) {
        String uriString = audioFile.toURI().toString();
        Media media = new Media(uriString);
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        media.durationProperty().addListener((obs, oldDur, newDur) -> {
            if (newDur != null && !newDur.isUnknown() && newDur.toSeconds() > 0) {
                double totalSeconds = media.getDuration().toSeconds();
                int minutes = (int) (totalSeconds / 60);
                int seconds = (int) (totalSeconds % 60);
                double formattedDuration = Double.parseDouble(
                        String.format("%d.%02d", minutes, seconds)
                );
                track.setLength(formattedDuration);
                ConcreteLibrary.getInstance().updateTrack(track);
                mediaPlayer.statusProperty().addListener((obsS, oldS, newS) -> {
                    if (newS == MediaPlayer.Status.DISPOSED) {
                        Platform.runLater(onDisposed);
                    }
                });
                mediaPlayer.dispose();
            }
        });
    }



    public void incrementPlayCount(Track track) {
        trackDao.update(track);




            // 2. Cerchiamo la traccia "gemella" (stesso ID) nella Libreria Principale e la aggiorniamo
            for (Track t : ConcreteLibrary.getInstance().getTracks()) {
                if (t.getId() == track.getId() && t != track) {
                    t.setCountPlayed(track.getCountPlayed());
                    break;
                }
            }

            // 3. Cerchiamo la traccia gemella anche nella Playlist corrente per evitare inconsistenze incrociate
           Playlist currentPlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
            if (currentPlaylist != null) {
                for (Track t : currentPlaylist.getTracks()) {
                    if (t.getId() == track.getId() && t != track) {
                        t.setCountPlayed(track.getCountPlayed());
                        break;
                    }
                }
            }
        }
    }
