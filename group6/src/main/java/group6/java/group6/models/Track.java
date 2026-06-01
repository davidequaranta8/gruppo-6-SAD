package group6.java.group6.models;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;

public class Track {
    private int id;
    private String title;
    private String author;
    private double length;
    private GenreEnum genre;
    private int year;
    private TagEnum tag;
    private int countPlayed;
    private String filePath; //file path of the track on the disk: music/title_track

    // Costruttore: id non c'è perchè viene settato quando la track è salvata nel db , altrimenti non sapremmo quale id ha quando la costruiamo
    public Track(String title, String author, GenreEnum genre, int year, TagEnum tag){
        this.title = title;
        this.author = author;
        this.length = 0.0; // lo inizializziamo a 0.0 anche se il valore viene inizializzato con la set siccome lo preleviamo dal file
        this.genre = genre;
        this.year = year;
        this.tag = tag;
        countPlayed = 0;
    }


    public int getCountPlayed() {
        return countPlayed;
    }

    public int getId() { return id;    }

    public String getTitle() { return title;    }

    public String getAuthor() { return author;    }

    public double getLength() { return length;    }

    public GenreEnum getGenre(){ return genre;    }

    public int getYear() { return year;    }

    public TagEnum getTag() {
        return tag;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setTag(TagEnum tag) {
        this.tag = tag;
    }

   public void setId(int id) { this.id = id;    }

    public void setTitle(String title) { this.title = title;    }

    public void setAuthor(String author) { this.author = author;    }

    public void setLength(double length) { this.length = length;    }

    public void setGenre(GenreEnum genre) { this.genre = genre;    }

    public void setYear(int year) { this.year = year;    }

    public void setCountPlayed(int countPlayed) {
        this.countPlayed = countPlayed;
    }

    public void incrementCountPlayed(){
        countPlayed++;
    }

    public void updateTrack(String title, String author, double length, GenreEnum genre, int year, TagEnum tag){
        setAuthor(author);
        setGenre(genre);
        setLength(length);
        setYear(year);
        setTitle(title);
        setTag(tag);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return id == track.id;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", length=" + length +
                ", genre=" + genre +
                ", year=" + year +
                ", tag=" + tag +
                ", countPlayed=" + countPlayed +
                '}';
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }


}