package inkball;

import processing.core.PVector;

/**
 * Represents a tile in the InkBall game grid.
 * Tiles are the building blocks of the game board and can represent various elements like walls, holes, etc.
 */
public class Tile extends GameObject {

    public static final int HALF_SIZE = 16;

    protected PVector topLeftCorner;
    protected PVector topRightCorner;
    protected PVector bottomLeftCorner;
    protected PVector bottomRightCorner;

    /**
     * Constructs a new Tile object at the specified position.
     *
     * @param centerX The x-coordinate of the tile's center position.
     * @param centerY The y-coordinate of the tile's center position.
     */
    public Tile(float centerX, float centerY) {
        super(centerX, centerY);
        initializeCorners();
    }

    /**
     * Initializes the corner positions of the tile based on its center position.
     */
    private void initializeCorners() {
        float left = centerXPosition - HALF_SIZE;
        float right = centerXPosition + HALF_SIZE;
        float top = centerYPosition - HALF_SIZE;
        float bottom = centerYPosition + HALF_SIZE;

        topLeftCorner = new PVector(left, top);
        topRightCorner = new PVector(right, top);
        bottomLeftCorner = new PVector(left, bottom);
        bottomRightCorner = new PVector(right, bottom);
    }

    /**
     * Gets the top-left corner position of the tile.
     *
     * @return The top-left corner position.
     */
    public PVector getTopLeftCorner() {
        return topLeftCorner;
    }

    /**
     * Gets the top-right corner position of the tile.
     *
     * @return The top-right corner position.
     */
    public PVector getTopRightCorner() {
        return topRightCorner;
    }

    /**
     * Gets the bottom-left corner position of the tile.
     *
     * @return The bottom-left corner position.
     */
    public PVector getBottomLeftCorner() {
        return bottomLeftCorner;
    }

    /**
     * Gets the bottom-right corner position of the tile.
     *
     * @return The bottom-right corner position.
     */
    public PVector getBottomRightCorner() {
        return bottomRightCorner;
    }
}