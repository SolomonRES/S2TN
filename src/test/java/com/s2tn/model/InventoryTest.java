package com.s2tn.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
    }

    @Test
    @DisplayName("add(String) should add items and increment counts")
    void add() {
        inventory.add("KEY");
        assertEquals(1, inventory.getCount("KEY"));

        inventory.add("KEY");
        assertEquals(2, inventory.getCount("KEY"));

        inventory.add("COIN");
        assertEquals(1, inventory.getCount("COIN"));
        assertEquals(2, inventory.size());
    }

    @Test
    @DisplayName("add(String) should ignore null or blank keys")
    void add_shouldIgnoreNullOrBlank() {
        inventory.add((Item)null);
        assertEquals(0, inventory.size());

        inventory.add("  ");
        assertEquals(0, inventory.size());
    }

    @Test
    @DisplayName("add(Item) should add items by their key")
    void testAdd() {
        Item item = new Item("GEM", "A shiny gem");
        inventory.add(item);
        assertTrue(inventory.has("GEM"));
        assertEquals(1, inventory.getCount("GEM"));

        inventory.add((Item) null);
        assertEquals(1, inventory.size(), "Adding a null item should not change the inventory.");
    }

    @Test
    @DisplayName("has should correctly report item presence")
    void has() {
        assertFalse(inventory.has("KEY"));
        inventory.add("KEY");
        assertTrue(inventory.has("KEY"));
        assertFalse(inventory.has("COIN"));
    }

    @Test
    @DisplayName("getCount should return the correct count or 0")
    void getCount() {
        assertEquals(0, inventory.getCount("NON_EXISTENT"));
        inventory.add("KEY");
        inventory.add("KEY");
        assertEquals(2, inventory.getCount("KEY"));
    }

    @Test
    @DisplayName("use should decrement count or remove item")
    void use() {
        inventory.add("POTION");
        inventory.add("POTION");

        assertTrue(inventory.use("POTION"));
        assertEquals(1, inventory.getCount("POTION"));

        assertTrue(inventory.use("POTION"));
        assertEquals(0, inventory.getCount("POTION"));
        assertFalse(inventory.has("POTION"));

        assertFalse(inventory.use("POTION"), "Using an item that is out of stock should return false.");
        assertFalse(inventory.use("NON_EXISTENT"), "Using an item that never existed should return false.");
    }

    @Test
    @DisplayName("removeAll should remove all instances of an item")
    void removeAll() {
        inventory.add("ARROW");
        inventory.add("ARROW");
        assertTrue(inventory.removeAll("ARROW"));
        assertFalse(inventory.has("ARROW"));
        assertEquals(0, inventory.getCount("ARROW"));

        assertFalse(inventory.removeAll("NON_EXISTENT"), "Removing a non-existent item should return false.");
    }

    @Test
    @DisplayName("listKeys should return an unmodifiable list of unique keys")
    void listKeys() {
        inventory.add("KEY");
        inventory.add("COIN");
        inventory.add("KEY");

        List<String> keys = inventory.listKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("KEY"));
        assertTrue(keys.contains("COIN"));

        assertThrows(UnsupportedOperationException.class, () -> keys.add("BOMB"), "The returned list should be unmodifiable.");
    }

    @Test
    @DisplayName("size should return the number of unique items")
    void size() {
        assertEquals(0, inventory.size());
        inventory.add("KEY");
        assertEquals(1, inventory.size());
        inventory.add("COIN");
        assertEquals(2, inventory.size());
        inventory.add("KEY"); // Duplicate
        assertEquals(2, inventory.size(), "Adding a duplicate item should not increase the size.");
    }

    @Test
    @DisplayName("clear should remove all items from the inventory")
    void clear() {
        inventory.add("ITEM1");
        inventory.add("ITEM2");
        assertFalse(inventory.listKeys().isEmpty());

        inventory.clear();
        assertTrue(inventory.listKeys().isEmpty());
        assertEquals(0, inventory.size());
    }

    @Test
    @DisplayName("toString should provide a clear representation of the inventory")
    void testToString() {
        assertEquals("Inventory{}", inventory.toString());
        inventory.add("KEY");
        inventory.add("COIN");
        inventory.add("KEY");
        assertEquals("Inventory{KEY=2, COIN=1}", inventory.toString());
    }
}