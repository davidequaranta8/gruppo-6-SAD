package models;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.exceptions.DuplicatePlaylistException;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistManagerTest {

    private PlaylistManager manager;
    private String title;
    private TrackDao trackDao = new TrackDao();
    
    // Campi per la pulizia nel tearDown
    private Playlist p;
    private Track track;

    @BeforeEach
    public void setUp() {
        manager = PlaylistManager.getInstance();
        title = "TestPlaylist";
        p = null;
        track = null;
    }

    @AfterEach
    public void tearDown() {
        if (p != null) {
            manager.deletePlaylist(p);
        }
        if (track != null) {
            trackDao.delete(track);
        }
        manager.setSelectedPlaylist(null);
    }

    @Test
    void testCreateAndDelete() throws DuplicatePlaylistException {
        p = manager.createPlaylist(title);

        assertTrue(manager.getPlaylists().contains(p));
        assertEquals(title, p.getTitle());
    }

    @Test
    void testDuplicatePlaylistException() throws DuplicatePlaylistException {
        p = manager.createPlaylist(title);

        assertThrows(DuplicatePlaylistException.class, () -> {
            manager.createPlaylist(title);
        });
    }

    @Test
    void testRenamePlaylist() throws DuplicatePlaylistException {
        p = manager.createPlaylist(title);

        String newTitle = "UpdatedTestPlaylist";
        manager.renamePlaylist(p, newTitle);
        assertEquals(newTitle, p.getTitle());
    }

    @Test
    void testSetSelectedPlaylist() throws DuplicatePlaylistException {
        p = manager.createPlaylist(title);

        manager.setSelectedPlaylist(p);
        assertEquals(p, manager.getSelectedPlaylist());
    }
    
    @Test
    void testAddTrackToPlaylist() throws DuplicatePlaylistException, DuplicateTitleTrackException {
        p = manager.createPlaylist(title);
        track = new Track("Canzone di Test", "Autore di Test", GenreEnum.ROCK, 2020);
        
        trackDao.save(track);
        assertDoesNotThrow(() -> manager.addTrackToPlaylist(p, track));
    }

    @Test
    void testRemoveTrackFromPlaylist() throws DuplicatePlaylistException, DuplicateTitleTrackException {
        p = manager.createPlaylist(title);
        track = new Track("Canzone di Test", "Autore di Test", GenreEnum.BLUES, 2020);
        
        trackDao.save(track);
        manager.addTrackToPlaylist(p, track); 

        assertDoesNotThrow(() -> {manager.removeTrackFromPlaylist(p, track); });
    }
}