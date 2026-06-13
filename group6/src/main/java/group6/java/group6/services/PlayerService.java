package group6.java.group6.services;

import org.kordamp.ikonli.javafx.FontIcon;

import group6.java.group6.models.Track;
import group6.java.group6.player.AudioPlayer;
import group6.java.group6.utils.TimeUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class PlayerService {

    private final AudioPlayer audioPlayer = new AudioPlayer();
    private final TrackService trackService;
    // Callback per avvisare il Controller che la traccia è finita
    private Runnable onTrackEnd;

    // Elementi UI
    private final Label currentTimeLabel;
    private final Label totalTimeLabel;
    private final Slider progressSlider;
    private final Button playPauseBtn;
    private final Label currentTitle;
    private final Label currentAuthor;

    private Track currentPlayingTrack = null;

    public PlayerService(TrackService trackService, Label currentTimeLabel, Label totalTimeLabel,
                         Slider progressSlider, Button playPauseBtn,
                         Label currentTitle, Label currentAuthor) {
        this.trackService = trackService;
        this.currentTimeLabel = currentTimeLabel;
        this.totalTimeLabel = totalTimeLabel;
        this.progressSlider = progressSlider;
        this.playPauseBtn = playPauseBtn;
        this.currentTitle = currentTitle;
        this.currentAuthor = currentAuthor;

        setupPlayerCallbacks();
    }

    private void setupPlayerCallbacks() {
        audioPlayer.setOnEndOfMedia(() -> {
            FontIcon icon = (FontIcon) playPauseBtn.getGraphic();
            icon.setIconLiteral("fas-play");
            currentTimeLabel.setText("0:00");
            totalTimeLabel.setText("0:00");
            progressSlider.setValue(0);

            if (onTrackEnd != null) {
                onTrackEnd.run();
            }
        });



        audioPlayer.setOnTimeChanged((current, total) -> {
            if (total > 0) {
                progressSlider.setValue((current / total) * 100);
            }
            currentTimeLabel.setText(TimeUtils.formatTime(current));
            totalTimeLabel.setText(TimeUtils.formatTime(total));
        });
    }

    public void handlePlayPause(Track selectedTrack) {
        FontIcon icon = (FontIcon) playPauseBtn.getGraphic();
        boolean isNewTrack = selectedTrack != null && !selectedTrack.equals(currentPlayingTrack);

        if (selectedTrack != null) {
            currentAuthor.setText(selectedTrack.getAuthor());
            currentTitle.setText(selectedTrack.getTitle());
        }

        if (audioPlayer.isPlaying()) {
            if (isNewTrack) {
                playNewTrack(selectedTrack, icon);
            } else {
                audioPlayer.pause();
                icon.setIconLiteral("fas-play");
            }
        } else if (audioPlayer.isPaused() && !isNewTrack) {
            audioPlayer.resume();
            icon.setIconLiteral("fas-pause");
        } else {
            if (selectedTrack != null) {
                playNewTrack(selectedTrack, icon);
            }
        }
    }

    private void playNewTrack(Track track, FontIcon icon) {
        audioPlayer.play(track);
        currentPlayingTrack = track;
        icon.setIconLiteral("fas-pause");
        track.incrementCountPlayed();
        trackService.updateTrack(track, null);
    }

    public void seekTrack() {
        double seekSeconds = (progressSlider.getValue() / 100) * audioPlayer.getTotalDuration();
        audioPlayer.seekTo(seekSeconds);
    }

    public void stopAndClearIfPlaying(Track track) {
        if (track != null && track.equals(currentPlayingTrack)) {
            audioPlayer.stop();
            currentPlayingTrack = null;
            progressSlider.setValue(0);
            currentTimeLabel.setText("0:00");
            FontIcon icon = (FontIcon) playPauseBtn.getGraphic();
            icon.setIconLiteral("fas-play");
        }


    }

    public Track getCurrentPlayingTrack() {
        return currentPlayingTrack;
    }


    public void changeTrack(Track newTrack) {
        boolean wasPaused = audioPlayer.isPaused();
        currentPlayingTrack = newTrack;
        currentTitle.setText(newTrack.getTitle());
        currentAuthor.setText(newTrack.getAuthor());
        audioPlayer.play(newTrack);
        FontIcon icon = (FontIcon) playPauseBtn.getGraphic();
        if (wasPaused) {
            audioPlayer.pause();
            icon.setIconLiteral("fas-play");
        } else {
            icon.setIconLiteral("fas-pause");
            newTrack.incrementCountPlayed();
            trackService.incrementPlayCount(newTrack);
        }
    }

    public void setOnTrackEnd(Runnable onTrackEnd) {
        this.onTrackEnd = onTrackEnd;
    }

}