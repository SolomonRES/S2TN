package com.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DungeonListTest {

    private DungeonList dungeonList;
    private Dungeon dungeon1;
    private Dungeon dungeon2;
    private UUID dungeon1Id;
    private UUID dungeon2Id;

    @BeforeEach
    void setUp() {
        dungeonList = DungeonList.getInstance();
        // IMPORTANT: Clear the list before each test to ensure isolation
        dungeonList.clear();

        // Create some test dungeons
        dungeon1 = new Dungeon("Crypt of the Damned", new ArrayList<>(), 600000, Difficulty.NORMAL, null);
        dungeon2 = new Dungeon("Wizard's Tower", new ArrayList<>(), 900000, Difficulty.HARD, null);
        dungeon1Id = dungeon1.getUUID();
        dungeon2Id = dungeon2.getUUID();

        // Pre-populate the list for most tests
        dungeonList.addDungeon(dungeon1);
        dungeonList.addDungeon(dungeon2);
    }

    @AfterEach
    void tearDown() {
        dungeonList.clear();
        dungeonList = null;
    }

    @Test
    @DisplayName("getInstance should return a non-null singleton instance")
    void getInstance() {
        assertNotNull(dungeonList, "getInstance() should not return null.");
        DungeonList anotherInstance = DungeonList.getInstance();
        assertSame(dungeonList, anotherInstance, "getInstance() should always return the same instance.");
    }

    @Test
    @DisplayName("getById should return the correct dungeon or null")
    void getById() {
        assertEquals(dungeon1, dungeonList.getById(dungeon1Id));
        assertEquals(dungeon2, dungeonList.getById(dungeon2Id));
        assertNull(dungeonList.getById(UUID.randomUUID()), "Should return null for a non-existent ID.");
        assertNull(dungeonList.getById(null), "Should return null for a null ID.");
    }

    @Test
    @DisplayName("getByName should return the correct dungeon case-insensitively or null")
    void getByName() {
        assertEquals(dungeon1, dungeonList.getByName("Crypt of the Damned"));
        assertEquals(dungeon1, dungeonList.getByName("crypt of the damned"), "Should match case-insensitively.");
        assertEquals(dungeon2, dungeonList.getByName("Wizard's Tower"));
        assertNull(dungeonList.getByName("Unknown Dungeon"), "Should return null for a non-existent name.");
        assertNull(dungeonList.getByName(null), "Should return null for a null name.");
    }

    @Test
    @DisplayName("replaceAll should replace all dungeons in the list")
    void replaceAll() {
        assertEquals(2, dungeonList.getAll().size());

        Dungeon dungeon3 = new Dungeon("Sunken Ship", new ArrayList<>(), 300000, Difficulty.EASY, null);
        ArrayList<Dungeon> newList = new ArrayList<>();
        newList.add(dungeon3);

        dungeonList.replaceAll(newList);
        assertEquals(1, dungeonList.getAll().size());
        assertTrue(dungeonList.getAll().contains(dungeon3));
        assertFalse(dungeonList.getAll().contains(dungeon1));

        // Test replacing with null
        dungeonList.replaceAll(null);
        assertTrue(dungeonList.getAll().isEmpty(), "Replacing with null should clear the list.");
    }

    @Test
    @DisplayName("removeDungeon should remove the specified dungeon and return true")
    void removeDungeon() {
        assertTrue(dungeonList.removeDungeon(dungeon1Id), "Should return true on successful removal.");
        assertEquals(1, dungeonList.getAll().size());
        assertNull(dungeonList.getById(dungeon1Id));

        assertFalse(dungeonList.removeDungeon(UUID.randomUUID()), "Should return false for a non-existent ID.");
        assertFalse(dungeonList.removeDungeon(null), "Should return false for a null ID.");
    }

    @Test
    @DisplayName("toJSON should return an empty string as it is unimplemented")
    void toJSON() {
        assertEquals("", dungeonList.toJSON(), "toJSON is unimplemented and should return an empty string.");
    }

    @Test
    @DisplayName("fromJSON should not throw an exception as it is unimplemented")
    void fromJSON() {
        assertDoesNotThrow(() -> dungeonList.fromJSON("{\"key\":\"value\"}"), "fromJSON is a no-op and should not throw.");
        assertEquals(2, dungeonList.getAll().size(), "fromJSON should not alter the list contents.");
    }

    @Test
    @DisplayName("clear should remove all dungeons from the list")
    void clear() {
        assertFalse(dungeonList.getAll().isEmpty());
        dungeonList.clear();
        assertTrue(dungeonList.getAll().isEmpty(), "List should be empty after clear().");
    }

    @Test
    @DisplayName("getAll should return a defensive copy of the dungeon list")
    void getAll() {
        List<Dungeon> allDungeons = dungeonList.getAll();
        assertEquals(2, allDungeons.size());

        // Modify the returned list
        allDungeons.clear();

        // The original list should be unaffected
        assertEquals(2, dungeonList.getAll().size(), "Modifying the list from getAll() should not affect the internal list.");
    }

    @Test
    @DisplayName("addDungeon should add a new dungeon but not duplicates or nulls")
    void addDungeon() {
        dungeonList.clear();
        assertEquals(0, dungeonList.getAll().size());

        dungeonList.addDungeon(dungeon1);
        assertEquals(1, dungeonList.getAll().size());

        // Try adding a duplicate
        dungeonList.addDungeon(dungeon1);
        assertEquals(1, dungeonList.getAll().size(), "Duplicate dungeons should not be added.");

        // Try adding null
        dungeonList.addDungeon(null);
        assertEquals(1, dungeonList.getAll().size(), "Null dungeons should not be added.");
    }
}