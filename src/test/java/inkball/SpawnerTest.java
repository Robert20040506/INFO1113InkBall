package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Test class for the Spawner class, covering basic initialization.
 */
public class SpawnerTest {

    /**
     * Tests that a spawner is initialized at the correct position.
     */
    @Test
    public void testSpawnerInitialization() {
        Spawner spawner = new Spawner(100, 200);

        assertEquals(100, spawner.getCenterXPosition(), 0.001);
        assertEquals(200, spawner.getCenterYPosition(), 0.001);
    }
}