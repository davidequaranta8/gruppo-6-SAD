package group6.java.group6.player;

import java.util.ArrayList;
import java.util.List;

import group6.java.group6.models.Track;

public class LoopStrategy implements PlaybackStrategy {
     @Override
    public Track nextTrack(List<Track> queue, Track current) {
        int i = queue.indexOf(current);
        if (i < 0) return null;
        return queue.get((i + 1) % queue.size());
    }
    @Override
    public Track prevTrack(List<Track> queue, Track current) {
        int i = queue.indexOf(current);
        if (i < 0) return null;
        return queue.get((i - 1 + queue.size()) % queue.size());
    }
    @Override
    public List<Track> buildQueue(List<Track> tracks, Track current) {
        return new ArrayList<>(tracks);
    }
}
