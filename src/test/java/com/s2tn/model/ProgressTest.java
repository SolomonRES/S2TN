package com.s2tn.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProgressTest {
    @Test
    void testUserNameGetterSetter() {
        Progress progress = new Progress();
        progress.setUserName("Playerone");
        assertEquals("PlayerOne", progress.getUserName());
    }

    @Test
    void testDugeonIDGetterSetter() {
        Progress progress = new Progress();
        UUID dungeonID = UUID.randomUUID();
        progress.setDungeonID(dungeonID);
        assertEquals(dungeonID, progress.getDungeonID());
    }

    @Test
    void testCurrentRoomIDGetterSetter() {
        Progress progress = new Progress();
        UUID roomID = UUID.randomUUID();
        progress.setCurrentRoomID(roomID);
        assertEquals(roomID, progress.getCurrentRoomID());
    }

    @Test
    void testPuzzleStateGetterSetter() {
        Progress progress = new Progress();
        Map<String, PuzzleState> puzzles = new HashMap<>();
        PuzzleState dummyState = new PuzzleState(); // assuming default constructor exists
        puzzles.put("puzzle1", dummyState);

        progress.setPuzzleState(puzzles);
        assertEquals(puzzles, progress.getPuzzleState());
        assertEquals(dummyState, progress.getPuzzleState().get("puzzle1"));
    }

    @Test
    void testSetPuzzleStateWithNull() {
        Progress progress = new Progress();
        progress.setPuzzleState(null);
        assertNotNull(progress.getPuzzleState(), "PuzzleState map should not be null");
        assertTrue(progress.getPuzzleState().isEmpty(), "PuzzleState map should be empty");
    }

    @Test
    void testElapsedTimeGetterSetter() {
        Progress progress = new Progress();
        progress.setElapsedTime(12345L);
        assertEquals(12345L, progress.getElapsedTime());
    }

    @Test
    void testSlotGetterSetter() {
        Progress progress = new Progress();
        progress.setSlot("Slot1");
        assertEquals("Slot1", progress.getSlot());
    }
    
}
