package dao;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TrackDaoTest {
    
    private TrackDao trackDao = new TrackDao();

    @AfterEach
    public void tearDown() {
        trackDao.deleteAll();
    }

    @Test
    public void saveTrackTest(){
        Track track = new Track("track 1 " , "mj"  , GenreEnum.METAL , 2008 , TagEnum.Preferiti);
        track.setLength(3.29);
        try {
            trackDao.save(track);
        } catch (DuplicateTitleTrackException e) {
            throw new RuntimeException(e);
        }
        Optional<Track> trackRead = trackDao.get(track.getId());
        assertTrue(trackRead.isPresent());
        assertEquals(track, trackRead.get());
    }

    @Test
    public void deleteTrackTest(){
        Track track = new Track("track 1 " , "mj"   , GenreEnum.METAL , 2008 , TagEnum.Preferiti);
        track.setLength(3.29);
        try {
            trackDao.save(track);
        } catch (DuplicateTitleTrackException e) {
            throw new RuntimeException(e);
        }
        trackDao.delete(track);
        //try to read the deleted track so we expect an empy optional
        Optional<Track> trackRead = trackDao.get(track.getId());
        assertFalse(trackRead.isPresent());
    }

    @Test
    public void updateTrackTest(){
        Track track = new Track("track 1 " , "mj"   , GenreEnum.METAL , 2008 , TagEnum.Preferiti);
        track.setLength(3.29);
        try {
            trackDao.save(track);
        } catch (DuplicateTitleTrackException e) {
            throw new RuntimeException(e);
        }
        track.setAuthor("author 1"); //update track
        trackDao.update(track);
        Optional<Track> trackRead = trackDao.get(track.getId());
        assertTrue(trackRead.isPresent());
        //The author must be changed: author 1
        assertEquals(trackRead.get().getAuthor(),track.getAuthor());
    }

    @Test
    public void insertDuplicateTrackTest() {
        Track track1 = new Track("Same Title", "Author 1", GenreEnum.POP, 2020, TagEnum.Allenamento);
        Track track2 = new Track("Same Title", "Author 1", GenreEnum.ROCK, 2021, TagEnum.Chill);

        try {
            ConcreteLibrary.getInstance().addTrack(track1);
        } catch (DuplicateTitleTrackException e) {
            fail("Il primo inserimento non dovrebbe lanciare eccezione");
        }

        assertThrows(DuplicateTitleTrackException.class, () -> {
            ConcreteLibrary.getInstance().addTrack(track2);
        });
    }

}
