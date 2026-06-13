package group6.java.group6.player;

import java.util.ArrayList;
import java.util.List;

import group6.java.group6.models.Track;


public class SequentialStrategy implements PlaybackStrategy {

@Override
    public Track nextTrack(List<Track> queue, Track current) {
        int i = queue.indexOf(current);
        return (i >= 0 && i < queue.size() - 1) ? queue.get(i + 1) : null;
    }

    @Override
    public Track prevTrack(List<Track> queue, Track current) {
        int i = queue.indexOf(current);
        return (i > 0) ? queue.get(i - 1) : null;
    }
    
    @Override
    public List<Track> buildQueue(List<Track> tracks, Track current) {
        return new ArrayList<>(tracks);
    }
}
