package inkball;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The main class for the InkBall game application.
 */
public class App extends PApplet {
    public static App instance;

    // Constants
    public static final int CELL_SIZE = 32;
    public static final int TOP_BAR_HEIGHT = 64;
    public static final int BOARD_SIZE = 18;
    public static final int FPS = 30;
    public static final int WIDTH = CELL_SIZE * BOARD_SIZE;
    public static final int HEIGHT = WIDTH + TOP_BAR_HEIGHT;

    // Game configuration
    public String configPath;

    // Game state variables
    public GameState gameState;
    public boolean ctrlPressed;
    public boolean drawing;
    public boolean levelLoaded;
    public boolean draw;

    // Game data
    public int currentLevel;
    public int currentScore;
    public int previousScore;
    public int levelTime;
    public int levelFrames;
    public float spawnTime;
    public int spawnFrames;
    public int spawnInterval;
    public float scoreIncreaseModifier;
    public float scoreDecreaseModifier;

    // Game objects
    public String layout;
    public List<Level> levels;
    public Queue<String> ballsInQueue;
    public List<Ball> ballsOnScreen;
    public List<Spawner> spawners;
    public List<Ball> ballsToBeRemoved;
    public List<List<PVector>> lines;
    public List<PVector> currentLine;

    // Images
    public List<PImage> ballImages;
    public List<PImage> wallImages;
    public List<PImage> holeImages;
    public PImage normalTileImage;
    public PImage spawnerImage;
    public List<PImage> verticalColorWallImages;
    public List<PImage> horizontalColorWallImages;

    // Graphics layers
    public PGraphics boardLayer;
    public PGraphics levelCompletionLayer;
    public PGraphics ballLayer;
    public PGraphics topBarLayer;
    public PGraphics conveyorBeltLayer;
    public PGraphics lineLayer;

    // Board
    public Tile[][] board;

    // Static maps for score calculations
    public static Map<String, Integer> scoreIncreaseMap;
    public static Map<String, Integer> scoreDecreaseMap;

    // Random number generator
    public static final Random RANDOM = new Random();

    /**
     * Enum representing the game state.
     */
    public enum GameState {
        RUNNING,
        PAUSED,
        LEVEL_COMPLETE,
        GAME_ENDED,
        LEVEL_COMPLETION_ANIMATION,
        LEVEL_TIME_UP
    }

    /**
     * Constructor for the App class.
     */
    public App() {
        instance = this;
        this.configPath = "config.json";
        this.currentLevel = 1;
        this.gameState = GameState.RUNNING;
    }

    public static App getInstance() { return instance; }
    public static void setInstance(App instance) { App.instance = instance; }

    /**
     * Initializes the settings of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Loads resources and initializes game elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        loadConfig(configPath);
        loadImages();
        initializeLayers();
        initializeGameVariables();
    }

    /**
     * Initializes graphics layers.
     */
    public void initializeLayers() {
        boardLayer = createGraphics(WIDTH, WIDTH);
        levelCompletionLayer = createGraphics(WIDTH, WIDTH);
        ballLayer = createGraphics(WIDTH, WIDTH);
        topBarLayer = createGraphics(WIDTH, TOP_BAR_HEIGHT);
        conveyorBeltLayer = createGraphics(162, 40);
        lineLayer = createGraphics(WIDTH, WIDTH);
    }

    /**
     * Initializes game variables.
     */
    public void initializeGameVariables() {
        ctrlPressed = false;
        drawing = false;
        draw = true;
        ballsOnScreen = new ArrayList<>();
        ballsToBeRemoved = new ArrayList<>();
        lines = new ArrayList<>();
        currentLine = new ArrayList<>();
    }

    /**
     * Loads images required for the game.
     */
    public void loadImages() {
        ballImages = new ArrayList<>();
        wallImages = new ArrayList<>();
        holeImages = new ArrayList<>();
        verticalColorWallImages = new ArrayList<>();
        horizontalColorWallImages = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            ballImages.add(loadImage("src/main/resources/inkball/ball" + i + ".png"));
            wallImages.add(loadImage("src/main/resources/inkball/wall" + i + ".png"));
            holeImages.add(loadImage("src/main/resources/inkball/hole" + i + ".png"));
            if (i != 0) {
                verticalColorWallImages.add(loadImage("src/main/resources/inkball/verticalColorRestrictingWall" + i + ".png"));
                horizontalColorWallImages.add(loadImage("src/main/resources/inkball/horizontalColorRestrictingWall" + i + ".png"));
            }
        }

        normalTileImage = loadImage("src/main/resources/inkball/tile.png");
        spawnerImage = loadImage("src/main/resources/inkball/entrypoint.png");
    }

    /**
     * Loads game configuration from a JSON file.
     *
     * @param configPath Path to the JSON configuration file.
     */
    public void loadConfig(String configPath) {
        JSONObject config = loadJSONObject(configPath);

        // Load levels
        levels = new ArrayList<>();
        JSONArray jsonLevels = config.getJSONArray("levels");
        for (int i = 0; i < jsonLevels.size(); i++) {
            JSONObject jsonLevel = jsonLevels.getJSONObject(i);
            String layout = jsonLevel.getString("layout");
            int time = jsonLevel.getInt("time");
            int spawnInterval = jsonLevel.getInt("spawn_interval");
            float scoreIncreaseModifier = jsonLevel.getFloat("score_increase_from_hole_capture_modifier");
            float scoreDecreaseModifier = jsonLevel.getFloat("score_decrease_from_wrong_hole_modifier");
            List<String> ballsList = jsonArrayToList(jsonLevel.getJSONArray("balls"));

            Level level = new Level(layout, time, spawnInterval, scoreIncreaseModifier, scoreDecreaseModifier, ballsList);
            levels.add(level);

            // System.out.println("ScoreIncreaseModifier: " + scoreIncreaseModifier);
            // System.out.println("ScoreDecreaseModifier: " + scoreDecreaseModifier);
        }

        // Load score mappings
        scoreIncreaseMap = jsonToMap(config.getJSONObject("score_increase_from_hole_capture"));
        scoreDecreaseMap = jsonToMap(config.getJSONObject("score_decrease_from_wrong_hole"));

        // System.out.println("ScoreIncreaseMap: " + scoreIncreaseMap);
        // System.out.println("ScoreDecreaseMap: " + scoreDecreaseMap);
    }

    /**
     * Converts a JSONArray to a List of Strings.
     *
     * @param jsonArray JSONArray to convert.
     * @return List of Strings.
     */
    public List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    /**
     * Converts a JSONObject to a Map of String to Integer.
     *
     * @param jsonObject JSONObject to convert.
     * @return Map of String to Integer.
     */
    public static Map<String, Integer> jsonToMap(JSONObject jsonObject) {
        Map<String, Integer> map = new HashMap<>();
        @SuppressWarnings("unchecked")
        Set<String> keys = jsonObject.keys();
        for (String key : keys) {
            Integer value = jsonObject.getInt(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * Handles key pressed events.
     *
     * @param event KeyEvent.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (gameState != GameState.LEVEL_COMPLETION_ANIMATION) {
            if (key == ' ') {
                togglePause();
            }
            if (key == 'r') {
                restartLevel();
            }
            if (keyCode == CONTROL) {
                ctrlPressed = true;
            }
        }
    }

    /**
     * Toggles the pause state of the game.
     */
    public void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
        }
    }

    /**
     * Restarts the current level or the game.
     */
    public void restartLevel() {
        if (gameState == GameState.GAME_ENDED) {
            currentLevel = 1;
            currentScore = 0;
            previousScore = 0;
        }
        lines.clear();
        levelLoaded = false;
        gameState = GameState.RUNNING;
        levelTime = levels.get(currentLevel - 1).getTime();
        currentScore = previousScore;
    }

    /**
     * Handles key released events.
     */
    @Override
    public void keyReleased() {
        if (keyCode == CONTROL) {
            ctrlPressed = false;
        }
    }

    /**
     * Handles mouse pressed events.
     *
     * @param event MouseEvent.
     */
    @Override
    public void mousePressed(MouseEvent event) {
        if (mouseButton == RIGHT || (ctrlPressed && mouseButton == LEFT)) {
            removeLineAtMouse();
        }
    }

    /**
     * Removes a line if the mouse position collides with the line.
     */
    public void removeLineAtMouse() {
        float tolerance = 5; // Tolerance distance for removing the line

        // Check each line to see if the mouse is near any segment
        Iterator<List<PVector>> iterator = lines.iterator();
        while (iterator.hasNext()) {
            List<PVector> line = iterator.next();
            for (int i = 1; i < line.size(); i++) {
                PVector start = line.get(i - 1);
                PVector end = line.get(i);

                if (isMouseNearLine(start, end, tolerance)) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    /**
     * Checks if the mouse position is near a line segment.
     *
     * @param start     Start point of the line segment.
     * @param end       End point of the line segment.
     * @param tolerance Tolerance distance.
     * @return True if near, false otherwise.
     */
    public boolean isMouseNearLine(PVector start, PVector end, float tolerance) {
        PVector mousePos = new PVector(mouseX, mouseY - TOP_BAR_HEIGHT);
        float distance = distPointToSegment(mousePos, start, end);
        return distance < tolerance;
    }

    /**
     * Calculates the distance from a point to a line segment.
     *
     * @param p Point.
     * @param v Line segment start.
     * @param w Line segment end.
     * @return Distance.
     */
    public static float distPointToSegment(PVector p, PVector v, PVector w) {
        float l2 = v.dist(w) * v.dist(w);
        if (l2 == 0.0) return p.dist(v);
        float t = Math.max(0, Math.min(1, PVector.sub(p, v).dot(PVector.sub(w, v)) / l2));
        PVector projection = PVector.add(v, PVector.sub(w, v).mult(t));
        return p.dist(projection);
    }

    public PImage getBallImage(int color) {
        return ballImages.get(color);
    }

    public void addBallToRemove(Ball ball) {
        ballsToBeRemoved.add(ball);
    }
    
    public void increaseScore(int amount) {
        currentScore += amount;
    }
    
    public void decreaseScore(int amount) {
        currentScore = Math.max(0, currentScore - amount);
    }
    
    public void addBallToQueue(String ballColorName) {
        ballsInQueue.add(ballColorName);
    }
    
    public float getScoreIncreaseModifier() {
        return scoreIncreaseModifier;
    }
    
    public float getScoreDecreaseModifier() {
        return scoreDecreaseModifier;
    }
    
    public Map<String, Integer> getScoreIncreaseMap() {
        return scoreIncreaseMap;
    }
    
    public Map<String, Integer> getScoreDecreaseMap() {
        return scoreDecreaseMap;
    }

    // public static void setScoreIncreaseMap(Map<String, Integer> scoreIncreaseMap) {
    //     App.scoreIncreaseMap = scoreIncreaseMap;
    // }
    
    // public static void setScoreDecreaseMap(Map<String, Integer> scoreDecreaseMap) {
    //     App.scoreDecreaseMap = scoreDecreaseMap;
    // }

    // public int getCurrentScore() { return currentScore; }

    public List<Ball> getBallsToBeRemoved() { return ballsToBeRemoved; }

    /**
     * Handles mouse dragged events.
     *
     * @param event MouseEvent.
     */
    @Override
    public void mouseDragged(MouseEvent event) {
        if (mouseButton == LEFT) {
            if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
                drawing = true;
                currentLine.add(new PVector(mouseX, mouseY - TOP_BAR_HEIGHT));
            }
        }
    }

    /**
     * Handles mouse released events.
     *
     * @param event MouseEvent.
     */
    @Override
    public void mouseReleased(MouseEvent event) {
        if (mouseButton == LEFT) {
            if (!currentLine.isEmpty()) {
                lines.add(new ArrayList<>(currentLine));
                currentLine.clear();
            }
            drawing = false;
        }
    }

    /**
     * The main game loop logic.
     */
    public void tick() {
        if (!levelLoaded) {
            loadLevel(currentLevel);
        }

        switch (gameState) {
            case RUNNING:
                updateGame();
                break;
            case LEVEL_COMPLETION_ANIMATION:
                playLevelCompletionAnimation();
                break;
            case LEVEL_COMPLETE:
                advanceLevel();
                break;
            case GAME_ENDED:
                resetGame();
                break;
            default:
                break;
        }

        drawTopBarLayer();
        drawLineLayer();
    }

    /**
     * Updates the game state when running.
     */
    public void updateGame() {
        if (ballsOnScreen.isEmpty() && ballsInQueue.isEmpty()) {
            drawBallLayer(); // clear ball layer (otherwise last ball to fall in hole will freeze on screen)
            image(ballLayer, 0, TOP_BAR_HEIGHT);
            gameState = GameState.LEVEL_COMPLETION_ANIMATION;
        } else {
            updateTime();
            drawBallLayer();
            drawConveyorBeltLayer();
        }
    }

    /**
     * Plays the level completion animation.
     */
    public void playLevelCompletionAnimation() {

        if (levelTime > 0) {
            if (draw) {
                drawLevelCompletionLayer();
            }
            draw = !draw;
        } else {
            gameState = GameState.LEVEL_COMPLETE;
        }
    }

    /**
     * Advances to the next level or ends the game if last level.
     */
    public void advanceLevel() {
        if (currentLevel < levels.size()) {
            currentLevel++;
            levelLoaded = false;
            gameState = GameState.RUNNING;
            previousScore = currentScore;
            lines.clear();
        } else {
            gameState = GameState.GAME_ENDED;
        }
    }

    /**
     * Resets the game after it has ended.
     */
    public void resetGame() {
        drawBallLayer(); // clear ball layer (otherwise last ball to fall in hole will freeze on screen)
        image(ballLayer, 0, TOP_BAR_HEIGHT);

        lines.clear();
    }

    /**
     * Updates the level time and spawn time.
     */
    public void updateTime() {
        levelFrames--;
        levelTime = levelFrames / FPS;
        spawnFrames--;
        spawnTime = (float) Math.ceil(spawnFrames / 3) / 10.0f;

        if (levelFrames == 0) {
            gameState = GameState.LEVEL_TIME_UP;
        }
    }

    int tile1Row = 1;
    int tile1Column = 0;
    int xDirection = 0;
    int yDirection = -1;

    /**
     * Draws the level completion layer.
     *
     * @return True if drawing occurs, false otherwise.
     */
    public boolean drawLevelCompletionLayer() {
        levelCompletionLayer.beginDraw();
        levelCompletionLayer.clear();

        tile1Row += yDirection;
        tile1Column += xDirection;
        int tile2Row = (BOARD_SIZE-1) - tile1Row;
        int tile2Column = (BOARD_SIZE-1) - tile1Column;
        if (tile1Row == 0 && tile1Column == 0) { // tile 1 reaches top left corner
            yDirection = 0;
            xDirection = 1;
        }
        if (tile1Row == 0 && tile1Column == 17) { // tile 1 reaches top right corner
            yDirection = 1;
            xDirection = 0;
        }
        if (tile1Row == 17 && tile1Column == 17) { // tile 1 reaches bottom right corner
            yDirection = 0;
            xDirection = -1;
        }
        if (tile1Row == 17 && tile1Column == 0) { // tile 1 reaches bottom left corner
            yDirection = -1;
            xDirection = 0;
        }

        int tile1X = tile1Column*CELL_SIZE;
        int tile1Y = tile1Row*CELL_SIZE;
        int tile2X = tile2Column*CELL_SIZE;
        int tile2Y = tile2Row*CELL_SIZE;

        levelCompletionLayer.image(wallImages.get(4), tile1X, tile1Y);
        levelCompletionLayer.image(wallImages.get(4), tile2X, tile2Y);

        levelTime--;
        currentScore++;

        levelCompletionLayer.endDraw();

        return !draw;
    }


    /**
     * Draws the line layer.
     */
    public void drawLineLayer() {
        lineLayer.beginDraw();
        lineLayer.clear();
        lineLayer.stroke(0);
        lineLayer.strokeWeight(10);

        List<List<PVector>> linesToBeRemoved = new ArrayList<>();

        // Draw existing lines
        for (List<PVector> line : lines) {
            for (int i = 1; i < line.size(); i++) {
                PVector start = line.get(i - 1);
                PVector end = line.get(i);
                lineLayer.line(start.x, start.y, end.x, end.y);
                if (gameState == GameState.RUNNING) {
                    for (Ball ball : ballsOnScreen) {
                        if (ball.handleCollisionWithLine(start, end)) {
                            linesToBeRemoved.add(line);
                            break;
                        }
                    }
                }
            }
        }

        for (List<PVector> line : linesToBeRemoved) {
            lines.remove(line);
        }

        // Draw the current line being drawn
        if (drawing && currentLine.size() > 1) {
            for (int i = 1; i < currentLine.size(); i++) {
                PVector start = currentLine.get(i - 1);
                PVector end = currentLine.get(i);
                lineLayer.line(start.x, start.y, end.x, end.y);
            }
        }

        lineLayer.endDraw();
    }

    /**
     * Draws the top bar layer.
     */
    public void drawTopBarLayer() {
        topBarLayer.beginDraw();
        topBarLayer.background(204);

        topBarLayer.fill(0);
        topBarLayer.textSize(20);
        topBarLayer.text("Score: " + currentScore, WIDTH - 120, 25);
        topBarLayer.text("Time: " + levelTime, WIDTH - 120, 50);

        if (!ballsInQueue.isEmpty()) {
            topBarLayer.text(String.format("%.1f", spawnTime), 190, 35);
        }

        switch (gameState) {
            case GAME_ENDED:
                topBarLayer.text("=== GAME ENDED ===", 200, 35);
                break;
            case LEVEL_TIME_UP:
                topBarLayer.text("=== TIME’S UP ===", 200, 35);
                break;
            case PAUSED:
                topBarLayer.text("*** PAUSED ***", 240, 35);
                break;
            default:
                break;
        }

        topBarLayer.endDraw();
    }

    int horizontalOffset = 0;

    /**
     * Draws the ball layer.
     */
    public void drawBallLayer() {
        ballLayer.beginDraw();
        ballLayer.clear();

        ballsToBeRemoved.clear();
        for (Ball ball : ballsOnScreen) {
            ball.checkSurrounding(board);
            ball.updatePosition();
            ballLayer.image(ball.getImage(), ball.getCenterXPosition() - Ball.RADIUS, ball.getCenterYPosition() - Ball.RADIUS);
        }

        ballsOnScreen.removeAll(ballsToBeRemoved);

        ballLayer.endDraw();
    }

    /**
     * Draws the conveyor belt layer.
     */
    public void drawConveyorBeltLayer() {
        conveyorBeltLayer.beginDraw();
        conveyorBeltLayer.background(0);

        int ballVerticalOffset = (conveyorBeltLayer.height - 2 * Ball.RADIUS) / 2;
        int spaceBetweenBalls = (conveyorBeltLayer.width - (5 * 2 * Ball.RADIUS)) / 6;

        if (spawnFrames == 0) {
            spawnFrames = FPS * spawnInterval;
            horizontalOffset = spaceBetweenBalls+2*Ball.RADIUS;
            if (!ballsInQueue.isEmpty()) {
                String ballColor = ballsInQueue.poll();
                Spawner spawner = spawners.get(RANDOM.nextInt(spawners.size()));
                float centerX = spawner.getCenterXPosition();
                float centerY = spawner.getCenterYPosition();
                int color = ColorCode.getValue(ballColor);
                Ball ball = new Ball(centerX, centerY, color, ballImages.get(color));
                ballsOnScreen.add(ball);
            }
        }

        Iterator<String> iterator = ballsInQueue.iterator();
        int count = 0;

        while (iterator.hasNext() && count < 5) {
            String ballColor = iterator.next();
            int ballHorizontalOffset = horizontalOffset + (count + 1) * spaceBetweenBalls + count * 2 * Ball.RADIUS;
            conveyorBeltLayer.image(ballImages.get(ColorCode.getValue(ballColor)), ballHorizontalOffset, ballVerticalOffset);
            count++;
        }

        if (horizontalOffset > 0) {
            horizontalOffset--;
        }

        conveyorBeltLayer.endDraw();
    }

    /**
     * Loads a level based on the current level number.
     *
     * @param levelNumber The current level number.
     */
    public void loadLevel(int levelNumber) {
        Level level = levels.get(levelNumber - 1);

        layout = level.getLayout();
        loadLayout(layout);

        levelTime = level.getTime();
        levelFrames = levelTime * FPS;
        spawnInterval = level.getSpawnInterval();
        spawnTime = spawnInterval;
        spawnFrames = (int) spawnTime * FPS;

        scoreIncreaseModifier = level.getScoreIncreaseModifier();
        scoreDecreaseModifier = level.getScoreDecreaseModifier();
        ballsInQueue = new ArrayDeque<>(level.getBalls());

        levelLoaded = true;
        gameState = GameState.RUNNING;
    }

    /**
     * Loads the layout from a file.
     *
     * @param layoutFile The layout file.
     */
    public void loadLayout(String layoutFile) {
        board = new Tile[BOARD_SIZE][BOARD_SIZE];
        ballsOnScreen = new ArrayList<>();
        spawners = new ArrayList<>();

        boardLayer.beginDraw();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(layoutFile));
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null && row < BOARD_SIZE) {
                for (int column = 0; column < BOARD_SIZE && column < line.length(); column++) {
                    char c = line.charAt(column);
                    int xPosition = column * CELL_SIZE;
                    int yPosition = row * CELL_SIZE;
                    int centerXPosition = column * CELL_SIZE + CELL_SIZE / 2;
                    int centerYPosition = row * CELL_SIZE + CELL_SIZE / 2;
                    int color;

                    switch(c) {
                        case 'X':
                            color = 0;
                            board[row][column] = new Wall(centerXPosition, centerYPosition, color);
                            boardLayer.image(wallImages.get(color), xPosition, yPosition);
                            break;
                        case '1':
                            color = 1;
                            board[row][column] = new Wall(centerXPosition, centerYPosition, color);
                            boardLayer.image(wallImages.get(color), xPosition, yPosition);
                            break;
                        case '2':
                            color = 2;
                            board[row][column] = new Wall(centerXPosition, centerYPosition, color);
                            boardLayer.image(wallImages.get(color), xPosition, yPosition);
                            break;
                        case '3':
                            color = 3;
                            board[row][column] = new Wall(centerXPosition, centerYPosition, color);
                            boardLayer.image(wallImages.get(color), xPosition, yPosition);
                            break;
                        case '4':
                            color = 4;
                            board[row][column] = new Wall(centerXPosition, centerYPosition, color);
                            boardLayer.image(wallImages.get(color), xPosition, yPosition);
                            break;
                        case 'V':
                            color = line.charAt(column + 1) - '0';
                            if (color > 4 || color < 0) {
                                return;
                            }
                            centerXPosition = column * CELL_SIZE + Wall.HALF_SIZE;
                            centerYPosition = row * CELL_SIZE + Wall.HALF_SIZE;
                            ColorRestrictingWall verticalColorRestrictingWall = new ColorRestrictingWall(centerXPosition, centerYPosition, color, true);
                            board[row][column] = verticalColorRestrictingWall;
                            boardLayer.image(verticalColorWallImages.get(color-1), xPosition, yPosition);
                            column++; // Skip the color character
                            xPosition = column * CELL_SIZE;
                            boardLayer.image(normalTileImage, xPosition, yPosition);
                            break;
                        case 'Z':
                            int offset = column;
                            while (line.charAt(offset + 1) - '0' > 9 || line.charAt(offset + 1) - '0' < 0) { // next char is not color
                                offset++;
                            }
                            color = line.charAt(offset + 1) - '0';
                            centerXPosition = column * CELL_SIZE + Wall.HALF_SIZE;
                            centerYPosition = row * CELL_SIZE + Wall.HALF_SIZE;
                            ColorRestrictingWall horizontalColorRestrictingWall = new ColorRestrictingWall(centerXPosition, centerYPosition, color, false);
                            board[row][column] = horizontalColorRestrictingWall;
                            boardLayer.image(horizontalColorWallImages.get(color-1), xPosition, yPosition);
                            if (offset == column) { // next char is color
                                column++;
                                xPosition = column * CELL_SIZE;
                                boardLayer.image(normalTileImage, xPosition, yPosition);
                            }
                            break;
                        case ' ':
                            if (board[row][column] == null) {
                                boardLayer.image(normalTileImage, xPosition, yPosition);
                            }
                            break;
                        case 'S':
                            Spawner spawner = new Spawner(centerXPosition, centerYPosition);
                            spawners.add(spawner);
                            board[row][column] = spawner;
                            boardLayer.image(spawnerImage, xPosition, yPosition);
                            break;
                        case 'H':
                            color = line.charAt(column + 1) - '0';
                            centerXPosition = column * CELL_SIZE + Hole.HALF_SIZE;
                            centerYPosition = row * CELL_SIZE + Hole.HALF_SIZE;
                            Hole hole = new Hole(centerXPosition, centerYPosition, color);
                            boardLayer.image(holeImages.get(color), xPosition, yPosition);
                            board[row][column] = hole;
                            board[row][column + 1] = hole;
                            board[row + 1][column] = hole;
                            board[row + 1][column + 1] = hole;
                            column++; // Skip the color character
                            break;
                        case 'B':
                            color = line.charAt(column + 1) - '0';
                            ballsOnScreen.add(new Ball(centerXPosition, centerYPosition, color, ballImages.get(color)));
                            boardLayer.image(normalTileImage, xPosition, yPosition);
                            column++; // Skip the color character
                            xPosition = column * CELL_SIZE;
                            boardLayer.image(normalTileImage, xPosition, yPosition);
                            break;
                    }

                }
                row++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + layoutFile);
            // e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading the file: " + layoutFile);
            // e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred.");
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Handle potential IOException from reader.close()
                e.printStackTrace();
            }
        }

        boardLayer.endDraw();
    }

    /**
     * Draws all elements in the game by current frame.
     */
    @Override
    public void draw() {
        tick();

        image(boardLayer, 0, TOP_BAR_HEIGHT);
        if (gameState == GameState.LEVEL_COMPLETION_ANIMATION) {
            image(levelCompletionLayer, 0, TOP_BAR_HEIGHT);
        }
        image(ballLayer, 0, TOP_BAR_HEIGHT);
        image(lineLayer, 0, TOP_BAR_HEIGHT);
        image(topBarLayer, 0, 0);
        image(conveyorBeltLayer, 10, 10);
    }

    /**
     * Main method to start the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}