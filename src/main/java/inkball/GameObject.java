package inkball;

import processing.core.PVector;

/**
 * Base class for all game objects in the InkBall game.
 * Provides common properties and methods for position and distance calculation.
 */
public abstract class GameObject {

    protected float centerXPosition;
    protected float centerYPosition;

    /**
     * Constructs a new GameObject at the specified position.
     *
     * @param centerX The x-coordinate of the object's center position.
     * @param centerY The y-coordinate of the object's center position.
     */
    public GameObject(float centerX, float centerY) {
        this.centerXPosition = centerX;
        this.centerYPosition = centerY;
    }

    /**
     * Calculates the distance from this object to another GameObject.
     *
     * @param other The other GameObject.
     * @return The distance between the two objects.
     */
    public float distanceTo(GameObject other) {
        float deltaX = this.centerXPosition - other.centerXPosition;
        float deltaY = this.centerYPosition - other.centerYPosition;
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Gets the x and y-coordinates of the object's center position.
     *
     * @return The x and y-coordinates.
     */
    public PVector getCenterPosition() {
        return new PVector(centerXPosition, centerYPosition);
    }

    /**
     * Gets the x-coordinate of the object's center position.
     *
     * @return The x-coordinate.
     */
    public float getCenterXPosition() {
        return centerXPosition;
    }
    

    /**
     * Sets the x-coordinate of the object's center position.
     *
     * @param centerX The new x-coordinate.
     */
    public void setCenterXPosition(float centerX) {
        this.centerXPosition = centerX;
    }

    /**
     * Gets the y-coordinate of the object's center position.
     *
     * @return The y-coordinate.
     */
    public float getCenterYPosition() {
        return centerYPosition;
    }

    /**
     * Sets the y-coordinate of the object's center position.
     *
     * @param centerY The new y-coordinate.
     */
    public void setCenterYPosition(float centerY) {
        this.centerYPosition = centerY;
    }
}