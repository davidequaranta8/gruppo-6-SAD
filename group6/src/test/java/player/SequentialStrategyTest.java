package player;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.models.Track;
import group6.java.group6.player.SequentialStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SequentialStrategyTest {

    private SequentialStrategy strategy;
    private List<Track> queue;
    private Track t1, t2, t3;

    @BeforeEach
    public void setUp() {
        strategy = new SequentialStrategy();
        t1 = new Track("Track 1", "Author 1", GenreEnum.ROCK, 2020);
        t1.setId(1);
        t2 = new Track("Track 2", "Author 2", GenreEnum.ROCK, 2020);
        t2.setId(2);
        t3 = new Track("Track 3", "Author 3", GenreEnum.ROCK, 2020);
        t3.setId(3);
        queue = Arrays.asList(t1, t2, t3);
    }

    @Test
    public void testNextTrack() {
        assertEquals(t2, strategy.nextTrack(queue, t1));
        assertEquals(t3, strategy.nextTrack(queue, t2));
        assertNull(strategy.nextTrack(queue, t3), "La traccia successiva all'ultima deve essere null in modalità sequenziale");
    }

    @Test
    public void testPrevTrack() {
        assertEquals(t2, strategy.prevTrack(queue, t3));
        assertEquals(t1, strategy.prevTrack(queue, t2));
        assertNull(strategy.prevTrack(queue, t1), "La traccia precedente alla prima deve essere null");
    }

    @Test
    public void testBuildQueue() {
        List<Track> newQueue = strategy.buildQueue(queue, t1);
        assertEquals(queue.size(), newQueue.size());
        assertEquals(queue, newQueue); // Deve mantenere lo stesso ordine
    }
}
