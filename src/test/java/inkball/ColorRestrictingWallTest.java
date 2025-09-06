package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import processing.core.PVector;

/**
 * Test class for the ColorRestrictingWall class, covering collision handling based on ball color.
 */
public class ColorRestrictingWallTest {

    private ColorRestrictingWall verticalWall;
    private ColorRestrictingWall horizontalWall;
    private Ball ball;

    @BeforeEach
    public void setUp() {
        // Initialize vertical and horizontal color-restricting walls
        verticalWall = new ColorRestrictingWall(100, 100, 1, true); // Color 1 (e.g., orange)
        horizontalWall = new ColorRestrictingWall(200, 200, 2, false); // Color 2 (e.g., blue)
    }

    /**
     * Tests that the wall blocks a ball of a different color.
     */
    @Test
    public void testHandleCollision_BlocksDifferentColorBall() {
        ball = new Ball(97, 100, 3, null); // Ball color different from wall color
        ball.setVelocity(new PVector(2, 0));

        verticalWall.handleCollision(ball);

        // Velocity should be reflected
        assertEquals(-2, ball.getVelocity().x, 0.001);
    }

    /**
     * Tests that the wall allows a ball of the same color to pass through.
     */
    @Test
    public void testHandleCollision_AllowsSameColorBall() {
        ball = new Ball(97, 100, 1, null); // Ball color matches wall color
        ball.setVelocity(new PVector(2, 0));
        PVector initialVelocity = ball.getVelocity().copy();

        verticalWall.handleCollision(ball);

        // Velocity should remain the same
        assertEquals(initialVelocity.x, ball.getVelocity().x, 0.001);
    }

    /**
     * Tests that the wall correctly identifies its orientation.
     */
    @Test
    public void testIsVertical() {
        assertTrue(verticalWall.isVertical());
        assertFalse(horizontalWall.isVertical());
    }
}