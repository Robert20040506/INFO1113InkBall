package inkball;

import processing.core.PImage;
import processing.core.PVector;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

/**
 * Represents a ball in the InkBall game.
 */
public class Ball extends GameObject {

    public static final int RADIUS = 12;
    private static final Random RANDOM = new Random();
    public boolean colliding = false;

    private PVector velocity;
    private int color;
    private PImage image;
    private Set<Tile> surroundingTiles;

    /**
     * Constructs a new Ball object.
     *
     * @param centerX The x-coordinate of the ball's center.
     * @param centerY The y-coordinate of the ball's center.
     * @param color   The color code of the ball.
     * @param image   The image representing the ball.
     */
    public Ball(float centerX, float centerY, int color, PImage image) {
        super(centerX, centerY);
        this.velocity = new PVector(getRandomVelocity(), getRandomVelocity());
        this.color = color;
        this.image = image;
        this.surroundingTiles = new TreeSet<>(new TileComparator(this));
    }

    // Custom Comparator to sort by age
    class TileComparator implements Comparator<Tile> {
        private Ball ball;

        public TileComparator(Ball ball) {
            this.ball = ball;
        }

        @Override
        public int compare(Tile tile1, Tile tile2) {
            return Float.compare(ball.distanceTo(tile1), ball.distanceTo(tile2)); // Sort tiles in ascending order by distance to ball
        }
    }

    /**
     * Returns a random velocity component, either -2 or 2.
     *
     * @return The random velocity component.
     */
    private float getRandomVelocity() {
        return RANDOM.nextBoolean() ? 2 : -2;
    }

    /**
     * Updates the position of the ball based on its velocity and handles bouncing
     * off the edges.
     */
    public void updatePosition() {
        centerXPosition += velocity.x;
        centerYPosition += velocity.y;

        // Bounce off the edges of the screen
        if (centerXPosition - RADIUS < 0) {
            centerXPosition = RADIUS;
            velocity.x *= -1;
        }
        if (centerXPosition + RADIUS > App.WIDTH) {
            centerXPosition = App.WIDTH - RADIUS;
            velocity.x *= -1;
        }
        if (centerYPosition - RADIUS < 0) {
            centerYPosition = RADIUS;
            velocity.y *= -1;
        }
        if (centerYPosition + RADIUS > App.HEIGHT) {
            centerYPosition = App.HEIGHT - RADIUS;
            velocity.y *= -1;
        }
    }

    /**
     * Checks the surrounding tiles for collisions and handles interactions.
     *
     * @param board The game board.
     */
    public void checkSurrounding(Tile[][] board) {
        int row = (int) (centerYPosition / App.CELL_SIZE);
        int column = (int) (centerXPosition / App.CELL_SIZE);

        // Clear previous surrounding tiles
        surroundingTiles.clear();

        if (row < 0 || row > 17 || column < 0 || column > 17) {
            return;
        }

        // Get surrounding tiles
        // Above
        if (row > 0 && board[row - 1][column] != null) {
            surroundingTiles.add(board[row - 1][column]);
        }
        // Below
        if (row < App.BOARD_SIZE - 1 && board[row + 1][column] != null) {
            surroundingTiles.add(board[row + 1][column]);
        }
        // Left
        if (column > 0 && board[row][column - 1] != null) {
            surroundingTiles.add(board[row][column - 1]);
        }
        // Right
        if (column < App.BOARD_SIZE - 1 && board[row][column + 1] != null) {
            surroundingTiles.add(board[row][column + 1]);
        }
        // Current Tile
        if (board[row][column] != null) {
            surroundingTiles.add(board[row][column]);
        }

        // Collision detection with surrounding tiles
        for (Tile tile : surroundingTiles) {
            if (tile instanceof Wall) {
                Wall wall = (Wall) tile;
                if (wall.collidesWithBall(this)) {
                    wall.handleCollision(this);
                }
            } else if (tile instanceof Hole) {
                Hole hole = (Hole) tile;
                float distance = distanceTo(hole);

                if (distance <= Hole.ATTRACTION_RADIUS) {
                    hole.handleAttraction(this, distance);
                    // Resize the ball image based on the distance to the hole
                    image = App.getInstance().getBallImage(color).copy();
                    int scaledSize = (int) (distance / Hole.ATTRACTION_RADIUS * 2 * RADIUS);
                    image.resize(scaledSize, scaledSize);
                }
            }
        }

        colliding = false;
    }

    /**
     * Handles collision with a drawn line.
     *
     * @param lineStart The start point of the line segment.
     * @param lineEnd   The end point of the line segment.
     * @return True if collision occurred, false otherwise.
     */
    public boolean handleCollisionWithLine(PVector lineStart, PVector lineEnd) {
        PVector futurePosition = PVector.add(new PVector(centerXPosition, centerYPosition), velocity);
        float distance = App.distPointToSegment(futurePosition, lineStart, lineEnd);

        if (distance <= RADIUS) {
            PVector normalVector = calculateNormal(futurePosition, lineStart, lineEnd);

            if (normalVector.mag() != 0) {
                normalVector.normalize();
                float dotProduct = PVector.dot(velocity, normalVector);
                velocity = PVector.sub(velocity, PVector.mult(normalVector, 2 * dotProduct));
                return true;
            }
        }

        return false;
    }

    /**
     * Calculates the normal vector from a point to a line segment.
     *
     * @param point The point.
     * @param A     The start point of the line segment.
     * @param B     The end point of the line segment.
     * @return The normal vector.
     */
    public PVector calculateNormal(PVector point, PVector A, PVector B) {
        PVector direction = PVector.sub(B, A);
        float dirMagSq = PVector.dot(direction, direction);

        if (dirMagSq == 0) {
            // The line segment is a point
            return PVector.sub(point, A);
        }

        PVector PA = PVector.sub(point, A);
        float projectionScale = PVector.dot(PA, direction) / dirMagSq;
        PVector projection = PVector.mult(direction, projectionScale);
        PVector normal = PVector.sub(PA, projection);

        return normal;
    }

    /**
     * Sets the color of the ball and updates its image.
     *
     * @param color The new color code.
     * @param image The new image.
     */
    public void setColor(int color, PImage image) {
        this.color = color;
        this.image = image;
    }

    /**
     * Returns the color code of the ball.
     *
     * @return The color code.
     */
    public int getColor() {
        return color;
    }

    /**
     * Returns the image of the ball.
     *
     * @return The image.
     */
    public PImage getImage() {
        return image;
    }

    public float getCenterXPosition() {
        return centerXPosition;
    }

    public void setCenterXPosition(float x) {
        centerXPosition = x;
    }

    public float getCenterYPosition() {
        return centerYPosition;
    }

    public void setCenterYPosition(float y) {
        centerYPosition = y;
    }

    public Set<Tile> getSurroundingTiles() {
        return surroundingTiles;
    }

    /**
     * Returns the velocity of the ball.
     *
     * @return The velocity vector.
     */
    public PVector getVelocity() {
        return velocity;
    }

    /**
     * Sets the velocity of the ball.
     *
     * @param velocity The new velocity vector.
     */
    public void setVelocity(PVector velocity) {
        this.velocity = velocity;
    }
}