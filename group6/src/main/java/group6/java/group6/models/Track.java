package group6.java.group6.models;

import group6.java.group6.enumerations.Genre;

public class Track {
    private int id;
    private String title;
    private String author;
    private double length;
    private Genre genre;
    private int year;
    private String tag;
    private int countPlayed;

    // Costruttore
    public Track(int id, String title, String author, double length, Genre genre, int year,String tag ){
        this.id = id;
        this.title = title;
        this.author = author;
        this.length = length;
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

    public Genre getGenre(){ return genre;    }

    public int getYear() { return year;    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setId(int id) { this.id = id;    }

    public void setTitle(String title) { this.title = title;    }

    public void setAuthor(String author) { this.author = author;    }

    public void setLength(double length) { this.length = length;    }

    public void setGenre(Genre genre) { this.genre = genre;    }

    public void setYear(int year) { this.year = year;    }

    public void incrementCountPlayed(){
        countPlayed++;
    }


}
