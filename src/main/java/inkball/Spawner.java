package inkball;

/**
 * Represents a spawner tile in the InkBall game.
 * Spawners are locations from which balls can enter the game.
 */
public class Spawner extends Tile {

    /**
     * Constructs a new Spawner object at the specified position.
     *
     * @param centerX The x-coordinate of the spawner's center position.
     * @param centerY The y-coordinate of the spawner's center position.
     */
    public Spawner(float centerX, float centerY) {
        super(centerX, centerY);
    }
}