package group6.java.group6.models;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public class Library {
    private Set<Track> tracks;
    private static Library instance; // Shared instance

    private Library() {
        this.tracks = new HashSet<Track>();
    }

    public static synchronized Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    public Set<Track> getTracks() {
        return Collections.unmodifiableSet(tracks); // restituisce un Set immutabile
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    public void addTrack(Track t) {
        tracks.add(t);
    }

    public void removeTrack(Track t){
        tracks.remove(t);
    }

}
