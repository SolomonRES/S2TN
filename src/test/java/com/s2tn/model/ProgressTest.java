package com.s2tn.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProgressTest {

    @Test
    void testSetAndGetUserName() {
        Progress progress = new Progress();
        String expectedUserName = "PlayerOne";
        progress.setUserName(expectedUserName);
        assertEquals(expectedUserName, progress.getUserName(), "UserName should match what was set.");
    }

    @Test
    void testSetAndGetDungeonID() {
        Progress progress = new Progress();
        UUID dungeonID = UUID.randomUUID();
        progress.setDungeonID(dungeonID);
        assertEquals(dungeonID, progress.getDungeonID(), "DungeonID should match what was set.");
    }

    @Test
    void testSetAndGetCurrentRoomID() {
        Progress progress = new Progress();
        UUID roomID = UUID.randomUUID();
        progress.setCurrentRoomID(roomID);
        assertEquals(roomID, progress.getCurrentRoomID(), "CurrentRoomID should match what was set.");
    }

    @Test
    void testSetAndGetPuzzleState() {
        Progress progress = new Progress();
        Map<String, PuzzleState> puzzles = new HashMap<>();
        // Assuming PuzzleState has a default constructor or a suitable way to create it
        PuzzleState dummyState1 = PuzzleState.INIT;
        PuzzleState dummyState2 = PuzzleState.IN_PROGRESS;

        puzzles.put("puzzle1", dummyState1);
        puzzles.put("puzzle2", dummyState2);

        progress.setPuzzleState(puzzles);

        assertEquals(puzzles, progress.getPuzzleState(), "Returned puzzle state map should be the same as the one set.");
        assertEquals(dummyState1, progress.getPuzzleState().get("puzzle1"), "PuzzleState for 'puzzle1' should match.");
        assertEquals(dummyState2, progress.getPuzzleState().get("puzzle2"), "PuzzleState for 'puzzle2' should match.");
    }

    @Test
    void testSetPuzzleStateWithNullCreatesEmptyMap() {
        Progress progress = new Progress();
        progress.setPuzzleState(null);
        assertNotNull(progress.getPuzzleState(), "PuzzleState map should not be null after setting null.");
        assertTrue(progress.getPuzzleState().isEmpty(), "PuzzleState map should be empty after setting null.");
    }

    @Test
    void testSetAndGetElapsedTime() {
        Progress progress = new Progress();
        long expectedElapsedTime = 12345L;
        progress.setElapsedTime(expectedElapsedTime);
        assertEquals(expectedElapsedTime, progress.getElapsedTime(), "ElapsedTime should match what was set.");
    }

    @Test
    void testSetAndGetSlot() {
        Progress progress = new Progress();
        String expectedSlot = "Slot1";
        progress.setSlot(expectedSlot);
        assertEquals(expectedSlot, progress.getSlot(), "Slot should match what was set.");
    }

    @Test
    void testDefaultConstructorInitialState() {
        Progress progress = new Progress();
        assertNull(progress.getUserName(), "UserName should be null initially.");
        assertNull(progress.getDungeonID(), "DungeonID should be null initially.");
        assertNull(progress.getCurrentRoomID(), "CurrentRoomID should be null initially.");
        assertNotNull(progress.getPuzzleState(), "PuzzleState map should not be null initially.");
        assertTrue(progress.getPuzzleState().isEmpty(), "PuzzleState map should be empty initially.");
        assertEquals(0L, progress.getElapsedTime(), "ElapsedTime should be 0L initially.");
        assertNull(progress.getSlot(), "Slot should be null initially.");
    }
}