package inkball;

import java.util.List;

/**
 * Represents a game level in the InkBall game.
 * Contains configuration details such as layout, time limits, and ball sequences.
 */
public class Level {

    private String layout;
    private int time;
    private int spawnInterval;
    private float scoreIncreaseModifier;
    private float scoreDecreaseModifier;
    private List<String> balls;

    /**
     * Constructs a new Level object with specified parameters.
     *
     * @param layout                The layout file name for the level.
     * @param time                  The time limit for the level in seconds.
     * @param spawnInterval         The interval between ball spawns in seconds.
     * @param scoreIncreaseModifier The modifier for score increases.
     * @param scoreDecreaseModifier The modifier for score decreases.
     * @param balls                 The list of balls to be spawned in the level.
     */
    public Level(String layout, int time, int spawnInterval, float scoreIncreaseModifier, float scoreDecreaseModifier, List<String> balls) {
        this.layout = layout;
        this.time = time;
        this.spawnInterval = spawnInterval;
        this.scoreIncreaseModifier = scoreIncreaseModifier;
        this.scoreDecreaseModifier = scoreDecreaseModifier;
        this.balls = balls;
    }

    /**
     * Gets the layout file name for the level.
     *
     * @return The layout file name.
     */
    public String getLayout() {
        return layout;
    }

    /**
     * Gets the time limit for the level in seconds.
     *
     * @return The time limit.
     */
    public int getTime() {
        return time;
    }

    /**
     * Gets the interval between ball spawns in seconds.
     *
     * @return The spawn interval.
     */
    public int getSpawnInterval() {
        return spawnInterval;
    }

    /**
     * Gets the modifier for score increases.
     *
     * @return The score increase modifier.
     */
    public float getScoreIncreaseModifier() {
        return scoreIncreaseModifier;
    }

    /**
     * Gets the modifier for score decreases.
     *
     * @return The score decrease modifier.
     */
    public float getScoreDecreaseModifier() {
        return scoreDecreaseModifier;
    }

    /**
     * Gets the list of balls to be spawned in the level.
     *
     * @return The list of balls.
     */
    public List<String> getBalls() {
        return balls;
    }
}