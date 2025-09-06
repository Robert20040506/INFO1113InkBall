package inkball;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import inkball.App.GameState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Test class for the App class, covering various game functionalities.
 */
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppTest {

    public App app;

    @BeforeAll
    public void setUpOnce() throws InterruptedException {
        app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        // Allow time for the sketch to initialize
        Thread.sleep(1000);
        app.noLoop();
    }

    @BeforeEach
    public void setUp() {
        
        // Initialize necessary static variables before each test
        // App.scoreIncreaseMap = new HashMap<>();
        // App.scoreIncreaseMap.put("orange", 50);
        // App.scoreIncreaseMap.put("grey", 70);
        // App.scoreIncreaseMap.put("blue", 80);
        // App.scoreIncreaseMap.put("green", 90);
        // App.scoreIncreaseMap.put("red", 100);

        // App.scoreDecreaseMap = new HashMap<>();
        // App.scoreDecreaseMap.put("orange", 25);
        // App.scoreDecreaseMap.put("grey", 0);
        // App.scoreDecreaseMap.put("blue", 30);
        // App.scoreDecreaseMap.put("green", 35);
        // App.scoreDecreaseMap.put("red", 40);

        // app.scoreIncreaseModifier = 1.0f;
        // app.scoreDecreaseModifier = 1.0f;

        app.currentLevel = 1;
        app.currentScore = 0;
        app.previousScore = 0;

        app.lines = new ArrayList<>();
        app.currentLine = new ArrayList<>();
        app.ballsOnScreen = new ArrayList<>();
        app.ballsToBeRemoved = new ArrayList<>();
        app.ballsInQueue = new ArrayDeque<>();
        app.spawners = new ArrayList<>();
        app.levelLoaded = true;
        app.gameState = App.GameState.RUNNING;
    }

    /**
     * Tests loading a level correctly.
     */
    @Test
    public void testLoadLevel() {
        app.currentLevel = 1;
        app.levelLoaded = false;
        app.loadLevel(app.currentLevel);

        assertNotNull(app.layout);
        assertEquals(120, app.levelTime);
        assertEquals(10, app.spawnInterval);
        assertEquals(6, app.ballsInQueue.size()); // Based on config.json
    }

    /**
     * Tests restarting the level.
     */
    @Test
    public void testLevelRestart() {
        app.gameState = App.GameState.GAME_ENDED;
        app.currentScore = 50;
        app.previousScore = 30;
        app.restartLevel();

        assertEquals(App.GameState.RUNNING, app.gameState);
        assertEquals(app.currentScore, app.previousScore);
        assertFalse(app.levelLoaded);
        assertEquals(app.currentLevel, 1);

        app.gameState = App.GameState.RUNNING;
        app.currentScore = 50;
        app.previousScore = 30;
        app.restartLevel();

        assertEquals(App.GameState.RUNNING, app.gameState);
        assertEquals(app.currentScore, app.previousScore);
        assertFalse(app.levelLoaded);
    }

    /**
     * Tests removing a line when a line exists near the mouse.
     */
    @Test
    public void testRemoveLineAtMouse_LineExists() {
        // Set up a line
        app.lines.add(Arrays.asList(new PVector(50, 50), new PVector(100, 100)));
        app.mouseX = 75;
        app.mouseY = 75 + App.TOP_BAR_HEIGHT; // Adjust for top bar

        app.removeLineAtMouse();

        assertEquals(0, app.lines.size()); // Line should be removed
    }

    /**
     * Tests that a line remains when the mouse is not near any line.
     */
    @Test
    public void testRemoveLineAtMouse_LineDoesNotExist() {
        // Set up a line
        app.lines.add(Arrays.asList(new PVector(50, 50), new PVector(100, 100)));
        app.mouseX = 200;
        app.mouseY = 200 + App.TOP_BAR_HEIGHT; // Adjust for top bar

        app.removeLineAtMouse();

        assertEquals(1, app.lines.size()); // Line should remain
    }

    /**
     * Test removing a line when there are no lines drawn
     */
    @Test
    public void testRemoveLineAtMouse_NoLines() {
        app.lines = new ArrayList<>();
        app.removeLineAtMouse();
        assertEquals(0, app.lines.size());
    }

    /**
     * Tests drawing and releasing a line.
     */
    @Test
    public void testMouseDraggedAndReleased() {
        app.mouseButton = PConstants.LEFT;
        app.gameState = App.GameState.RUNNING;
        app.drawing = false;
        app.currentLine = new ArrayList<>();
        app.lines = new ArrayList<>();

        // Simulate mouse drag
        app.mouseX = 50;
        app.mouseY = 50 + App.TOP_BAR_HEIGHT;
        app.mouseDragged(null);
        assertTrue(app.drawing);
        assertEquals(1, app.currentLine.size());

        // Simulate mouse drag to new position
        app.mouseX = 100;
        app.mouseY = 100 + App.TOP_BAR_HEIGHT;
        app.mouseDragged(null);
        assertEquals(2, app.currentLine.size());

        // Simulate mouse release
        app.mouseReleased(null);
        assertFalse(app.drawing);
        assertEquals(1, app.lines.size()); // The line should be added to lines
    }

    // /**
    //  * Tests that drawing does not start when game is not running.
    //  */
    // @Test
    // public void testMouseDragged_GameNotRunning() {
    //     app.mouseButton = PConstants.LEFT;
    //     app.gameState = App.GameState.PAUSED;
    //     app.drawing = false;
    //     app.currentLine = new ArrayList<>();

    //     app.mouseX = 50;
    //     app.mouseY = 50 + App.TOP_BAR_HEIGHT;
    //     app.mouseDragged(null);

    //     assertFalse(app.drawing);
    //     assertEquals(0, app.currentLine.size());
    // }

    @Test
    public void testLoadLayout_AllCases() throws IOException {
        // Create a temporary layout file that includes all possible tiles
        Path tempLayoutFile = Files.createTempFile("testLayout", ".txt");

        String layoutContent =
            "X1 2 3 4V1Z1H1B1S \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  ";

        Files.write(tempLayoutFile, layoutContent.getBytes());

        app.loadLayout(tempLayoutFile.toString());

        // Now, check that the board has the correct tiles at the correct positions

        // For 'X' at [0][0]
        assertTrue(app.board[0][0] instanceof Wall);
        assertEquals(0, ((Wall)app.board[0][0]).getColor());

        // For '1' at [0][1]
        assertTrue(app.board[0][1] instanceof Wall);
        assertEquals(1, ((Wall)app.board[0][1]).getColor());

        // For '2' at [0][3]
        assertTrue(app.board[0][3] instanceof Wall);
        assertEquals(2, ((Wall)app.board[0][3]).getColor());

        // For '3' at [0][5]
        assertTrue(app.board[0][5] instanceof Wall);
        assertEquals(3, ((Wall)app.board[0][5]).getColor());

        // For '4' at [0][7]
        assertTrue(app.board[0][7] instanceof Wall);
        assertEquals(4, ((Wall)app.board[0][7]).getColor());

        // For 'V1' at [0][8]
        assertTrue(app.board[0][8] instanceof ColorRestrictingWall);
        assertEquals(1, ((ColorRestrictingWall)app.board[0][8]).getColor());
        assertTrue(((ColorRestrictingWall)app.board[0][8]).isVertical());

        // For 'Z1' at [0][10]
        assertTrue(app.board[0][10] instanceof ColorRestrictingWall);
        assertEquals(1, ((ColorRestrictingWall)app.board[0][10]).getColor());
        assertFalse(((ColorRestrictingWall)app.board[0][10]).isVertical());

        // For 'H1' at [0][12]
        assertTrue(app.board[0][12] instanceof Hole);
        assertEquals(1, ((Hole)app.board[0][12]).getColor());

        // For 'B1' at [0][14], balls_on_screen should contain a ball
        assertEquals(1, app.ballsOnScreen.size());
        Ball ball = app.ballsOnScreen.get(0);
        assertEquals(1, ball.getColor());

        // For 'S' at [0][16], spawners should contain a spawner
        assertEquals(1, app.spawners.size());
        Spawner spawner = app.spawners.get(0);
        assertNotNull(spawner);

        // Clean up temporary file
        Files.delete(tempLayoutFile);
    }

    @Test
    public void testLoadLayout_InvalidV() throws IOException {
        // Create a temporary layout file that includes 'V' followed by an invalid character
        Path tempLayoutFile = Files.createTempFile("testLayoutInvalidV", ".txt");

        String layoutContent =
            "VX                \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  \n" +
            "                  ";

        Files.write(tempLayoutFile, layoutContent.getBytes());

        // Expecting an exception or handling of invalid character
        try {
            app.loadLayout(tempLayoutFile.toString());
            // Since 'X' - '0' will result in a negative number, check for invalid color
            int color = ((ColorRestrictingWall)app.board[0][0]).getColor();
            assertTrue(color < 0);
        } catch (Exception e) {
            // Test passes if exception is thrown
        }

        // Clean up temporary file
        Files.delete(tempLayoutFile);
    }

    @Test
    public void testDrawLevelCompletionLayer() {
        // Set initial conditions
        app.tile1Row = 1;
        app.tile1Column = 0;
        app.xDirection = 0;
        app.yDirection = -1;
        app.levelTime = 100;
        app.currentScore = 0;
        app.draw = true;

        // Simulate calling drawLevelCompletionLayer multiple times to cover different branches
        for (int i = 0; i < 100; i++) {
            boolean result = app.drawLevelCompletionLayer();

            // Check that levelTime decreases
            assertEquals(100 - (i + 1), app.levelTime);

            // Check that currentScore increases
            assertEquals(i + 1, app.currentScore);

            // Check tile1Row and tile1Column change appropriately
            // Add assertions when the conditions are met

            if (app.tile1Row == 0 && app.tile1Column == 0) {
                assertEquals(0, app.yDirection);
                assertEquals(1, app.xDirection);
            }
            if (app.tile1Row == 0 && app.tile1Column == 17) {
                assertEquals(1, app.yDirection);
                assertEquals(0, app.xDirection);
            }
            if (app.tile1Row == 17 && app.tile1Column == 17) {
                assertEquals(0, app.yDirection);
                assertEquals(-1, app.xDirection);
            }
            if (app.tile1Row == 17 && app.tile1Column == 0) {
                assertEquals(-1, app.yDirection);
                assertEquals(0, app.xDirection);
            }
        }

        // At the end, levelTime should be 0
        assertEquals(0, app.levelTime);
    }

    /**
     * Tests toggling the pause state.
     */
    @Test
    public void testTogglePause() {
        app.gameState = App.GameState.RUNNING;
        app.togglePause();
        assertEquals(App.GameState.PAUSED, app.gameState);

        app.togglePause();
        assertEquals(App.GameState.RUNNING, app.gameState);
    }

    /**
     * Tests updating game time and spawn time.
     */
    @Test
    public void testUpdateTime() {
        app.levelFrames = App.FPS * 10; // 10 seconds
        app.spawnFrames = App.FPS * 5;  // 5 seconds

        app.updateTime();

        assertEquals(App.FPS * 10 - 1, app.levelFrames);
        assertEquals(App.FPS * 5 - 1, app.spawnFrames);
    }

    /**
     * Tests advancing to the next level.
     */
    @Test
    public void testAdvanceLevel_NotLastLevel() {
        app.currentLevel = 1;
        app.currentScore = 50;
        app.advanceLevel();

        assertEquals(2, app.currentLevel);
        assertEquals(App.GameState.RUNNING, app.gameState);
        assertEquals(50, app.previousScore);
        assertFalse(app.levelLoaded);
        assertTrue(app.lines.isEmpty());
    }

    /**
     * Tests advancing when on the last level.
     */
    @Test
    public void testAdvanceLevel_LastLevel() {
        app.currentLevel = app.levels.size();
        app.advanceLevel();

        assertEquals(App.GameState.GAME_ENDED, app.gameState);
    }

    /**
     * Tests loading a layout file that contains longer lines than the board size.
     */
    @Test
    public void testLoadLayout_LongLines() throws IOException {
        // Create a temporary layout file with long lines
        Path tempLayoutFile = Files.createTempFile("testLayoutLongLines", ".txt");

        StringBuilder layoutContent = new StringBuilder();
        for (int i = 0; i < App.BOARD_SIZE; i++) {
            layoutContent.append(
                String.join("", Collections.nCopies(App.BOARD_SIZE + 10, "X"))
            ).append("\n"); // Lines longer than BOARD_SIZE
        }

        Files.write(tempLayoutFile, layoutContent.toString().getBytes());

        app.loadLayout(tempLayoutFile.toString());

        // Ensure no exceptions occur and board is populated correctly
        assertNotNull(app.board[0][0]);

        // Clean up temporary file
        Files.delete(tempLayoutFile);
    }

    /**
     * Tests loading a layout file that contains unknown characters.
     */
    @Test
    public void testLoadLayout_UnknownCharacters() throws IOException {
        // Create a temporary layout file with an unknown character
        Path tempLayoutFile = Files.createTempFile("testLayoutUnknownChar", ".txt");

        String layoutContent = 
            "?                   \n" +
            String.join("", Collections.nCopies(App.BOARD_SIZE - 1, "                    \n"));

        Files.write(tempLayoutFile, layoutContent.getBytes());

        app.loadLayout(tempLayoutFile.toString());

        // Since '?' is not handled, nothing should be added to the board
        assertNull(app.board[0][0]);

        // Clean up temporary file
        Files.delete(tempLayoutFile);
    }

    /**
     * Tests handling file not found exception during layout loading.
     */
    @Test
    public void testLoadLayout_FileNotFound() {
        try {
            app.loadLayout("nonexistent_file.txt");
        } catch (Exception e) {
            fail("Exception should be caught inside loadLayout");
        }
        // Ensure the method handles the exception
    }

    /**
     * Tests that the main method runs without exceptions.
     */
    @Test
    public void testMainMethod() {
        try {
            App.main(new String[]{});
        } catch (Exception e) {
            fail("Main method should run without exceptions");
        }
    }

    /**
     * Tests that the draw method runs without exceptions.
     */
    @Test
    public void testDrawMethod() {
        try {
            app.gameState = GameState.LEVEL_COMPLETION_ANIMATION;
            app.draw();
        } catch (Exception e) {
            fail("Draw method should run without exceptions");
        }
    }
        
    @Test
    public void testDrawTopBarLayer_GameEnded() {
        app.gameState = GameState.GAME_ENDED;

        app.drawTopBarLayer();

        // Ensure no exceptions occur
    }

    @Test
    public void testDrawTopBarLayer_LevelTimesUp() {
        app.gameState = GameState.LEVEL_TIME_UP;

        app.drawTopBarLayer();

        // Ensure no exceptions occur
    }

    @Test
    public void testDrawTopBarLayer_PAUSED() {
        app.gameState = GameState.PAUSED;

        app.drawTopBarLayer();

        // Ensure no exceptions occur
    }

    /**
     * Tests the tick method when the game state is RUNNING and balls are present.
     */
    @Test
    public void testTick_GameRunningWithBalls() {
        app.gameState = App.GameState.RUNNING;
        app.levelFrames = App.FPS * 10;
        app.ballsOnScreen.add(new Ball(100, 100, 1, app.ballImages.get(1)));

        app.tick();
        

        // Ensure game state remains RUNNING
        assertEquals(App.GameState.RUNNING, app.gameState);
        assertEquals(App.FPS * 10 - 1, app.levelFrames);
    }

    /**
     * Tests the tick method when the game state is RUNNING and no balls are present.
     */
    @Test
    public void testTick_GameRunningNoBalls() {
        app.gameState = App.GameState.RUNNING;
        app.ballsOnScreen.clear();
        app.ballsInQueue.clear();
        
        app.tick();

        // Game should transition to LEVEL_COMPLETION_ANIMATION
        assertEquals(App.GameState.LEVEL_COMPLETION_ANIMATION, app.gameState);
    }

    /**
     * Tests the tick method during level completion animation.
     */
    @Test
    public void testTick_LevelCompletionAnimation() {
        app.gameState = App.GameState.LEVEL_COMPLETION_ANIMATION;
        app.levelTime = 5;

        app.tick();

        // Level time should decrease
        assertEquals(4, app.levelTime);
    }

    /**
     * Tests the tick method when level completion animation ends.
     */
    @Test
    public void testTick_LevelCompletionAnimationEnds() {
        app.gameState = App.GameState.LEVEL_COMPLETION_ANIMATION;
        app.levelTime = 0;

        app.tick();

        // Game should transition to LEVEL_COMPLETE
        assertEquals(App.GameState.LEVEL_COMPLETE, app.gameState);
    }

    /**
     * Tests the tick method when level is complete.
     */
    @Test
    public void testTick_LevelComplete() {
        app.gameState = App.GameState.LEVEL_COMPLETE;
        app.currentLevel = 1;
        app.currentScore = 100;

        app.tick();

        // Should advance to next level
        assertEquals(2, app.currentLevel);
        assertEquals(App.GameState.RUNNING, app.gameState);
        assertEquals(100, app.previousScore);
    }

    /**
     * Tests the tick method when game has ended.
     */
    @Test
    public void testTick_GameEnded() {
        app.gameState = App.GameState.GAME_ENDED;
        app.currentScore = 100;
        app.previousScore = 100;

        app.levelLoaded = true;
        app.tick();

        // Should reset the game
        assertTrue(app.lines.isEmpty());
    }

    @Test
    public void testKeyPressed() {
        // Simulate pressing the space key to pause the game
        app.key = ' ';
        app.gameState = GameState.RUNNING;

        app.keyPressed(null);

        assertTrue(app.gameState == GameState.PAUSED);

        // Simulate pressing the space key to pause the game while level completion animation is playing
        app.key = ' ';
        app.gameState = GameState.LEVEL_COMPLETION_ANIMATION;

        app.keyPressed(null);

        // level completion animation shouldn't be paused
        assertTrue(app.gameState == GameState.LEVEL_COMPLETION_ANIMATION);

        // Simulate pressing 'r' to restart level
        app.key = 'r';
        app.gameState = GameState.GAME_ENDED;
        app.currentScore = 50;
        app.previousScore = 30;

        app.keyPressed(null);

        assertTrue(app.gameState == GameState.RUNNING);
        assertEquals(app.currentScore, app.previousScore);

        // Simulate pressing 'r' to restart level when level completion animation is running
        app.key = 'r';
        app.gameState = GameState.LEVEL_COMPLETION_ANIMATION;

        app.keyPressed(null);

        // gamestate should remain as LEVEL_COMPLETION_ANIMATION
        assertTrue(app.gameState == GameState.LEVEL_COMPLETION_ANIMATION);

        // Simulate pressing the CTRL key
        app.keyCode = PConstants.CONTROL;
        app.gameState = GameState.RUNNING;
        app.ctrlPressed = false;

        app.keyPressed(null);

        assertTrue(app.ctrlPressed);
    }

    @Test
    public void testMousePressed() {
        // Simulate right mouse button click
        app.mouseButton = PConstants.RIGHT;
        app.ctrlPressed = false;
        app.lines = new ArrayList<>();
        app.lines.add(Arrays.asList(new PVector(50, 50), new PVector(100, 100)));
        app.mouseX = 75;
        app.mouseY = 75 + App.TOP_BAR_HEIGHT; // Adjust for top bar

        app.mousePressed(null);

        assertEquals(0, app.lines.size()); // Line should be removed

        // Simulate left mouse button click with ctrl pressed
        app.mouseButton = PConstants.LEFT;
        app.ctrlPressed = true;
        app.lines = new ArrayList<>();
        app.lines.add(Arrays.asList(new PVector(150, 150), new PVector(200, 200)));
        app.mouseX = 175;
        app.mouseY = 175 + App.TOP_BAR_HEIGHT; // Adjust for top bar

        app.mousePressed(null);

        assertEquals(0, app.lines.size()); // Line should be removed
    }

    @Test
    public void testKeyReleased() {
        // Simulate releasing the CTRL key
        app.keyCode = PConstants.CONTROL;
        app.ctrlPressed = true;

        app.keyReleased();

        assertFalse(app.ctrlPressed);

        // Simulate releasing another key
        app.keyCode = PConstants.SHIFT;
        app.ctrlPressed = false;

        app.keyReleased();

        assertFalse(app.ctrlPressed);
    }

    @Test
    public void testDrawLineLayer_NoLines() {
        app.lines = new ArrayList<>();
        app.drawing = false;
        app.currentLine = new ArrayList<>();

        app.drawLineLayer();

        // Since there are no lines, just ensure no exceptions occur
        assertTrue(app.lines.isEmpty());
    }

    @Test
    public void testDrawLineLayer_WithLines_NoCollision() {
        app.gameState = GameState.RUNNING;
        app.lines = new ArrayList<>();
        List<PVector> line1 = Arrays.asList(new PVector(50, 50), new PVector(100, 100));
        app.lines.add(line1);

        // No balls on screen
        app.ballsOnScreen = new ArrayList<>();

        // Set gamePaused to true to skip collision detection
        app.gameState = GameState.PAUSED;

        app.drawLineLayer();

        // Lines should remain since there's no collision
        assertEquals(1, app.lines.size());
    }

    @Test
    public void testDrawLineLayer_WithLines_WithCollision() {
        // Set up lines
        app.lines = new ArrayList<>();
        List<PVector> line1 = Arrays.asList(new PVector(50, 50), new PVector(100, 100));
        List<PVector> line2 = Arrays.asList(new PVector(50, 50), new PVector(50, 50)); // add 0 length line to test robustness
        app.lines.add(line1);
        app.lines.add(line2);

        // Set up a ball that will collide with the line
        Ball ball = new Ball(75, 75, 1, app.ballImages.get(1));
        app.ballsOnScreen = new ArrayList<>();
        app.ballsOnScreen.add(ball);

        // Set gameState to RUNNING to enable collision detection
        app.gameState = GameState.RUNNING;

        // // Mock the ball's handleCollisionWithLine method to return true
        // Ball testBall = new Ball(75, 75, 1, null) {
        //     @Override
        //     public boolean handleCollisionWithLine(PVector start, PVector end) {
        //         return true; // Simulate collision
        //     }
        // };
        // app.ballsOnScreen.add(0, testBall);

        app.drawLineLayer();

        // The line should be removed after collision. 0 length line shouldn't be removed
        assertEquals(1, app.lines.size());
    }

    @Test
    public void testDrawLineLayer_DrawingInProgress() {
        app.drawing = true;
        app.currentLine = new ArrayList<>();
        app.currentLine.add(new PVector(50, 50));
        app.currentLine.add(new PVector(100, 100));

        app.drawLineLayer();

        // No assertions needed; ensure no exceptions occur
    }

    @Test
    public void testDrawLineLayer_NotDrawing() {
        app.drawing = false;
        app.currentLine = new ArrayList<>();
        app.currentLine.add(new PVector(50, 50));
        app.currentLine.add(new PVector(100, 100));

        app.drawLineLayer();

        // No assertions needed; ensure no exceptions occur
    }

    @Test
    public void testDrawConveyorBeltLayer_BallsInQueue_PeekNotNull() {
        app.ballsInQueue = new ArrayDeque<>();
        app.ballsInQueue.add("blue");
        app.spawnFrames = 0;
        app.spawnInterval = 5;
        app.ballsOnScreen = new ArrayList<>();
        app.spawners = new ArrayList<>();
        app.spawners.add(new Spawner(100, 100));

        app.drawConveyorBeltLayer();

        assertEquals(App.FPS * app.spawnInterval, app.spawnFrames);
        assertEquals(1, app.ballsOnScreen.size());
        assertEquals(0, app.ballsInQueue.size());
    }

    @Test
    public void testDrawConveyorBeltLayer_BallsInQueue_PeekNull() {
        app.ballsInQueue = new ArrayDeque<>();
        app.spawnFrames = 0;
        app.spawnInterval = 5;
        app.ballsOnScreen = new ArrayList<>();
        app.spawners = new ArrayList<>();
        app.spawners.add(new Spawner(100, 100));

        app.drawConveyorBeltLayer();

        // No ball should be spawned
        assertEquals(App.FPS * app.spawnInterval, app.spawnFrames);
        assertEquals(0, app.ballsOnScreen.size());
    }

    @Test
    public void testTick_PlayLevelCompleteAnimationTriggered() {
        app.gameState = GameState.RUNNING;
        app.ballsOnScreen = new ArrayList<>();
        app.ballsInQueue = new ArrayDeque<>();

        app.tick();

        assertEquals(app.gameState, GameState.LEVEL_COMPLETION_ANIMATION);
    }

    @Test
    public void testTick_PlayLevelCompleteAnimationNotTriggered() {
        app.gameState = GameState.RUNNING;
        app.ballsOnScreen = new ArrayList<>();
        app.ballsInQueue = new ArrayDeque<>();
        app.ballsInQueue.add("blue"); // Balls still in queue

        app.tick();

        assertNotEquals(app.gameState, GameState.LEVEL_COMPLETION_ANIMATION);
    }

    @Test
    public void testTick_PlayLevelCompleteAnimationActive() {
        app.gameState = GameState.LEVEL_COMPLETION_ANIMATION;
        app.levelTime = 10;

        app.tick();

        assertEquals(9, app.levelTime);
    }

    @Test
    public void testTick_PlayLevelCompleteAnimationInactive() {
        app.gameState = GameState.RUNNING;
        app.ballsOnScreen.add(new Ball(75, 75, 1, app.ballImages.get(1)));
        app.levelFrames = 90;
        app.spawnFrames = 30;

        app.tick();

        // Level frames and spawn frames should decrease
        assertEquals(89, app.levelFrames);
        assertEquals(29, app.spawnFrames);
    }

}