package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Test class for the GameObject class, covering basic functionality like distance calculation.
 */
public class GameObjectTest {

    /**
     * Tests calculating the distance between two game objects.
     */
    @Test
    public void testDistanceTo() {
        GameObject obj1 = new GameObject(0, 0) {};
        GameObject obj2 = new GameObject(3, 4) {};

        float distance = obj1.distanceTo(obj2);
        assertEquals(5.0, distance, 0.001);
    }

    /**
     * Tests calculating distance when both objects are at the same position.
     */
    @Test
    public void testDistanceTo_SamePosition() {
        GameObject obj1 = new GameObject(10, 10) {};
        GameObject obj2 = new GameObject(10, 10) {};

        float distance = obj1.distanceTo(obj2);
        assertEquals(0.0, distance, 0.001);
    }
}