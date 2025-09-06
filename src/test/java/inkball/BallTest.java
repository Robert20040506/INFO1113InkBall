package inkball;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Test class for the Ball class, covering movement, collision detection, and interactions.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BallTest {

    private Ball ball;
    public App app;

    @BeforeAll
    public void setUpOnce() throws InterruptedException {
        app = new App();
        App.setInstance(app);
        PApplet.runSketch(new String[] { "App" }, app);
        Thread.sleep(1000);
        app.noLoop();
    }

    @BeforeEach
    public void setUp() {
        // Initialize a ball at position (100, 100) with color 1 (e.g., orange)
        ball = new Ball(100, 100, 1, null);
    }

    /**
     * Tests that the ball updates its position correctly under normal conditions.
     */
    @Test
    public void testUpdatePosition_NormalMovement() {
        float initialX = ball.getCenterXPosition();
        float initialY = ball.getCenterYPosition();
        float velocityX = ball.getVelocity().x;
        float velocityY = ball.getVelocity().y;

        ball.updatePosition();

        assertEquals(initialX + velocityX, ball.getCenterXPosition(), 0.001);
        assertEquals(initialY + velocityY, ball.getCenterYPosition(), 0.001);
    }

    /**
     * Tests that the ball bounces off the left edge of the screen.
     */
    @Test
    public void testUpdatePosition_BounceOffLeftEdge() {
        ball.setCenterXPosition(Ball.RADIUS - 1);
        ball.getVelocity().x = -2;

        ball.updatePosition();

        assertEquals(Ball.RADIUS, ball.getCenterXPosition(), 0.001);
        assertEquals(2, ball.getVelocity().x, 0.001); // Velocity should reverse
    }

    /**
     * Tests that the ball bounces off the right edge of the screen.
     */
    @Test
    public void testUpdatePosition_BounceOffRightEdge() {
        ball.setCenterXPosition(App.WIDTH - Ball.RADIUS + 1);
        ball.getVelocity().x = 2;

        ball.updatePosition();

        assertEquals(App.WIDTH - Ball.RADIUS, ball.getCenterXPosition(), 0.001);
        assertEquals(-2, ball.getVelocity().x, 0.001); // Velocity should reverse
    }

    /**
     * Tests that the ball bounces off the top edge of the screen.
     */
    @Test
    public void testUpdatePosition_BounceOffTopEdge() {
        ball.setCenterYPosition(Ball.RADIUS - 1);
        ball.getVelocity().y = -2;

        ball.updatePosition();

        assertEquals(Ball.RADIUS, ball.getCenterYPosition(), 0.001);
        assertEquals(2, ball.getVelocity().y, 0.001); // Velocity should reverse
    }

    /**
     * Tests that the ball bounces off the bottom edge of the screen.
     */
    @Test
    public void testUpdatePosition_BounceOffBottomEdge() {
        ball.setCenterYPosition(App.HEIGHT - Ball.RADIUS + 1);
        ball.getVelocity().y = 2;

        ball.updatePosition();

        assertEquals(App.HEIGHT - Ball.RADIUS, ball.getCenterYPosition(), 0.001);
        assertEquals(-2, ball.getVelocity().y, 0.001); // Velocity should reverse
    }

    /**
     * Tests collision detection with a line when a collision occurs.
     */
    @Test
    public void testHandleCollisionWithLine_Collision() {
        PVector lineStart = new PVector(50, 50);
        PVector lineEnd = new PVector(150, 50);

        ball.setCenterXPosition(100);
        ball.setCenterYPosition(36); // Close to the line
        ball.setVelocity(new PVector(0, 2)); // Moving downwards

        boolean collided = ball.handleCollisionWithLine(lineStart, lineEnd);

        assertTrue(collided);
        assertEquals(-2, ball.getVelocity().y, 0.001); // Velocity should reverse
    }

    /**
     * Test that the ball does not handle collision when not colliding with the line
     */
    @Test
    public void testHandleCollisionWithLine_NoCollision1() {
        PVector lineStart = new PVector(50, 50);
        PVector lineEnd = new PVector(150, 50);

        ball.centerXPosition = 100;
        ball.centerYPosition = 200; // Far from the line
        ball.setVelocity(new PVector(0, -2)); // Moving upwards

        boolean collided = ball.handleCollisionWithLine(lineStart, lineEnd);

        assertFalse(collided);
        assertEquals(-2, ball.getVelocity().y, 0.001); // Velocity should remain the same
    }

    /**
     * Test that the ball does not handle collision when right on top of line
     */
    @Test
    public void testHandleCollisionWithLine_NoCollision2() {
        PVector lineStart = new PVector(50, 50);
        PVector lineEnd = new PVector(150, 50);

        // Ball right in the midpoint of the line
        ball.centerXPosition = 100;
        ball.centerYPosition = 52;
        ball.setVelocity(new PVector(0, -2)); // Moving upwards

        boolean collided = ball.handleCollisionWithLine(lineStart, lineEnd);

        assertFalse(collided);
        assertEquals(-2, ball.getVelocity().y, 0.001); // Velocity should remain the same
    }


    /**
     * Test that no surrounding tiles are found when the ball is positioned
     * outside the bounds of the game board. The expected behavior is that no
     * surrounding tiles should be added to the surroundingTiles set.
     */
    @Test
    public void testCheckSurrounding_NoSurroundingTiles1() {
        // Set ball position to be outside the bounds of the board
        ball.centerXPosition = App.CELL_SIZE * App.BOARD_SIZE + Ball.RADIUS;
        ball.centerYPosition = App.CELL_SIZE * 4;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);

        // Assert that surroundingTiles set remains empty since the ball is out of bounds
        assertTrue(ball.getSurroundingTiles().isEmpty(), "No surrounding tiles should be added when the ball is out of bounds.");

        // Set ball position to be outside the bounds of the board
        ball.centerXPosition = App.CELL_SIZE * -1;
        ball.centerYPosition = App.CELL_SIZE * 4;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);

        // Assert that surroundingTiles set remains empty since the ball is out of bounds
        assertTrue(ball.getSurroundingTiles().isEmpty(), "No surrounding tiles should be added when the ball is out of bounds.");

        // Set ball position to be outside the bounds of the board
        ball.centerXPosition = App.CELL_SIZE * 4;
        ball.centerYPosition = App.CELL_SIZE * App.BOARD_SIZE + Ball.RADIUS;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);

        // Assert that surroundingTiles set remains empty since the ball is out of bounds
        assertTrue(ball.getSurroundingTiles().isEmpty(), "No surrounding tiles should be added when the ball is out of bounds.");

        // Set ball position to be outside the bounds of the board
        ball.centerXPosition = App.CELL_SIZE * 4;
        ball.centerYPosition = App.CELL_SIZE * -1;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);

        // Assert that surroundingTiles set remains empty since the ball is out of bounds
        assertTrue(ball.getSurroundingTiles().isEmpty(), "No surrounding tiles should be added when the ball is out of bounds.");
    }

    /**
     * Test that no surrounding tiles are found when there are no tiles surround the ball.
     * The expected behavior is that no surrounding tiles should be added to the surroundingTiles set.
     */
    @Test
    public void testCheckSurrounding_NoSurroundingTiles2() {
        // Set ball position to be inside the bounds of the board
        ball.centerXPosition = App.CELL_SIZE * 4;
        ball.centerYPosition = App.CELL_SIZE * 4;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);

        // Assert that surroundingTiles set remains empty since no tiles are around ball
        assertTrue(ball.getSurroundingTiles().isEmpty(), "No surrounding tiles should be added when the ball is out of bounds.");
    }

    /**
     * Test that when Wall tiles are positioned near the ball, the walls are detected
     */
    @Test
    public void testCheckSurrounding_WithWallTile() {
        // Create walls
        Wall wallLeft = new Wall(App.CELL_SIZE * 3 + Wall.HALF_SIZE, App.CELL_SIZE * 4 + Wall.HALF_SIZE, 0);
        Wall wallRight = new Wall(App.CELL_SIZE * 5 + Wall.HALF_SIZE, App.CELL_SIZE * 4 + Wall.HALF_SIZE, 0);
        Wall wallAbove = new Wall(App.CELL_SIZE * 4 + Wall.HALF_SIZE, App.CELL_SIZE * 3 + Wall.HALF_SIZE, 0);
        Wall wallBelow = new Wall(App.CELL_SIZE * 4 + Wall.HALF_SIZE, App.CELL_SIZE * 5 + Wall.HALF_SIZE, 0);

        // Place the Wall tiles
        app.board[4][3] = wallLeft;
        app.board[4][5] = wallRight;
        app.board[3][4] = wallAbove;
        app.board[5][4] = wallBelow;

        // Set ball colliding with wall to the left
        ball.centerXPosition = App.CELL_SIZE * 4 + Ball.RADIUS - 2;
        ball.centerYPosition = App.CELL_SIZE * 4 + Wall.HALF_SIZE;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);

        assertEquals(4, ball.getSurroundingTiles().size());
    }

    /**
     * Test that when a Hole tile is positioned near the ball, the Hole's
     * handleAttraction method is invoked. It also verifies that the ball's image
     * is resized correctly after attraction. This ensures the correct behavior
     * when the ball interacts with a Hole tile.
     */
    @Test
    public void testCheckSurrounding_WithHoleTile1() {
        // Create a Hole object
        Hole hole = new Hole(App.CELL_SIZE * 5 + Hole.HALF_SIZE, App.CELL_SIZE * 5 + Hole.HALF_SIZE, 0);
        Hole spyHole = Mockito.spy(hole);

        // Place the Hole tile at position (5, 5)
        app.board[5][5] = spyHole;

        // Set ball close to the Hole tile
        ball.centerXPosition = App.CELL_SIZE * 5+10;
        ball.centerYPosition = App.CELL_SIZE * 5+10;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);

        // Verify that Hole's handleAttraction method was called
        Mockito.verify(spyHole).handleAttraction(eq(ball), anyFloat());

        // Additional checks if the image is resized based on attraction
        assertNotNull(ball.getImage(), "Ball image should not be null after attraction.");
        assertTrue(ball.getImage().width > 0, "Ball image should be resized correctly after attraction.");
    }

    /**
     * Test that when a Hole tile is positioned near the ball but not close enough, the Hole's
     * handleAttraction method is not invoked.
     */
    @Test
    public void testCheckSurrounding_WithHoleTile2() {
        // Create a Hole object
        Hole hole = new Hole(App.CELL_SIZE * 5 + Hole.HALF_SIZE, App.CELL_SIZE * 5 + Hole.HALF_SIZE, 0);
        Hole spyHole = Mockito.spy(hole);

        // Place the Hole tile at position (5, 5)
        app.board[5][5] = spyHole;

        // Set ball close to the Hole tile but not close enough to be attracted
        ball.centerXPosition = App.CELL_SIZE * 5;
        ball.centerYPosition = App.CELL_SIZE * 5;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);

        // Verify that Hole's handleAttraction method was not called
        verify(spyHole, times(0)).handleAttraction(eq(ball), anyFloat());
    }

    /**
     * Test that when a non-wall, non-hole tile is positioned near the ball, nothing happens
     */
    @Test
    public void testCheckSurrounding_WithTile() {
        // Create a tile object
        Tile tile = new Tile(App.CELL_SIZE * 2 + Tile.HALF_SIZE, App.CELL_SIZE * 2 + Tile.HALF_SIZE);

        // Place the tile at position (2, 2)
        app.board[2][2] = tile;

        // Set ball next to tile
        ball.centerXPosition = App.CELL_SIZE * 3 + Tile.HALF_SIZE;
        ball.centerYPosition = App.CELL_SIZE * 2 + Tile.HALF_SIZE;

        // Call the checkSurrounding method
        ball.checkSurrounding(app.board);
    }

    /**
     * Tests setting and getting the color of the ball.
     */
    @Test
    public void testSetAndGetColor() {
        ball.setColor(2, null);
        assertEquals(2, ball.getColor());
    }

    /**
     * Test normal calculation between a point and a line segment
     */
    @Test
    public void testCalculateNormal_NonZeroVector() {
        PVector point = new PVector(3, 4);
        PVector A = new PVector(0, 0);
        PVector B = new PVector(5, 0);

        PVector normal = ball.calculateNormal(point, A, B);

        assertNotNull(normal);
        assertEquals(0, normal.x, 0.001);
        assertEquals(4, normal.y, 0.001);
    }

    /**
     * Test normal calculation when the line segment is a point
     */
    @Test
    public void testCalculateNormal_ZeroLengthLine() {
        PVector point = new PVector(3, 4);
        PVector A = new PVector(0, 0);
        PVector B = new PVector(0, 0);

        PVector normal = ball.calculateNormal(point, A, B);

        assertNotNull(normal);
        assertEquals(3, normal.x, 0.001);
        assertEquals(4, normal.y, 0.001);
    }

    /**
     * Tests checking surrounding tiles when there are no tiles around the ball.
     */
    @Test
    public void testCheckSurrounding_NoTiles() {
        ball.setCenterXPosition(App.CELL_SIZE * 4);
        ball.setCenterYPosition(App.CELL_SIZE * 4);

        Tile[][] board = new Tile[App.BOARD_SIZE][App.BOARD_SIZE];
        ball.checkSurrounding(board);

        assertTrue(ball.getSurroundingTiles().isEmpty());
    }

    /**
     * Tests checking surrounding tiles when walls are present.
     */
    @Test
    public void testCheckSurrounding_WithWalls() {
        Tile[][] board = new Tile[App.BOARD_SIZE][App.BOARD_SIZE];
        Wall wallLeft = new Wall(App.CELL_SIZE * 3 + Tile.HALF_SIZE, App.CELL_SIZE * 4 + Tile.HALF_SIZE, 0);
        Wall wallRight = new Wall(App.CELL_SIZE * 5 + Tile.HALF_SIZE, App.CELL_SIZE * 4 + Tile.HALF_SIZE, 0);
        Wall wallAbove = new Wall(App.CELL_SIZE * 4 + Tile.HALF_SIZE, App.CELL_SIZE * 3 + Tile.HALF_SIZE, 0);
        Wall wallBelow = new Wall(App.CELL_SIZE * 4 + Tile.HALF_SIZE, App.CELL_SIZE * 5 + Tile.HALF_SIZE, 0);

        board[4][3] = wallLeft;
        board[4][5] = wallRight;
        board[3][4] = wallAbove;
        board[5][4] = wallBelow;

        ball.setCenterXPosition(App.CELL_SIZE * 4);
        ball.setCenterYPosition(App.CELL_SIZE * 4);

        ball.checkSurrounding(board);

        assertEquals(4, ball.getSurroundingTiles().size());
    }
}