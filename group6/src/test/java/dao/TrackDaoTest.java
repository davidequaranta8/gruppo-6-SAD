package dao;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TrackDaoTest {
    private TrackDao trackDao = new TrackDao();

    @Test
    public void saveTrackTest(){
        Track track = new Track("track 1 " , "mj" , 3.29  , GenreEnum.METAL , 2008 , TagEnum.Preferiti);
        trackDao.save(track);
        Optional<Track> trackRead = trackDao.get(track.getId());
        assertTrue(trackRead.isPresent());
        assertEquals(track, trackRead.get());

        trackDao.deleteAll();
    }

    @Test
    public void deleteTrackTest(){
        Track track = new Track("track 1 " , "mj" , 3.29  , GenreEnum.METAL , 2008 , TagEnum.Preferiti);
        trackDao.save(track);
        trackDao.delete(track);
        //try to read the deleted track so we expect an empy optional
        Optional<Track> trackRead = trackDao.get(track.getId());
        assertFalse(trackRead.isPresent());

        trackDao.deleteAll();
    }

    @Test
    public void updateTrackTest(){
        Track track = new Track("track 1 " , "mj" , 3.29  , GenreEnum.METAL , 2008 , TagEnum.Preferiti);
        trackDao.save(track);
        track.setAuthor("author 1"); //update track
        trackDao.update(track);
        Optional<Track> trackRead = trackDao.get(track.getId());
        assertTrue(trackRead.isPresent());
        //The author must be changed: author 1
        assertEquals(trackRead.get().getAuthor(),track.getAuthor());
        trackDao.deleteAll();
    }


}
