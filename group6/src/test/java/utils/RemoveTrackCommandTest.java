package utils;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.exceptions.DuplicatePlaylistException;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import group6.java.group6.utils.RemoveTrackCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RemoveTrackCommandTest {

    private Playlist playlist;
    private Track track;
    private RemoveTrackCommand command;
    private TrackDao trackDao = new TrackDao();

    @BeforeEach
    public void setUp() throws DuplicatePlaylistException, DuplicateTitleTrackException {
        playlist = PlaylistManager.getInstance().createPlaylist("Test Remove Command Playlist");
        track = new Track("Track Command 2", "Author Command 2", GenreEnum.ROCK, 2020);
        trackDao.save(track);
        
        // Aggiungiamo prima la traccia
        PlaylistManager.getInstance().addTrackToPlaylist(playlist, track);
        
        command = new RemoveTrackCommand(track, playlist);
    }

    @AfterEach
    public void tearDown() {
        PlaylistManager.getInstance().deletePlaylist(playlist);
        trackDao.delete(track);
    }

    @Test
    public void testExecuteAndUndo() {
        assertTrue(playlist.getTracks().contains(track)); // verifica precondizione
        
        // Test Execute
        command.execute();
        assertFalse(playlist.getTracks().contains(track));
        
        // Test Undo
        command.undo();
        assertTrue(playlist.getTracks().contains(track));
    }
}
