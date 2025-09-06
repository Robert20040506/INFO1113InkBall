package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ColorCodeTest {

    @Test
    public void testGetValue_ValidColor() {
        // Test getting the value for a valid color name
        int value = ColorCode.getValue("orange");
        assertEquals(1, value);
    }

    @Test
    public void testGetValue_InvalidColor() {
        // Test that an exception is thrown for an invalid color name
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ColorCode.getValue("purple");
        });
        assertEquals("Invalid color name: purple", exception.getMessage());
    }

    @Test
    public void testFromValue_ValidValue() {
        // Test getting the color name from a valid value
        String colorName = ColorCode.fromValue(2);
        assertEquals("blue", colorName);
    }

    @Test
    public void testFromValue_InvalidValue() {
        // Test that an exception is thrown for an invalid value
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ColorCode.fromValue(99);
        });
        assertEquals("Invalid value: 99", exception.getMessage());
    }
}