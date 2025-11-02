package com.s2tn.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemTest {

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        item1 = new Item("KEY", "Old Key");
        item2 = new Item("POTION");
    }

    @Test
    @DisplayName("Constructor with key only should set display name to key")
    void testConstructorKeyOnly() {
        assertEquals("POTION", item2.getKey());
        assertEquals("POTION", item2.getDisplayName());

        Item nullKeyItem = new Item(null);
        assertEquals("", nullKeyItem.getKey());
        assertEquals("", nullKeyItem.getDisplayName());

        Item blankKeyItem = new Item("  ");
        assertEquals("  ", blankKeyItem.getKey()); // Key is not trimmed
        assertEquals("  ", blankKeyItem.getDisplayName()); // DisplayName is not trimmed if key is blank
    }

    @Test
    @DisplayName("Constructor with key and display name should set both correctly")
    void testConstructorKeyAndDisplayName() {
        assertEquals("KEY", item1.getKey());
        assertEquals("Old Key", item1.getDisplayName());

        Item nullDisplayNameItem = new Item("SWORD", null);
        assertEquals("SWORD", nullDisplayNameItem.getKey());
        assertEquals("SWORD", nullDisplayNameItem.getDisplayName(), "Null display name should default to key.");

        Item blankDisplayNameItem = new Item("SHIELD", "  ");
        assertEquals("SHIELD", blankDisplayNameItem.getKey());
        assertEquals("SHIELD", blankDisplayNameItem.getDisplayName(), "Blank display name should default to key.");

        Item nullKeyAndDisplayName = new Item(null, null);
        assertEquals("", nullKeyAndDisplayName.getKey());
        assertEquals("", nullKeyAndDisplayName.getDisplayName());
    }

    @Test
    @DisplayName("getKey should return the correct key")
    void testGetKey() {
        assertEquals("KEY", item1.getKey());
        assertEquals("POTION", item2.getKey());
    }

    @Test
    @DisplayName("getDisplayName should return the correct display name")
    void testGetDisplayName() {
        assertEquals("Old Key", item1.getDisplayName());
        assertEquals("POTION", item2.getDisplayName());
    }

    @Test
    @DisplayName("toString should return a formatted string")
    void testToString() {
        assertEquals("Old Key [KEY]", item1.toString());
        assertEquals("POTION [POTION]", item2.toString());

        Item emptyItem = new Item(null);
        assertEquals(" []", emptyItem.toString()); // Note: current implementation will produce " []" for empty key
    }

    @Test
    @DisplayName("equals should compare items based on their key")
    void testEquals() {
        Item sameKeyItem = new Item("KEY", "Shiny Key");
        Item differentKeyItem = new Item("COIN", "Gold Coin");

        // Same object
        assertTrue(item1.equals(item1));
        // Logically equal (same key)
        assertTrue(item1.equals(sameKeyItem));
        // Not equal (different key)
        assertFalse(item1.equals(differentKeyItem));
        // Not equal (null)
        assertFalse(item1.equals(null));
        // Not equal (different class)
        assertFalse(item1.equals("KEY"));
    }

    @Test
    @DisplayName("hashCode should be consistent with equals")
    void testHashCode() {
        Item sameKeyItem = new Item("KEY", "Shiny Key");
        Item differentKeyItem = new Item("COIN", "Gold Coin");

        // Equal objects must have equal hash codes
        assertEquals(item1.hashCode(), sameKeyItem.hashCode());
        // Unequal objects should ideally have different hash codes (though not strictly required)
        assertNotEquals(item1.hashCode(), differentKeyItem.hashCode());
    }
}