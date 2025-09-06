package inkball;

import processing.core.PVector;

/**
 * Represents a hole in the InkBall game. Balls can be attracted to holes and potentially captured.
 */
public class Hole extends Tile {

    public static final int HALF_SIZE = 32;
    public static final int ATTRACTION_RADIUS = 32;
    private int color;

    /**
     * Constructs a new Hole object.
     *
     * @param centerX The x-coordinate of the hole's center.
     * @param centerY The y-coordinate of the hole's center.
     * @param color   The color code of the hole.
     */
    public Hole(float centerX, float centerY, int color) {
        super(centerX, centerY);
        this.color = color;
    }

    /**
     * Handles the attraction of a ball towards the hole.
     *
     * @param ball     The ball being attracted.
     * @param distance The distance between the ball and the hole.
     */
    public void handleAttraction(Ball ball, float distance) {
        if (distance != 0) {
            // Calculate the attractive force vector
            PVector attractiveForce = PVector.sub(getCenterPosition(), ball.getCenterPosition());
            attractiveForce.mult((float) (ATTRACTION_RADIUS / distance * 0.01));
            ball.setVelocity(PVector.add(ball.getVelocity(), attractiveForce));
        }

        // Check if the ball is within the capture tolerance
        int tolerance = Ball.RADIUS;
        if (Math.abs(ball.getCenterXPosition() - centerXPosition) < tolerance && Math.abs(ball.getCenterYPosition() - centerYPosition) < tolerance) {
            captureBall(ball);
        }
    }

    /**
     * Captures the ball when it reaches the hole.
     *
     * @param ball The ball to be captured.
     */
    private void captureBall(Ball ball) {
        App.getInstance().addBallToRemove(ball);

        String ballColorName = ColorCode.fromValue(ball.getColor());

        if (ball.getColor() == 0 || this.color == 0 || ball.getColor() == this.color) {
            // Correct hole or grey hole
            int scoreIncrease = (int) (App.getInstance().getScoreIncreaseMap().get(ballColorName) * App.getInstance().getScoreIncreaseModifier());
            App.getInstance().increaseScore(scoreIncrease);
            
        } else {
            // Wrong hole
            int scoreDecrease = (int) (App.getInstance().getScoreDecreaseMap().get(ballColorName) * App.getInstance().getScoreDecreaseModifier());
            App.getInstance().decreaseScore(scoreDecrease);
            App.getInstance().addBallToQueue(ballColorName);
        }
    }

    /**
     * Returns the color of the hole.
     *
     * @return The color code.
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the color of the hole.
     *
     * @param color The new color.
     */
    public void setColor(int color) {
        this.color = color;
    }
}