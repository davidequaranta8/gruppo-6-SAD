package models;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistTest {

    private Playlist playlist;
    private Track track1;
    private Track track2;

    @BeforeEach
    public void setUp() {
        // ARRANGE: Preparazione dei dati valida per tutti i test
        playlist = new Playlist("My Favorite Songs");

        // Creiamo due tracce distinte per i test (usando i costruttori che hai già)
        track1 = new Track("Track 1", "Author A", 3.5, GenreEnum.POP, 2020, TagEnum.REMEMBER_ME);
    }



    @Test
    public void testAddTrack() {
        // Metodo da testare
        playlist.addTrack(track1);

        // ASSERT
        assertEquals(1, playlist.getTracks().size(), "La dimensione deve essere 1 dopo aver aggiunto una traccia");
        assertTrue(playlist.getTracks().contains(track1), "La playlist deve contenere la traccia appena aggiunta");
    }

    @Test
    public void testRemoveTrack() {
        // Preparazione
        playlist.addTrack(track1);

        // Metodo da testare
        playlist.removeTrack(track1);

        // ASSERT
        assertFalse(playlist.getTracks().contains(track1), "La traccia 1 deve essere stata rimossa");
        assertEquals(0, playlist.getTracks().size(), "La dimensione deve essere tornata a 0");

    }


}