package com.s2tn.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HintTest {

    private Hint hint;

    @BeforeEach
    void setUp() {
        hint = new Hint(1, "This is a test hint.", 5.0);
    }

    @Test
    @DisplayName("Default constructor should initialize with default values")
    void testDefaultConstructor() {
        Hint defaultHint = new Hint();
        assertEquals(0, defaultHint.getLevel());
        assertEquals("", defaultHint.getText());
        assertEquals(0.0, defaultHint.getCost());
    }

    @Test
    @DisplayName("Parameterized constructor should initialize with provided values")
    void testParameterizedConstructor() {
        assertEquals(1, hint.getLevel());
        assertEquals("This is a test hint.", hint.getText());
        assertEquals(5.0, hint.getCost());
    }

    @Test
    @DisplayName("Parameterized constructor should handle null text by setting it to empty string")
    void testParameterizedConstructorWithNullText() {
        Hint nullTextHint = new Hint(2, null, 10.0);
        assertEquals(2, nullTextHint.getLevel());
        assertEquals("", nullTextHint.getText());
        assertEquals(10.0, nullTextHint.getCost());
    }

    @Test
    @DisplayName("getLevel and setLevel should work correctly")
    void testGetSetLevel() {
        hint.setLevel(2);
        assertEquals(2, hint.getLevel());
    }

    @Test
    @DisplayName("getText and setText should work correctly")
    void testGetSetText() {
        hint.setText("New hint text.");
        assertEquals("New hint text.", hint.getText());
        hint.setText(null);
        assertEquals("", hint.getText(), "setText(null) should result in an empty string.");
    }

    @Test
    @DisplayName("getHint should return the same as getText")
    void testGetHint() {
        assertEquals(hint.getText(), hint.getHint());
    }

    @Test
    @DisplayName("getCost and setCost should work correctly")
    void testGetSetCost() {
        hint.setCost(7.5);
        assertEquals(7.5, hint.getCost());
        hint.setCost(0.0);
        assertEquals(0.0, hint.getCost());
        hint.setCost(-2.0); // Cost can be negative based on current implementation
        assertEquals(-2.0, hint.getCost());
    }

    @Test
    void testToString() {
        String expected = "Hint(level=1, cost=5.0, text=\"This is a test hint.\")";
        assertEquals(expected, hint.toString());

        Hint defaultHint = new Hint();
        String expectedDefault = "Hint(level=0, cost=0.0, text=\"\")";
        assertEquals(expectedDefault, defaultHint.toString());
    }

    @Test
    @DisplayName("Demonstrate that equals() and hashCode() are not overridden")
    void testEqualsAndHashCodeDefaultBehavior() {
        Hint sameContentHint = new Hint(1, "This is a test hint.", 5.0);

        assertNotSame(hint, sameContentHint); // They are different objects in memory
        assertFalse(hint.equals(sameContentHint),
                "Without overriding equals(), logically identical objects are considered different.");
        // This means if you add 'hint' to a Set and then try to add 'sameContentHint', both would be present.
    }
}