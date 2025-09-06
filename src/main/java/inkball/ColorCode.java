package inkball;

/**
 * Enumeration representing the color codes used in the InkBall game.
 * Provides mappings between color names and their corresponding integer values.
 */
public enum ColorCode {
    GREY(0),
    ORANGE(1),
    BLUE(2),
    GREEN(3),
    YELLOW(4);

    private final int value;

    /**
     * Constructs a ColorCode enum with the specified integer value.
     *
     * @param value The integer value associated with the color.
     */
    ColorCode(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with the specified color name.
     *
     * @param colorName The name of the color.
     * @return The integer value of the color.
     * @throws IllegalArgumentException If the color name is invalid.
     */
    public static int getValue(String colorName) {
        try {
            return ColorCode.valueOf(colorName.toUpperCase()).value;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid color name: " + colorName);
        }
    }

    /**
     * Gets the color name associated with the specified integer value.
     *
     * @param value The integer value of the color.
     * @return The name of the color.
     * @throws IllegalArgumentException If the value is invalid.
     */
    public static String fromValue(int value) {
        for (ColorCode color : ColorCode.values()) {
            if (color.value == value) {
                return color.name().toLowerCase();
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

    /**
     * Gets the integer value of the color.
     *
     * @return The integer value.
     */
    public int getValue() {
        return value;
    }
}