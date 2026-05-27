package group6.java.group6.models;

import java.util.Set;

public class Library {
    private Set<Track> tracks;
    private int numTrack;

    public Library(Set<Track> tracks, int numTrack) {
        this.tracks = tracks;
        this.numTrack = numTrack;
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    public int getNumTrack() {
        return numTrack;
    }

    public void setNumTrack(int numTrack) {
        this.numTrack = numTrack;
    }

    public void addTrack(Track t){
        if(!tracks.contains(t))
            if(tracks.add(t))
                numTrack++;
        }

    public void removeTrack(Track t){
        if(tracks.contains(t))
            if(tracks.remove(t))
                numTrack--;
    }

    public void updateTrack(Track oldT,Track newT){
        if (tracks.remove(oldT)) {
            tracks.add(newT);
        }

    }
}
