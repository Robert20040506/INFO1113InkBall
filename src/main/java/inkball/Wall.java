package inkball;

import processing.core.PVector;

/**
 * Represents a wall tile in the InkBall game. Walls can collide with balls and change their direction or color.
 */
public class Wall extends Tile {

    private final int color;

    /**
     * Constructs a new Wall object.
     *
     * @param centerX The x-coordinate of the wall's center.
     * @param centerY The y-coordinate of the wall's center.
     * @param color   The color code of the wall.
     */
    public Wall(float centerX, float centerY, int color) {
        super(centerX, centerY);
        this.color = color;
    }

    /**
     * Checks if the ball collides with this wall.
     *
     * @param ball The ball to check collision with.
     * @return True if collision occurs, false otherwise.
     */
    public boolean collidesWithBall(Ball ball) {
        // Axis-Aligned Bounding Box (AABB) collision detection
        float ballLeft = ball.getCenterXPosition() - Ball.RADIUS;
        float ballRight = ball.getCenterXPosition() + Ball.RADIUS;
        float ballTop = ball.getCenterYPosition() - Ball.RADIUS;
        float ballBottom = ball.getCenterYPosition() + Ball.RADIUS;

        float wallLeft = topLeftCorner.x;
        float wallRight = topRightCorner.x;
        float wallTop = topLeftCorner.y;
        float wallBottom = bottomLeftCorner.y;

        // Check for collision
        return (ballRight >= wallLeft && ballLeft <= wallRight && ballBottom >= wallTop && ballTop <= wallBottom);
    }

    /**
     * Handles the collision between the ball and this wall.
     *
     * @param ball The ball that collided with the wall.
     */
    public void handleCollision(Ball ball) {
        // Ensure ball only collides with one wall at a time
        if (ball.colliding) {
            return;
        } else {
            ball.colliding = true;
        }
        // System.out.println(this);
        // Determine the side of collision
        float overlapLeft = (ball.centerXPosition + Ball.RADIUS) - topLeftCorner.x;
        float overlapRight = topRightCorner.x - (ball.centerXPosition - Ball.RADIUS);
        float overlapTop = (ball.centerYPosition + Ball.RADIUS) - topLeftCorner.y;
        float overlapBottom = bottomLeftCorner.y - (ball.centerYPosition - Ball.RADIUS);

        // Find the minimal overlap
        float minOverlapX = Math.min(overlapLeft, overlapRight);
        float minOverlapY = Math.min(overlapTop, overlapBottom);

        // Resolve collision
        if (minOverlapX < minOverlapY) {
            // Horizontal collision
            if (overlapLeft < overlapRight) {
                // Collision on the left side
                ball.centerXPosition -= overlapLeft;
            } else {
                // Collision on the right side
                ball.centerXPosition += overlapRight;
            }
            PVector velocity = ball.getVelocity();
            velocity.x *= -1; // Reverse X velocity
            ball.setVelocity(velocity);
        } else {
            // Vertical collision
            if (overlapTop < overlapBottom) {
                // Collision on the top side
                ball.centerYPosition -= overlapTop;
            } else {
                // Collision on the bottom side
                ball.centerYPosition += overlapBottom;
            }
            PVector velocity = ball.getVelocity();
            velocity.y *= -1; // Reverse Y velocity
            ball.setVelocity(velocity);
        }

        // System.out.println("Colliding");

        if (this.getClass() != ColorRestrictingWall.class) {
            if (this.color != 0) {
                ball.setColor(this.color, App.getInstance().ballImages.get(this.color));
            }
        }
        
    }

    /**
     * Returns the color code of the wall.
     *
     * @return The color code.
     */
    public int getColor() {
        return color;
    }
}