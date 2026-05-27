package group6.java.group6.models;

import java.util.LinkedHashMap;
import java.util.Map;

/* Playlist model */

public class Playlist {
    private int id; /*unique identifier of a playlist*/
    private String title; //title of playlist
    private int countPlayed; //to track how many times playlist was played
    private Map<Integer ,Track> tracks; //(int : id_track ; Track: track associated with id_track)

    public Playlist(String title) {
        this.title = title;
        this.countPlayed=0;
        tracks = new LinkedHashMap<Integer, Track>();
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
    public Map<Integer, Track> getTracks() {
        return tracks;
    }

    /*Add a track in the playlist*/
    public void addTrack(Track track){
        tracks.put(track.getId(),track);
    }


    /*Increment count played of 1*/
    public void incrementCountPlayed(){
        countPlayed++;
    }


//TODO: Implement method for removing a track

}
