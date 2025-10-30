package com.s2tn.model;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoomTest {

    private Room room;
    private Puzzle puzzle1;
    private Hint hint1;
    private Room exitRoom;
    private Room lockedExitRoom;

    @BeforeEach
    void setUp() {
        puzzle1 = new CodePuzzle();
        hint1 = new Hint(1, "A hint", 0);
        exitRoom = new Room("Exit Room");
        lockedExitRoom = new Room("Locked Room");

        ArrayList<Puzzle> puzzles = new ArrayList<>();
        puzzles.add(puzzle1);

        ArrayList<Hint> hints = new ArrayList<>();
        hints.add(hint1);

        ArrayList<Room> exits = new ArrayList<>();
        exits.add(exitRoom);

        ArrayList<Room> lockedExits = new ArrayList<>();
        lockedExits.add(lockedExitRoom);

        room = new Room(puzzles, hints, exits, lockedExits);
    }

    @Test
    @DisplayName("Main constructor should initialize all properties correctly")
    void testMainConstructor() {
        assertNotNull(room.getRoomID());
        assertEquals(1, room.getPuzzles().size());
        assertTrue(room.getPuzzles().contains(puzzle1));
        assertEquals(1, room.getHints().size());
        assertTrue(room.getHints().contains(hint1));
        assertSame(exitRoom, room.getExits()[0]);
        assertSame(lockedExitRoom, room.getLockedExits()[0]);
    }

    @Test
    @DisplayName("Convenience constructor should create an empty room")
    void testConvenienceConstructor() {
        Room emptyRoom = new Room("Empty Room");
        assertNotNull(emptyRoom.getRoomID());
        assertTrue(emptyRoom.getPuzzles().isEmpty());
        assertTrue(emptyRoom.getHints().isEmpty());
        assertNull(emptyRoom.getExits()[0]);
        assertNull(emptyRoom.getLockedExits()[0]);
    }

    @Test
    @DisplayName("Constructor should handle null lists gracefully")
    void testConstructorWithNulls() {
        Room nullRoom = new Room(null, null, null, null);
        assertNotNull(nullRoom.getPuzzles());
        assertTrue(nullRoom.getPuzzles().isEmpty());
        assertNotNull(nullRoom.getHints());
        assertTrue(nullRoom.getHints().isEmpty());
        assertNotNull(nullRoom.getExits());
        assertNotNull(nullRoom.getLockedExits());
    }

    @Test
    @DisplayName("addPuzzle should add a new puzzle and prevent duplicates by identity")
    void addPuzzle() {
        Puzzle puzzle2 = new CodePuzzle();
        assertTrue(room.addPuzzle(puzzle2));
        assertEquals(2, room.getPuzzles().size());

        // Try adding the same instance again
        assertFalse(room.addPuzzle(puzzle2), "Should not add the same puzzle instance again.");
        assertEquals(2, room.getPuzzles().size());

        // Try adding null
        assertFalse(room.addPuzzle(null), "Should not add a null puzzle.");
    }

    @Test
    @DisplayName("addHint should add a new hint and prevent duplicates by identity")
    void addHint() {
        Hint hint2 = new Hint(2, "Another hint", 0);
        assertTrue(room.addHint(hint2));
        assertEquals(2, room.getHints().size());

        // Try adding the same instance again
        assertFalse(room.addHint(hint2), "Should not add the same hint instance again.");
        assertEquals(2, room.getHints().size());

        // Try adding null
        assertFalse(room.addHint(null), "Should not add a null hint.");
    }

    @Test
    @DisplayName("unlock should remove a room from lockedExits")
    void unlock() {
        // Verify the locked exit is present
        assertSame(lockedExitRoom, room.getLockedExits()[0]);

        // Unlock it
        room.unlock(lockedExitRoom);
        assertNull(room.getLockedExits()[0], "The unlocked room should be null in the lockedExits array.");

        // Try to unlock a room that isn't locked or is null
        Room anotherRoom = new Room("Another Room");
        room.unlock(anotherRoom); // Should do nothing
        room.unlock(null); // Should do nothing
        assertNull(room.getLockedExits()[0]); // State should remain unchanged
    }

    @Test
    @DisplayName("getPuzzles should return a mutable list (demonstrates encapsulation issue)")
    void getPuzzles_returnsMutableList() {
        assertEquals(1, room.getPuzzles().size());

        // Modify the returned list
        room.getPuzzles().clear();

        // The original list in the room should be affected
        assertEquals(0, room.getPuzzles().size(), "Modifying the returned list should affect the room's internal list.");
    }

    @Test
    @DisplayName("getHints should return a mutable list (demonstrates encapsulation issue)")
    void getHints_returnsMutableList() {
        assertEquals(1, room.getHints().size());

        // Modify the returned list
        room.getHints().clear();

        // The original list in the room should be affected
        assertEquals(0, room.getHints().size(), "Modifying the returned list should affect the room's internal list.");
    }
}