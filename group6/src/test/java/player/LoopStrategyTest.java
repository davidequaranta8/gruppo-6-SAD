package player;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.models.Track;
import group6.java.group6.player.LoopStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoopStrategyTest {

    private LoopStrategy strategy;
    private List<Track> queue;
    private Track t1, t2, t3;

    @BeforeEach
    public void setUp() {
        strategy = new LoopStrategy();
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
        // In LoopStrategy, nextTrack ritorna sempre la traccia corrente
        assertEquals(t1, strategy.nextTrack(queue, t1));
        assertEquals(t2, strategy.nextTrack(queue, t2));
    }

    @Test
    public void testPrevTrack() {
        // In LoopStrategy, prevTrack ritorna sempre la traccia corrente
        assertEquals(t1, strategy.prevTrack(queue, t1));
        assertEquals(t2, strategy.prevTrack(queue, t2));
    }

    @Test
    public void testBuildQueue() {
        List<Track> newQueue = strategy.buildQueue(queue, t1);
        assertEquals(queue.size(), newQueue.size());
        assertEquals(queue, newQueue); // Deve mantenere lo stesso ordine
    }
}
