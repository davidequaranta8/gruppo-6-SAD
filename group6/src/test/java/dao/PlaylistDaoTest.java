package dao;

import group6.java.group6.dao.PlaylistDao;
import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class PlaylistDaoTest {
    PlaylistDao playlistDao = new PlaylistDao();
    TrackDao trackDao = new TrackDao();


    @Test
    public void insertPlaylistTest() {
        Playlist playlist = new Playlist("Sample playlist");
        /*saves the sample playlist*/
        playlistDao.save(playlist);
        /*get the playlist just saved*/
        Optional<Playlist> playlistSaved = playlistDao.get(playlist.getId());
        //be sure the playlist is present
        assertTrue(playlistSaved.isPresent());
        //be sure the playlist got is same of one we just saved
        assertEquals(playlistSaved.get(), playlist);
        //finally delete the record to keep clean db after each test
        playlistDao.delete(playlist);
    }

    @Test
    public void getAllPlaylistTest() {
        Set<Playlist> playlistSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Playlist playlist = new Playlist("Sample playlist" + i);
            playlistDao.save(playlist);
            playlistSet.add(playlist);
        }
        List<Playlist> playlistSetGot = new ArrayList<>(playlistSet);
        List<Playlist> playlistSetArray = new ArrayList<>(playlistDao.getAll());
        //order both lists for id since when creating List it does not guarantee any order
        playlistSetGot.sort(Comparator.comparing(Playlist::getId));
        playlistSetArray.sort(Comparator.comparing(Playlist::getId));

        assertEquals(10, playlistSetGot.size());
        //assert each element saved and got is same
        for (int i = 0 ; i < playlistSetGot.size() ;i++ ){
            assertEquals(playlistSetArray.get(i), playlistSetGot.get(i));
        }
        //finally delete all
        playlistDao.deleteAll();
    }

    @Test
    public void deleteTest(){
        Playlist playlist = new Playlist("Sample playlist");
        //save playlist
        playlistDao.save(playlist);
        //delete the playlist
        playlistDao.delete(playlist);
        //try to see if we query db for the playlist deleted we find something
        Optional<Playlist> playlistSaved = playlistDao.get(playlist.getId());
        //be sure we don't find the playlist
        assertFalse(playlistSaved.isPresent());


    }

    @Test
    public void updateTest(){
        Playlist playlist = new Playlist("Sample playlist");
        playlistDao.save(playlist);
        playlist.setTitle("Updated title");
        playlistDao.update(playlist);
        Optional<Playlist> playlistSaved = playlistDao.get(playlist.getId());
        assertTrue(playlistSaved.isPresent());
        assertEquals(playlist.getTitle(), playlistSaved.get().getTitle());

        playlistDao.deleteAll();
    }

    @Test
    public void incrementCountPlayedTest(){
        Playlist playlist = new Playlist("Sample playlist");
        //save the playlist
        playlistDao.save(playlist);
        //increment count played: we expect it increments both for model and in record db
        playlistDao.incrementCountPlayed(playlist);
        //get the saved playlist
        Optional<Playlist> playlistSaved = playlistDao.get(playlist.getId());
        //check playlist is saved
        assertTrue(playlistSaved.isPresent());
        //check playlist saved is the one in model we saved
        assertEquals(playlistSaved.get(), playlist);
        //check model and record both were updated
        assertEquals(playlistSaved.get().getCountPlayed() , playlist.getCountPlayed());
        //check update is correctly done
        assertEquals(1,playlist.getCountPlayed());
        //delete all finally
        playlistDao.deleteAll();
    }


    @Test
    public void removeTrackTest(){
    Playlist playlist = new Playlist("Sample playlist");
    Track track = new Track("track 1 " , "mj" , 3.29  , GenreEnum.METAL , 2008 , TagEnum.STARRED);
    //save the playlist
    playlistDao.save(playlist);
    //save the track
    trackDao.save(track);
    //add track to playlist
    playlistDao.addTrack(playlist, track);

    //remove the track added above
    playlistDao.removeTrack(playlist, track);

    //assert the playlist no more contains the track
    playlistDao.loadAllTracks(playlist);
    assertTrue(playlist.getTracks().isEmpty());


    //clear db
    playlistDao.deleteAll();
    trackDao.deleteAll();

    }



    @Test
    public void loadAllTracksTest(){
        Playlist playlist = new Playlist("Sample playlist 1");
        Track track = new Track("track 1 " , "mj" , 3.29  , GenreEnum.METAL , 2008 , TagEnum.STARRED);

        //save first the track and the playlist to avoid references error
        trackDao.save(track);
        playlistDao.save(playlist);

        //save track in playlist
        playlistDao.addTrack(playlist, track);

        //get all the tracks of the playlist
        playlistDao.loadAllTracks(playlist);

        assertTrue(playlist.getTracks().contains(track));

        //clean db
        trackDao.deleteAll();
        playlistDao.deleteAll();
    }





    }


