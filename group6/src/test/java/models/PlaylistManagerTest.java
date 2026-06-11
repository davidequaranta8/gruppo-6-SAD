package models;
import group6.java.group6.dao.TrackDao;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import org.junit.jupiter.api.BeforeEach;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.exceptions.DuplicatePlaylistException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;

public class PlaylistManagerTest {

    private PlaylistManager manager;
    private String title;
    private TrackDao trackDao = new TrackDao();

    @BeforeEach
    public void setUp() {
        manager = PlaylistManager.getInstance();
        title = "TestPlaylist";
    }

    @Test
    void testCreateAndDelete() throws DuplicatePlaylistException {
        Playlist p = manager.createPlaylist(title);

        try {
            assertTrue(manager.getPlaylists().contains(p));
            assertEquals(title, p.getTitle());
        } finally {
            manager.deletePlaylist(p);
        }
    }

    @Test
    void testDuplicatePlaylistException() throws DuplicatePlaylistException {
        Playlist p = manager.createPlaylist(title);

        try {
            assertThrows(DuplicatePlaylistException.class, () -> {
                manager.createPlaylist(title);
            });
        } finally {
            manager.deletePlaylist(p);
        }
    }

    @Test
    void testRenamePlaylist() throws DuplicatePlaylistException {
        Playlist p = manager.createPlaylist(title);

        try {
            String newTitle = "UpdatedTestPlaylist";
            manager.renamePlaylist(p, newTitle);
            assertEquals(newTitle, p.getTitle());
        } finally {
            manager.deletePlaylist(p);
        }
    }

    @Test
    void testSetSelectedPlaylist() throws DuplicatePlaylistException {
        Playlist p = manager.createPlaylist(title);

        try {
            manager.setSelectedPlaylist(p);
            assertEquals(p, manager.getSelectedPlaylist());
        } finally {
            manager.setSelectedPlaylist(null);
            manager.deletePlaylist(p);
        }
    }
    
    @Test
    void testAddTrackToPlaylist() throws DuplicatePlaylistException, DuplicateTitleTrackException {
        Playlist p = manager.createPlaylist(title);
        Track track = new Track("Canzone di Test", "Autore di Test", GenreEnum.ROCK, 2020);
        try {

            trackDao.save(track);
            assertDoesNotThrow(() -> manager.addTrackToPlaylist(p, track));
        } finally {
            manager.deletePlaylist(p);
            trackDao.delete(track);
        }
    }

    @Test
    void testRemoveTrackFromPlaylist() throws DuplicatePlaylistException, DuplicateTitleTrackException {
        Playlist p = manager.createPlaylist(title);
        Track track = new Track("Canzone di Test", "Autore di Test", GenreEnum.BLUES, 2020);
        try {
            trackDao.save(track);
            manager.addTrackToPlaylist(p, track); 

            assertDoesNotThrow(() -> {manager.removeTrackFromPlaylist(p, track); });
            
        } finally {
            manager.deletePlaylist(p);
            trackDao.delete(track);
        }
    }
}