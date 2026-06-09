package group6.java.group6.models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import group6.java.group6.dao.PlaylistDao;
import group6.java.group6.exceptions.DuplicatePlaylistException;

public class PlaylistManager {
    // ── Singleton ─────────────────────────────────────────────────────────────
    private static PlaylistManager instance;
 
    public static PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }
 
    // ── Stato interno ─────────────────────────────────────────────────────────
    private final Set<Playlist>          playlists   = new LinkedHashSet<>();
    private final List<PlaylistObserver> observers   = new ArrayList<>();
    private final PlaylistDao            playlistDao = new PlaylistDao();
 
    private Playlist selectedPlaylist = null;
 
    private PlaylistManager() {
        loadFromDb();
    }
 
    //Metodi Observer
    public void addObserver(PlaylistObserver observer) {
        observers.add(observer);
    }
 
    public void removeObserver(PlaylistObserver observer) {
        observers.remove(observer);
    }
 
    public void notifyObservers() {
        observers.forEach(PlaylistObserver::onPlaylistChanged);
    }
 
 
    public Playlist createPlaylist(String title) throws DuplicatePlaylistException {
        verifyDuplicateTitle(title, null);
        Playlist p = new Playlist(title);
        playlistDao.save(p);      // genera e imposta l'id nel modello
        playlists.add(p);
        notifyObservers();
        return p;
    }
 
    
    public void deletePlaylist(Playlist playlist) {
        playlistDao.delete(playlist);
        playlists.remove(playlist);
        if (playlist.equals(selectedPlaylist)) {
            selectedPlaylist = null;
        }
        notifyObservers();
    }
 

     public void renamePlaylist(Playlist playlist, String newTitle) throws DuplicatePlaylistException {
        verifyDuplicateTitle(newTitle, playlist);
        playlist.setTitle(newTitle);
        playlistDao.update(playlist);
        notifyObservers();
    }
 
 
    // Aggiunge una traccia alla playlist ed evita duplicati.
    public void addTrackToPlaylist(Playlist playlist, Track track) {
        playlistDao.addTrack(playlist, track);  // aggiorna anche il modello via DAO
        notifyObservers();
    }
 

     public void removeTrackFromPlaylist(Playlist playlist, Track track) {
        playlistDao.removeTrack(playlist, track);
        notifyObservers();
    }
 
    

    public void loadTracksForPlaylist(Playlist playlist) {
        if (playlist.getTracks().isEmpty()) {
            playlistDao.loadAllTracks(playlist);
        }
    }
 
    public Set<Playlist> getPlaylists() {
        return playlists;
    }
 
    
    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }
    

    public void setSelectedPlaylist(Playlist playlist) {
        this.selectedPlaylist = playlist;
        if (playlist != null) {
            loadTracksForPlaylist(playlist);
        }
        notifyObservers();
    }
 
    //  Caricamento iniziale dal DB
    private void loadFromDb() {
        playlists.addAll(playlistDao.getAll());
        // Le tracce di ogni playlist vengono caricate lazy in setSelectedPlaylist()
    }

    public void updateTrackInPlaylists(Track updatedTrack) {
        for (Playlist p : playlists) {
            for (Track t : p.getTracks()) {
                if (t.getId() == updatedTrack.getId() && t != updatedTrack) {
                    t.updateTrack(updatedTrack.getTitle(), updatedTrack.getAuthor(), updatedTrack.getGenre(), updatedTrack.getYear(), updatedTrack.getTag());
                    t.setLength(updatedTrack.getLength());
                    t.setFilePath(updatedTrack.getFilePath());
                    t.setCountPlayed(updatedTrack.getCountPlayed());
                }
            }
        }
        notifyObservers();
    }

    public void removeTrackFromAllPlaylists(Track track) {
        for (Playlist p : playlists) {
            p.removeTrack(track);
        }
        notifyObservers();
    }

    private void verifyDuplicateTitle(String title, Playlist exclude) throws DuplicatePlaylistException {
        for (Playlist p : playlists) {
            if (p != exclude && p.getTitle().equalsIgnoreCase(title)) {
                throw new DuplicatePlaylistException("");
            }
        }
    }
}
