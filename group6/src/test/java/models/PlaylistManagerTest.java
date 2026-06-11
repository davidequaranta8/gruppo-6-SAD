package models;
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
    void testAddTrackToPlaylist() throws DuplicatePlaylistException {
        Playlist p = manager.createPlaylist(title);
        try {
            Track track = new Track("Canzone di Test", "Autore di Test", GenreEnum.ROCK, 2020);
            
            assertDoesNotThrow(() -> manager.addTrackToPlaylist(p, track));
        } finally {
            manager.deletePlaylist(p);
        }
    }

    @Test
    void testRemoveTrackFromPlaylist() throws DuplicatePlaylistException {
        Playlist p = manager.createPlaylist(title);
        try {
            Track track = new Track("Canzone di Test", "Autore di Test", GenreEnum.BLUES, 2020);       
            manager.addTrackToPlaylist(p, track); 

            assertDoesNotThrow(() -> {manager.removeTrackFromPlaylist(p, track); });
            
        } finally {
            manager.deletePlaylist(p);
        }
    }
}