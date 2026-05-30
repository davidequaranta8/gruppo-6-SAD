package dao;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.Genre;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.basic.BasicSliderUI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TrackDaoTest {
    private TrackDao trackDao = new TrackDao();

    @Test
    public void saveTrackTest(){
        Track track = new Track("track 1 " , "mj" , 3.29  , Genre.METAL , 2008 , TagEnum.STARRED);
        trackDao.save(track);
        Optional<Track> trackRead = trackDao.get(track.getId());
        assertTrue(trackRead.isPresent());
        assertEquals(track, trackRead.get());

        trackDao.deleteAll();
    }


}
