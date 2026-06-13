package group6.java.group6.player;

import java.util.List;

import group6.java.group6.models.Track;

public interface PlaybackStrategy {
    Track nextTrack(List<Track> queue, Track current);
    Track prevTrack(List<Track> queue, Track current);
    List<Track> buildQueue(List<Track> tracks, Track current); 
}
