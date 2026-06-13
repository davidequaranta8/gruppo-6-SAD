package group6.java.group6.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import group6.java.group6.models.Track;

public class ShuffleStrategy implements PlaybackStrategy{
    private final Random random = new Random();
    private final List<Track> played = new ArrayList<>();

    @Override
    public Track nextTrack(List<Track> queue, Track current) {
        if (current != null && !played.contains(current)) {
            played.add(current);
        }

        // tracce non ancora ascoltate
        List<Track> remaining = new ArrayList<>();
        for (Track t : queue) {
            if (!played.contains(t)) {
                remaining.add(t);
            }
        }

        // se le abbiamo ascoltate tutte, ricominciamo
        if (remaining.isEmpty()) {
            played.clear();
            remaining = new ArrayList<>(queue);
            remaining.remove(current);
        }

        return remaining.get(random.nextInt(remaining.size()));
    }

    @Override
    public Track prevTrack(List<Track> queue, Track current) {
        // torna all'ultima traccia ascoltata
        if (played.isEmpty()) return null;
        Track prev = played.get(played.size() - 1);
        played.remove(prev);
        return prev;
    }

    @Override
    public List<Track> buildQueue(List<Track> tracks, Track current) {
        played.clear(); // reset quando la coda cambia
        List<Track> shuffled = new ArrayList<>(tracks);
        Collections.shuffle(shuffled);
        if (current != null) {
            shuffled.remove(current);
            shuffled.add(0, current);
        }
        return shuffled;
    }
}   
