package utils;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.exceptions.DuplicatePlaylistException;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import group6.java.group6.utils.AddTrackCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddTrackCommandTest {

    private Playlist playlist;
    private Track track;
    private AddTrackCommand command;
    private TrackDao trackDao = new TrackDao();

    @BeforeEach
    public void setUp() throws DuplicatePlaylistException, DuplicateTitleTrackException {
        playlist = PlaylistManager.getInstance().createPlaylist("Test Add Command Playlist");
        track = new Track("Track Command 1", "Author Command 1", GenreEnum.ROCK, 2020);
        trackDao.save(track);
        command = new AddTrackCommand(track, playlist);
    }

    @AfterEach
    public void tearDown() {
        PlaylistManager.getInstance().deletePlaylist(playlist);
        trackDao.delete(track);
    }

    @Test
    public void testExecuteAndUndo() {
        // Test Execute
        command.execute();
        assertTrue(playlist.getTracks().contains(track));
        
        // Test Undo
        command.undo();
        assertFalse(playlist.getTracks().contains(track));
    }
}

