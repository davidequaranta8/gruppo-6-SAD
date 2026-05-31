package group6.java.group6.player;

import group6.java.group6.models.Track;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class AudioPlayer {

    private MediaPlayer mediaPlayer;

    // Carica e mette in play una traccia
    public void play(Track track) {
        File file = new File(track.getFilePath());
        if (!file.exists()) {
            System.err.println("File not found: " + track.getFilePath());
            return;
        }
        // Se c'è già qualcosa in play, lo fermo prima
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // libera le risorse JavaFX
        }
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void resume() {
        if (mediaPlayer != null) mediaPlayer.play();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    // Utile per sapere se sta suonando (es. per aggiornare la UI)
    public boolean isPlaying() {
        return mediaPlayer != null &&
                mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }


    public boolean isPaused() {
        return mediaPlayer != null &&
                mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED;
    }
}