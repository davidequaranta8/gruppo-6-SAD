package group6.java.group6.models;
import java.util.HashMap;
import java.util.Map;


public class Playlist {
    private String title; //title of playlist
    private int countPlayed; //to track how many times playlist was played
    private Map<Integer ,Track> tracks; //(int : id_track ; Track: track associated with id_track)

    public Playlist(String title) {
        this.title = title;
        this.countPlayed=0;
        tracks = new HashMap<Integer, Track>();
    }

    public String getTitle() {
        return title;
    }


    public int getCountPlayed() {
        return countPlayed;
    }

    public Map<Integer, Track> getTracks() {
        return tracks;
    }
}
