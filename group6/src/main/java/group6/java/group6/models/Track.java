package group6.java.group6.models;

import group6.java.group6.enumerations.Genre;

public class Track {
    private int id;
    private String title;
    private String author;
    private double length;
    private Genre genre;
    private int year;
    private boolean favourite;
    private boolean explicit;
    private boolean newRelease;

    // Costruttore
    public Track(int id, String title, String author, double length, String genre, int year, boolean favourite, boolean explicit, boolean newRelease){
        this.id = id;
        this.title = title;
        this.author = author;
        this.length = length;
        this.genre = genre;
        this.year = year;
        this.favourite = favourite;
        this.explicit = explicit;
        this.newRelease = newRelease;
    }

    // Getter e Setter

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getLength() {
        return length;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public boolean isNewRelease() {
        return newRelease;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public void setNewRelease(boolean newRelease) {
        this.newRelease = newRelease;
    }
}
