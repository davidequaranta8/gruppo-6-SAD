package models;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrackTest {

    private Track track;

    @BeforeEach
    public void setUp() {
        track = new Track("Test Title", "Test Author", GenreEnum.ROCK, 2023, TagEnum.Preferiti);
        track.setLength(3.50);
        track.setId(1);
    }

    @Test
    public void testTrackCreation() {
        assertEquals("Test Title", track.getTitle());
        assertEquals("Test Author", track.getAuthor());
        assertEquals(GenreEnum.ROCK, track.getGenre());
        assertEquals(2023, track.getYear());
        assertEquals(TagEnum.Preferiti, track.getTag());
        assertEquals(3.50, track.getLength());
        assertEquals(1, track.getId());
        assertEquals(0, track.getCountPlayed());
    }

    @Test
    public void testUpdateTrack() {
        track.updateTrack("New Title", "New Author", GenreEnum.POP, 2024, TagEnum.Allenamento);
        
        assertEquals("New Title", track.getTitle());
        assertEquals("New Author", track.getAuthor());
        assertEquals(GenreEnum.POP, track.getGenre());
        assertEquals(2024, track.getYear());
        assertEquals(TagEnum.Allenamento, track.getTag());
    }

    @Test
    public void testIncrementCountPlayed() {
        assertEquals(0, track.getCountPlayed());
        track.incrementCountPlayed();
        assertEquals(1, track.getCountPlayed());
        track.incrementCountPlayed();
        assertEquals(2, track.getCountPlayed());
    }

    @Test
    public void testEqualsAndHashCode() {
        Track track2 = new Track("Test Title", "Test Author", GenreEnum.ROCK, 2023, TagEnum.Preferiti);
        track2.setId(1);
        
        Track track3 = new Track("Different Title", "Test Author", GenreEnum.ROCK, 2023, TagEnum.Preferiti);
        track3.setId(2);
        
        // Due tracce con stesso ID dovrebbero essere uguali
        assertEquals(track, track2);
        assertEquals(track.hashCode(), track2.hashCode());
        
        // Tracce con ID diverso sono tracce diverse
        assertNotEquals(track, track3);
    }
}
