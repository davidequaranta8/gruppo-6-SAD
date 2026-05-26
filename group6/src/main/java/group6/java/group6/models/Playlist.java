package group6.java.group6.models;

import java.util.LinkedHashMap;
import java.util.Map;

/* Playlist model */

public class Playlist {
    private String title; //title of playlist
    private int countPlayed; //to track how many times playlist was played
    private Map<Integer ,Track> tracks; //(int : id_track ; Track: track associated with id_track)

    public Playlist(String title) {
        this.title = title;
        this.countPlayed=0;
        tracks = new LinkedHashMap<Integer, Track>();
    }

    public String getTitle() {
        return title;
    }

    /*Get how many times a playlist was played*/
    public int getCountPlayed() {
        return countPlayed;
    }


    /*Get all the tracks of a playlist*/
    public Map<Integer, Track> getTracks() {
        return tracks;
    }

    /*Add a track in the playlist*/
    public void addTrack(Track track){
        tracks.put(track.getId(),track);
    }


    //TODO: Implement removeTrack method of class playlist

}
