package inkball;

/**
 * Represents a color-restricting wall in the InkBall game.
 * Only allows balls of a specific color to pass through.
 */
public class ColorRestrictingWall extends Wall {

    private boolean isVertical;

    /**
     * Constructs a new ColorRestrictingWall object.
     *
     * @param centerX   The x-coordinate of the wall's center position.
     * @param centerY   The y-coordinate of the wall's center position.
     * @param color     The color code that is allowed to pass through.
     * @param isVertical True if the wall is vertical; false if horizontal.
     */
    public ColorRestrictingWall(float centerX, float centerY, int color, boolean isVertical) {
        super(centerX, centerY, color);
        this.isVertical = isVertical;
        adjustWallSize();
    }

    /**
     * Adjusts the wall size to make it thinner based on its orientation.
     */
    private void adjustWallSize() {
        if (isVertical) {
            // Vertical wall: adjust horizontal dimensions
            topLeftCorner.x += 11;
            topRightCorner.x -= 11;
            bottomLeftCorner.x += 11;
            bottomRightCorner.x -= 11;
        } else {
            // Horizontal wall: adjust vertical dimensions
            topLeftCorner.y += 11;
            topRightCorner.y += 11;
            bottomLeftCorner.y -= 11;
            bottomRightCorner.y -= 11;
        }
    }

    /**
     * Handles the collision between the ball and the color-restricting wall.
     * Only blocks balls that do not match the wall's color.
     *
     * @param ball The ball that collided with the wall.
     */
    @Override
    public void handleCollision(Ball ball) {
        if (ball.getColor() == this.getColor() || ball.getColor() == 0) {
            // Allow the ball to pass through if the colors match
            return;
        }

        // Otherwise, block the ball like a regular wall
        super.handleCollision(ball);
    }

    /**
     * Checks if the wall is vertical.
     *
     * @return True if vertical; false otherwise.
     */
    public boolean isVertical() {
        return isVertical;
    }
}