package player;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.models.Track;
import group6.java.group6.player.ShuffleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShuffleStrategyTest {

    private ShuffleStrategy strategy;
    private List<Track> originalTracks;
    private Track t1, t2, t3, t4, t5;

    @BeforeEach
    public void setUp() {
        strategy = new ShuffleStrategy();
        t1 = new Track("Track 1", "Author 1", GenreEnum.ROCK, 2020);
        t1.setId(1);
        t2 = new Track("Track 2", "Author 2", GenreEnum.ROCK, 2020);
        t2.setId(2);
        t3 = new Track("Track 3", "Author 3", GenreEnum.ROCK, 2020);
        t3.setId(3);
        t4 = new Track("Track 4", "Author 4", GenreEnum.ROCK, 2020);
        t4.setId(4);
        t5 = new Track("Track 5", "Author 5", GenreEnum.ROCK, 2020);
        t5.setId(5);
        originalTracks = Arrays.asList(t1, t2, t3, t4, t5);
    }

    @Test
    public void testBuildQueue() {
        List<Track> shuffledQueue = strategy.buildQueue(originalTracks, t3);
        
        // Stessa dimensione
        assertEquals(originalTracks.size(), shuffledQueue.size());
        
        // Tutti gli elementi presenti
        assertTrue(shuffledQueue.containsAll(originalTracks));
        
        // La traccia corrente passata dovrebbe essere al primo posto
        assertEquals(t3, shuffledQueue.get(0));
    }

    @Test
    public void testNextAndPrevTrack() {
        // La ShuffleStrategy mantiene uno stato interno delle tracce già suonate.
        strategy.buildQueue(originalTracks, t1);
        
        // Riproduciamo una traccia
        Track next1 = strategy.nextTrack(originalTracks, t1);
        assertNotNull(next1);
        assertTrue(originalTracks.contains(next1));
        assertNotEquals(t1, next1); // Non dovrebbe riprodurre subito quella attuale
        
        // Riproduciamo un'altra traccia
        Track next2 = strategy.nextTrack(originalTracks, next1);
        assertNotNull(next2);
        assertTrue(originalTracks.contains(next2));
        assertNotEquals(next1, next2);
        
        // Test prev track: dovrebbe tornare alla next1 (l'ultima suonata prima della corrente next2)
        Track prev = strategy.prevTrack(originalTracks, next2);
        assertEquals(next1, prev);
        
        // Un altro prev track torna a t1
        Track prev2 = strategy.prevTrack(originalTracks, next1);
        assertEquals(t1, prev2);
    }
}
