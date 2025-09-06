package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import processing.core.PApplet;

/**
 * Test class for the Hole class, covering attraction and capture mechanics.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HoleTest {

    private Hole hole;
    private Ball ball;
    private App app;

    @BeforeAll
    public void setUpOnce() throws InterruptedException {
        app = new App();
        App.setInstance(app);
        PApplet.runSketch(new String[] { "App" }, app);
        Thread.sleep(1000);
        app.noLoop();
    }

    /**
     * Tests that the ball is not attracted when it's far from the hole.
     */
    @Test
    public void testHandleAttraction_NoAttraction() {
        // Initialize a hole and a ball
        ball = new Ball(300, 300, 1, null);
        hole = new Hole(100, 100, 2);

        float distance = ball.distanceTo(hole);
        hole.handleAttraction(ball, distance);

        assertFalse(app.getBallsToBeRemoved().contains(ball));
    }

    /**
     * Test when ball.getColor() == 0 (grey)
     */
    @Test
    public void testHandleAttraction_BallColorZero() {
        // Initialize a hole and a ball
        ball = new Ball(100, 100, 0, null); // Color 0 (e.g. grey)
        hole = new Hole(100, 100, 2); // Color 2 (e.g. blue)

        app.currentScore = 0; // Set initial score
        float distance = ball.distanceTo(hole);
        hole.handleAttraction(ball, distance);

        // Ball should be marked for removal
        assertTrue(app.getBallsToBeRemoved().contains(ball));
        // Score should increase because ball color is 0 (grey)
        assertEquals(70, app.currentScore); // From score_increase_from_hole_capture
    }

    /** 
     * Test when hole.color == 0 (grey)
     */
    @Test
    public void testHandleAttraction_HoleColorZero() {
        // Initialize a hole and a ball
        ball = new Ball(100, 100, 2, null); // Color 2 (e.g. blue)
        hole = new Hole(100, 100, 0); // Color 0 (e.g. gray)

        app.currentScore = 0; // Set initial score
        float distance = ball.distanceTo(hole);
        hole.handleAttraction(ball, distance);

        // Ball should be marked for removal
        assertTrue(app.getBallsToBeRemoved().contains(ball));
        // Score should increase because hole color is 0 (grey)
        assertEquals(50, app.currentScore); // From score_increase_from_hole_capture
    }

    /**
     * Test when both ball.getColor() == 0 and hole.color == 0
     */
    @Test
    public void testHandleAttraction_BothColorsZero() {
        // Initialize a hole and a ball
        ball = new Ball(100, 100, 0, null); // Color 0 (e.g. gray)
        hole = new Hole(100, 100, 0); // Color 0 (e.g. gray)

        app.currentScore = 0; // Set initial score
        float distance = ball.distanceTo(hole);
        hole.handleAttraction(ball, distance);

        // Ball should be marked for removal
        assertTrue(app.getBallsToBeRemoved().contains(ball));
        // Score should increase
        assertEquals(70, app.currentScore); // From score_increase_from_hole_capture
    }

    /**
     * Test when ball.getColor() != hole.color and neither is zero
     */
    @Test
    public void testHandleAttraction_DifferentColors_NoZero() {
        // Initialize a hole and a ball
        ball = new Ball(100, 100, 2, null); //Color 2 (e.g. blue)
        hole = new Hole(100, 100, 3); // Color 3 (e.g. green)

        hole.setColor(4);

        app.currentScore = 100; // Set initial score
        float distance = ball.distanceTo(hole);
        hole.handleAttraction(ball, distance);

        // Ball should be marked for removal
        assertTrue(app.getBallsToBeRemoved().contains(ball));
        // Score should decrease
        assertEquals(75, app.currentScore); // 100 - 25 (from score_decrease_from_wrong_hole)
    }

    /**
     * Test when App.currentScore becomes negative
     */
    @Test
    public void testHandleAttraction_ScoreGoesNegative() {
        // Initialize a hole and a ball
        ball = new Ball(110, 110, 1, null); // Color 1 (e.g. orange)
        hole = new Hole(100, 100, 2); // Color 2 (e.g. blue)
        app.currentScore = 10; // Set initial score low

        float distance = ball.distanceTo(hole);
        hole.handleAttraction(ball, distance);

        // Ball should be marked for removal
        assertTrue(app.getBallsToBeRemoved().contains(ball));
        // Score should be set to zero (cannot be negative)
        assertEquals(0, app.currentScore);
    }

    /**
     * Test when ball just outside the tolerance
     */
    @Test
    public void testHandleAttraction_BallOnBoundaryOfTolerance() {
        int tolerance = Ball.RADIUS;
        // Initialize a hole and a ball
        ball = new Ball(110, 110, 1, null);
        hole = new Hole(100, 100, 2);
        ball.centerXPosition = hole.centerXPosition + tolerance;
        ball.centerYPosition = hole.centerYPosition + tolerance;
        float distance = ball.distanceTo(hole);

        hole.handleAttraction(ball, distance);

        // Ball should not be marked for removal (since it's exactly at tolerance)
        assertFalse(app.getBallsToBeRemoved().contains(ball));

        // Now move ball just inside the tolerance
        ball.centerXPosition = hole.centerXPosition + tolerance - 0.01f;
        ball.centerYPosition = hole.centerYPosition + tolerance - 0.01f;
        distance = ball.distanceTo(hole);

        hole.handleAttraction(ball, distance);

        // Ball should be marked for removal
        assertTrue(app.getBallsToBeRemoved().contains(ball));
    }

    /**
     * Test when ball exactly at the center of the hole
     */
    @Test
    public void testHandleAttraction_DistanceZero() {
        // Initialize a hole and a ball
        ball = new Ball(110, 110, 1, null);
        hole = new Hole(100, 100, 2);
        ball.setCenterXPosition(hole.getCenterXPosition());
        ball.setCenterYPosition(hole.getCenterYPosition());
        float distance = ball.distanceTo(hole);

        hole.handleAttraction(ball, distance);

        // Ball should be marked for removal
        assertTrue(app.getBallsToBeRemoved().contains(ball));
    }

    /**
     * Test that the attractive force is calculated correctly
     */
    @Test
    public void testHandleAttraction_AttractiveForce() {
        // Initialize a hole and a ball
        ball = new Ball(110, 110, 1, null);
        hole = new Hole(100, 100, 2);

        float initialVelocityX = ball.getVelocity().x;
        float initialVelocityY = ball.getVelocity().y;
        float distance = ball.distanceTo(hole);

        hole.handleAttraction(ball, distance);

        // Ball's velocity should have changed
        assertNotEquals(initialVelocityX, ball.getVelocity().x);
        assertNotEquals(initialVelocityY, ball.getVelocity().y);
    }

    /**
     * Test when ball's velocity is null
     */
    @Test
    public void testHandleAttraction_NullBallVelocity() {
        // Initialize a hole and a ball
        ball = new Ball(110, 110, 1, null);
        hole = new Hole(100, 100, 2);

        ball.setVelocity(null);
        float distance = ball.distanceTo(hole);

        // Expecting an exception or handling of null velocity
        try {
            hole.handleAttraction(ball, distance);
            fail("Expected an exception due to null velocity");
        } catch (NullPointerException e) {
            // Test passes if NullPointerException is thrown
        }
    }

    /**
     * Test getColor works properly
     */
    @Test
    public void getColorTest() {
        hole = new Hole(100, 100, 1);
        assertEquals(hole.getColor(), 1);
    }
}