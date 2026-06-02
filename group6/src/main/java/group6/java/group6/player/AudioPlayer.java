package group6.java.group6.player;

import group6.java.group6.models.Track;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.function.BiConsumer;

public class AudioPlayer {

    private MediaPlayer mediaPlayer;
    //Callback to be called on end of a track
    private Runnable onEndCallback;
    //Callback to be called on progressing of a track
    //BiConsumer is same as Runnable , the only difference is in the fact that the former accepts data
    private BiConsumer<Double, Double> onTimeChanged;

    public void setOnEndOfMedia(Runnable callback) {
        this.onEndCallback = callback;
    }

    public void setOnTimeChanged(BiConsumer<Double, Double> callback) {
        this.onTimeChanged = callback;
    }


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

        //set callback to run at the end of the media
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.dispose();
            mediaPlayer = null;
            if (onEndCallback != null) {
                Platform.runLater(onEndCallback); // aggiorna la UI sul thread JavaFX
            }
        });
        //set the callback to run at each change
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (onTimeChanged != null && newTime != null) {
                double current = newTime.toSeconds();
                double total   = mediaPlayer.getTotalDuration() != null
                        ? mediaPlayer.getTotalDuration().toSeconds()
                        : 0;
                Platform.runLater(() -> onTimeChanged.accept(current, total));
            }
        });
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
    //useful to move along the track
    public void seekTo(double seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }

    public double getTotalDuration() {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null)
            return mediaPlayer.getTotalDuration().toSeconds();
        return 0;
    }
}