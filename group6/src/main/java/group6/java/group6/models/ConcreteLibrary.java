package group6.java.group6.models;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.exceptions.DuplicateTitleTrackException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public class ConcreteLibrary implements Library{
    private Set<Track> tracks;
    private static ConcreteLibrary instance; // istanza condivisa
    private List<LibraryObserver> observers; // Lista degli osservatori registrati
    private TrackDao trackDao = new TrackDao();

    private ConcreteLibrary() {
        this.tracks = trackDao.getAll();
        observers  = new ArrayList<>();
    }

    public static synchronized ConcreteLibrary getInstance() {
        if (instance == null) {
            instance = new ConcreteLibrary();
        }
        return instance;
    }

    // Getter e Setter
    public Set<Track> getTracks() {
        return Collections.unmodifiableSet(tracks); // restituisce un Set immutabile
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    // Metodi per aggiungere e rimuovere tracce
    public void addTrack(Track t) throws DuplicateTitleTrackException {
        trackDao.save(t); //salva nel db prima di aggiungere al set dal momento che il db setta l'id
        tracks.add(t);
        notifyObservers(); // quando aggiungo una traccia è necessario notificare il MainController per fargli aggiornare la vista
    }


    public void updateTrack(Track t) {
        trackDao.update(t);
        notifyObservers();
    }

    public void removeTrack(Track t){
        tracks.remove(t);
        trackDao.delete(t);
        notifyObservers();
    }

    // Metodi del pattern Observer
    @Override
    public void addObserver(LibraryObserver observer) { // aggiunge un observer alla lista
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    @Override
    public void removeObserver(LibraryObserver observer) { // rimuove un observer alla lista
        observers.remove(observer);
    }
    @Override
    public void notifyObservers() { // notifica gli observer che la libreria è stata modificata
        for (LibraryObserver observer : observers) {
            observer.onLibraryChanged();
        }
    }

}
