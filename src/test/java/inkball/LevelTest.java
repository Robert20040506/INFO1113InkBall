package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for the Level class, covering initialization and property access.
 */
public class LevelTest {

    /**
     * Tests that a level is initialized with correct parameters.
     */
    @Test
    public void testLevelInitialization() {
        String layout = "level1.txt";
        int time = 120;
        int spawnInterval = 10;
        float scoreIncreaseModifier = 1.0f;
        float scoreDecreaseModifier = 1.0f;
        List<String> balls = Arrays.asList("orange", "blue");

        Level level = new Level(layout, time, spawnInterval, scoreIncreaseModifier, scoreDecreaseModifier, balls);

        assertEquals(layout, level.getLayout());
        assertEquals(time, level.getTime());
        assertEquals(spawnInterval, level.getSpawnInterval());
        assertEquals(scoreIncreaseModifier, level.getScoreIncreaseModifier(), 0.001);
        assertEquals(scoreDecreaseModifier, level.getScoreDecreaseModifier(), 0.001);
        assertEquals(balls, level.getBalls());
    }
}