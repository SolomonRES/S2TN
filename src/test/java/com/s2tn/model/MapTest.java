package com.s2tn.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MapTest {

    private Map map;
    private Dungeon dungeon;
    private Room room1;
    private Room room2;
    private Puzzle puzzle1;
    private Puzzle puzzle2;

    @BeforeEach
    void setUp() {
        // Using CodePuzzle as a concrete implementation of the abstract Puzzle class
        puzzle1 = new CodePuzzle();
        puzzle2 = new CodePuzzle();

        ArrayList<Puzzle> room1Puzzles = new ArrayList<>();
        room1Puzzles.add(puzzle1);
        room1 = new Room(room1Puzzles, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        ArrayList<Puzzle> room2Puzzles = new ArrayList<>();
        room2Puzzles.add(puzzle2);
        room2 = new Room(room2Puzzles, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        ArrayList<Room> dungeonRooms = new ArrayList<>();
        dungeonRooms.add(room1);
        dungeonRooms.add(room2);

        dungeon = new Dungeon("Test Dungeon", dungeonRooms, 600000, Difficulty.NORMAL, room1);
        map = new Map(dungeon);
    }

    @Test
    @DisplayName("Constructor should initialize map with the correct dungeon and empty room lists")
    void testConstructor() {
        assertNotNull(map.getExploredRooms(), "Explored rooms list should be initialized.");
        assertTrue(map.getExploredRooms().isEmpty(), "Explored rooms list should be empty initially.");
        assertNotNull(map.getCompletedRooms(), "Completed rooms list should be initialized.");
        assertTrue(map.getCompletedRooms().isEmpty(), "Completed rooms list should be empty initially.");
        assertSame(dungeon, map.loadMap(), "loadMap() should return the dungeon instance provided in the constructor.");
    }

    @Test
    @DisplayName("markExplored should add a room to the explored list only once")
    void markExplored() {
        map.markExplored(room1);
        assertEquals(1, map.getExploredRooms().size());
        assertTrue(map.getExploredRooms().contains(room1));

        // Mark the same room again, should not be added
        map.markExplored(room1);
        assertEquals(1, map.getExploredRooms().size(), "Duplicate rooms should not be added to explored list.");

        // Mark a different room
        map.markExplored(room2);
        assertEquals(2, map.getExploredRooms().size());
        assertTrue(map.getExploredRooms().contains(room2));
    }

    @Test
    @DisplayName("markExplored should not add a null room")
    void markExplored_shouldIgnoreNull() {
        map.markExplored(null);
        assertTrue(map.getExploredRooms().isEmpty(), "Null rooms should not be added to the explored list.");
    }

    @Test
    @DisplayName("markComplete should add a room only if all its puzzles are solved")
    void markComplete() {
        // Initially, puzzle is not solved
        assertEquals(PuzzleState.INIT, puzzle1.getState());
        map.markComplete(room1);
        assertTrue(map.getCompletedRooms().isEmpty(), "Room should not be marked complete if puzzles are not solved.");

        // Solve the puzzle
        puzzle1.setState(PuzzleState.SOLVED);
        map.markComplete(room1);
        assertEquals(1, map.getCompletedRooms().size(), "Room should be marked complete once all puzzles are solved.");
        assertTrue(map.getCompletedRooms().contains(room1));

        // Mark the same room again, should not be added
        map.markComplete(room1);
        assertEquals(1, map.getCompletedRooms().size(), "Duplicate rooms should not be added to completed list.");
    }

    @Test
    @DisplayName("getCompletedRooms should return a defensive copy")
    void getCompletedRooms_shouldBeDefensiveCopy() {
        puzzle1.setState(PuzzleState.SOLVED);
        map.markComplete(room1);
        assertEquals(1, map.getCompletedRooms().size());

        // Modify the returned list
        map.getCompletedRooms().clear();

        // The original list in the map should be unaffected
        assertEquals(1, map.getCompletedRooms().size(), "Modifying the returned list should not affect the map's internal state.");
    }
}