package com.s2tn.model;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DungeonTest {

    private Dungeon dungeon;
    private Room startingRoom;
    private Room room2;
    private ArrayList<Room> rooms;

    @BeforeEach
    void setUp() {
        startingRoom = new Room("Entry Hall");
        room2 = new Room("Library");
        rooms = new ArrayList<>();
        rooms.add(startingRoom);
        rooms.add(room2);

        dungeon = new Dungeon("Test Dungeon", rooms, 300000, Difficulty.NORMAL, startingRoom);
    }

    @Test
    @DisplayName("Constructor should initialize all fields correctly")
    void testConstructor() {
        assertEquals("Test Dungeon", dungeon.getName());
        assertEquals(rooms, dungeon.getRooms());
        assertNotNull(dungeon.getTimer());
        assertEquals(Difficulty.NORMAL, dungeon.getDifficulty());
        assertEquals(startingRoom, dungeon.getStartingRoom());
        assertNull(dungeon.getCurrentRoom());
        assertNull(dungeon.getPreviousRoom());
        assertNotNull(dungeon.getUUID());
    }

    @Test
    @DisplayName("Constructor should handle null rooms list")
    void testConstructorWithNullRooms() {
        Dungeon d = new Dungeon("Empty Dungeon", null, 1000, Difficulty.EASY, null);
        assertNotNull(d.getRooms());
        assertTrue(d.getRooms().isEmpty());
    }

    @Test
    @DisplayName("Constructor should default null difficulty to NORMAL")
    void testConstructorWithNullDifficulty() {
        Dungeon d = new Dungeon("Default Dungeon", rooms, 1000, null, startingRoom);
        assertEquals(Difficulty.NORMAL, d.getDifficulty());
    }

    @Test
    @DisplayName("Getters and setters for name should work correctly")
    void testGetAndSetName() {
        dungeon.setName("Renamed Dungeon");
        assertEquals("Renamed Dungeon", dungeon.getName());
    }

    @Test
    @DisplayName("Getters and setters for rooms should work correctly")
    void testGetAndSetRooms() {
        ArrayList<Room> newRooms = new ArrayList<>();
        Room newRoom = new Room("Treasure Room");
        newRooms.add(newRoom);
        dungeon.setRooms(newRooms);
        assertEquals(newRooms, dungeon.getRooms());
        assertEquals(1, dungeon.getRooms().size());
        assertTrue(dungeon.getRooms().contains(newRoom));
    }

    @Test
    @DisplayName("Getters and setters for timer should work correctly")
    void testGetAndSetTimer() {
        Timer newTimer = new Timer();
        dungeon.setTimer(newTimer);
        assertSame(newTimer, dungeon.getTimer());
    }

    @Test
    @DisplayName("Getters and setters for difficulty should work correctly")
    void testGetAndSetDifficulty() {
        dungeon.setDifficulty(Difficulty.HARD);
        assertEquals(Difficulty.HARD, dungeon.getDifficulty());
    }

    @Test
    @DisplayName("getUUID should return a non-null UUID")
    void testGetUUID() {
        assertNotNull(dungeon.getUUID());
    }

    @Test
    @DisplayName("changeRoom should update current and previous rooms correctly")
    void testChangeRoom() {
        assertNull(dungeon.getCurrentRoom(), "Current room should be null initially.");
        assertNull(dungeon.getPreviousRoom(), "Previous room should be null initially.");

        // First move
        dungeon.changeRoom(startingRoom);
        assertEquals(startingRoom, dungeon.getCurrentRoom(), "Current room should be the starting room.");
        assertNull(dungeon.getPreviousRoom(), "Previous room should still be null after first move.");

        // Second move
        dungeon.changeRoom(room2);
        assertEquals(room2, dungeon.getCurrentRoom(), "Current room should be room2.");
        assertEquals(startingRoom, dungeon.getPreviousRoom(), "Previous room should now be the starting room.");
    }

    @Test
    @DisplayName("changeRoom should not change room if next room is null")
    void testChangeRoomToNull() {
        dungeon.changeRoom(startingRoom); // Set an initial room
        dungeon.changeRoom(null);
        assertEquals(startingRoom, dungeon.getCurrentRoom(), "Current room should not change when next is null.");
        assertNull(dungeon.getPreviousRoom(), "Previous room should not change when next is null.");
    }

    @Test
    @DisplayName("changeRoom should not change room if it's not in the dungeon")
    void testChangeRoomToNonexistentRoom() {
        Room outsideRoom = new Room("Outside");
        dungeon.changeRoom(startingRoom); // Set an initial room
        dungeon.changeRoom(outsideRoom);
        assertEquals(startingRoom, dungeon.getCurrentRoom(), "Current room should not change for a room not in the list.");
    }

    @Test
    @DisplayName("toString should return a non-empty string representation")
    void testToString() {
        String s = dungeon.toString();
        assertTrue(s.contains("Dungeon{"));
        assertTrue(s.contains("uuid=" + dungeon.getUUID()));
        assertTrue(s.contains("name='Test Dungeon'"));
        assertTrue(s.contains("rooms=2"));
        assertTrue(s.contains("difficulty=NORMAL"));
    }

    @Test
    @DisplayName("equals and hashCode should be based on UUID")
    void testEqualsAndHashCode() {
        Dungeon sameDungeonRef = dungeon;
        Dungeon sameIdDungeon = new Dungeon("Another Name", new ArrayList<>(), 1, Difficulty.HARD, null);
        // Manually setting UUID is not possible, so we can't test equality for different objects with same ID.
        // Instead, we test reflexivity, symmetry with null/other types, and inequality with different objects.

        Dungeon differentDungeon = new Dungeon("Test Dungeon", rooms, 300000, Difficulty.NORMAL, startingRoom);

        assertEquals(dungeon, sameDungeonRef, "An object must be equal to itself.");
        assertNotEquals(dungeon, differentDungeon, "Dungeons with different UUIDs should not be equal.");
        assertNotEquals(dungeon.hashCode(), differentDungeon.hashCode(), "Hashcodes for different dungeons should be different.");
        assertNotEquals(null, dungeon, "Dungeon should not be equal to null.");
        assertNotEquals(dungeon, new Object(), "Dungeon should not be equal to an object of a different type.");
    }
}