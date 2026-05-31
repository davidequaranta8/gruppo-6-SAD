package group6.java.group6.models;

import java.util.Set;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.Genre;
import group6.java.group6.enumerations.TagEnum;
;

public class LibraryFacade {
    private static LibraryFacade instance;
    private final ConcreteLibrary library;
    private final TrackDao        trackDao;

    private LibraryFacade() {
        this.library = ConcreteLibrary.getInstance(); // Singleton
        this.trackDao = new TrackDao(); // Inizializza il DAO
        this.loadTracksFromDb(); // Carica le tracce esistenti dal database nella libreria
    }

    // Singleton della Facade stessa. 
    public static synchronized LibraryFacade getInstance() {
        if (instance == null) {
            instance = new LibraryFacade();
        }
        return instance;
    }

    private void loadTracksFromDb() {
        trackDao.getAll().forEach(library::addTrack);
    }
    //  US-01 — Aggiungere una traccia
    
    public void addTrack(String title, String author, double length, Genre genre, int year, TagEnum tag) {
        // 1. Istanzia direttamente il brano usando il costruttore base
        tag = tag != null ? tag : TagEnum.NONE; // Se tag è null, assegna un valore di default (ad esempio NONE)
        Track track = new Track(title, author, length, genre, year, tag);
        trackDao.save(track);
        // 2. Lo aggiunge alla libreria
        library.addTrack(track); 
    }

    //  US-02 — Modificare una traccia
    
    public void updateTrack(Track track, String newTitle, String newAuthor, double length, Genre genre, int year, TagEnum tag) {
        tag = tag != null ? tag : TagEnum.NONE; 
        track.updateTrack(newTitle, newAuthor, length, genre, year, tag);
        trackDao.update(track);     // persiste le modifiche sul DB
        library.updateTrack(track, newTitle, newAuthor, length, genre, year, tag); // aggiorna la traccia 
    }

    //  US-03 — Eliminare una traccia
    
    public void removeTrack(Track track) {
        trackDao.delete(track);      // rimuove dal DB
        library.removeTrack(track);  // rimuove dalla libreria
    }

    //  US-04 — Visualizzare tutte le tracce
    
    public Set<Track> getAllTracks() {
        return library.getTracks();
    }
    
}
