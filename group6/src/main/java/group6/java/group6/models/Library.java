package group6.java.group6.models;

import java.util.HashSet;
import java.util.Set;

public class Library {
    private Set<Track> tracks;

    public Library(Set<Track> tracks) {
        this.tracks = new HashSet<Track>();
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    public void addTrack(Track t){
        if(!tracks.contains(t))
            tracks.add(t);
        }

    public void removeTrack(Track t){
        if(tracks.contains(t))
            tracks.remove(t);

    }

    public void updateTrack(Track oldT,Track newT){
        if (tracks.remove(oldT)){
            tracks.add(newT);
        }

    }
}
