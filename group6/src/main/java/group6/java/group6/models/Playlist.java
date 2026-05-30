package group6.java.group6.models;
import java.util.LinkedHashSet;
import java.util.Set;

/* Playlist model */

public class Playlist {
    private int id; /*unique identifier of a playlist*/
    private String title; //title of playlist
    private int countPlayed; //to track how many times playlist was played
    private Set<Track> tracks; //(int : id_track ; Track: track associated

    public Playlist(String title) {
        this.title = title;
        this.countPlayed=0;
        tracks = new LinkedHashSet<Track>();
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountPlayed(int countPlayed) {
        this.countPlayed = countPlayed;
    }

    public String getTitle() {
        return title;
    }

    /*Get how many times a playlist was played*/
    public int getCountPlayed() {
        return countPlayed;
    }


    /*Get all the tracks of a playlist*/
    public Set<Track> getTracks() {
        return tracks;
    }

    /*Add a track in the playlist*/
    public void addTrack(Track track){
        tracks.add(track);
    }


    /*Increment count played of 1*/
    public void incrementCountPlayed(){
        countPlayed++;
    }


    /*Remove a track from a playlist*/
    public void removeTrack(Track track){
        if(tracks.contains(track)){
            tracks.remove(track);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;
        Playlist playlist = (Playlist) o;
        return this.id == playlist.id;
    }

    @Override
    public String toString() {
        return "Playlist with id: "+this.id;
    }

}
