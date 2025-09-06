package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Test class for the Wall class, covering collision detection and color change.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WallTest {

    private Wall wall;
    private Ball ball;
    public App app;

    @BeforeAll
    public void setUpOnce() throws InterruptedException {
        app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        Thread.sleep(1000);
        app.noLoop();
    }

    @BeforeEach
    public void setUp() {
        // Initialize a wall at position (100, 100) with color 1
        wall = new Wall(100, 100, 1);
        // Initialize a ball at position (90, 90) with color 2
        ball = new Ball(90, 90, 2, null);
    }

    /**
     * Tests collision detection when the ball is colliding with the wall.
     */
    @Test
    public void testCollidesWithBall_Collision() {
        boolean collides = wall.collidesWithBall(ball);
        assertTrue(collides);
    }

    /**
     * Tests collision detection when the ball is not colliding with the wall.
     */
    @Test
    public void testCollidesWithBall_NoCollision() {
        ball.setCenterXPosition(200);
        ball.setCenterYPosition(200);
        boolean collides = wall.collidesWithBall(ball);
        assertFalse(collides);
    }

    /**
     * Tests that the ball changes color upon collision with a colored wall.
     */
    @Test
    public void testHandleCollision_ColorChange() {
        wall.handleCollision(ball);
        assertEquals(wall.getColor(), ball.getColor());
    }

    /**
     * Tests that the ball's velocity is reflected upon collision.
     */
    @Test
    public void testHandleCollision_VelocityReflection() {
        ball.setVelocity(new PVector(2, 2));
        wall.handleCollision(ball);

        // Depending on overlap, one of the velocities should be reversed
        assertTrue(ball.getVelocity().x == -2 || ball.getVelocity().y == -2);
    }
}