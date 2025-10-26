package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Singleton service that manages dungeons, rooms, and puzzles during gameplay.
 * Provides methods to register, start, and track dungeon progress.
 */
public final class DungeonService {

    private static final DungeonService INSTANCE = new DungeonService();

    /**
     * Returns the single instance of {@code DungeonService}.
     * 
     * @return the shared DungeonService instance
     */
    public static DungeonService getInstance() { return INSTANCE; }

    /**
     * Private constructor for singleton pattern.
     */
    private DungeonService() {
        // testing to see if if works properly
        // dungeons.add(new Dungeon("Dungeon One", 3, 2, Difficulty.EASY));
        // dungeons.add(new Dungeon("Dungeon Two", 2, 3, Difficulty.NORMAL));
    }

    /** Difficulty levels available for dungeons. */
    public enum Difficulty { EASY, NORMAL, HARD }

    /**
     * Simple internal timer for measuring elapsed time in a dungeon.
     */
    private static final class Timer {
        private long start;
        void start() { start = System.currentTimeMillis(); }
        long elapsed() { return start == 0L ? 0L : (System.currentTimeMillis() - start); }
        void reset() { start = 0L; }
    }

    /**
     * Represents an individual puzzle within a room.
     */
    private static final class Puzzle {
        @SuppressWarnings("unused")
        final UUID id = UUID.randomUUID();
        @SuppressWarnings("unused")
        final String type;
        boolean solved = false;
        Puzzle(String type) { this.type = type; }

        /**
         * Attempts to solve the puzzle.
         * 
         * @param attempt the player’s attempt
         * @return true if solved, false otherwise
         */
        boolean solve(String attempt) {
            if (!solved && attempt != null) { solved = true; return true; }
            return false;
        }
    }

    /**
     * Represents a room in a dungeon, containing puzzles.
     */
    private static final class Room {
        final UUID id = UUID.randomUUID();
        @SuppressWarnings("unused")
        final String name;
        final List<Puzzle> puzzles = new ArrayList<>();

        Room(String name, int puzzleCount) {
            this.name = name;
            for (int i = 0; i < puzzleCount; i++) {
                String t = switch (i % 5) {
                    case 0 -> "Code Puzzle";
                    case 1 -> "Maze";
                    case 2 -> "Word Scramble";
                    case 3 -> "Riddle";
                    default -> "Puzzle";
                };
                puzzles.add(new Puzzle(t));
            }
        }

        /** Returns true if all puzzles in this room are solved. */
        boolean isComplete() { for (Puzzle p : puzzles) if (!p.solved) return false; return true; }

        /** Returns the next unsolved puzzle, or null if all are solved. */
        Puzzle nextUnsolved() { for (Puzzle p : puzzles) if (!p.solved) return p; return null; }
    }

    /**
     * Tracks which rooms have been completed within a dungeon.
     */
    private static final class GameMap {
        private final HashMap<UUID, Boolean> completeByRoom = new HashMap<>();

        /** Marks a room as completed. */
        void markComplete(Room r) { completeByRoom.put(r.id, true); }

        /** Checks if a room has been marked complete. */
        @SuppressWarnings("unused")
        boolean isMarked(Room r) { return completeByRoom.getOrDefault(r.id, false); }
    }

    /**
     * Represents a full dungeon, consisting of rooms, puzzles, and a timer.
     */
    private static final class Dungeon {
        final UUID id = UUID.randomUUID();
        @SuppressWarnings("unused")
        final String name;
        final List<Room> rooms = new ArrayList<>();
        final GameMap map = new GameMap();
        final Timer timer = new Timer();
        @SuppressWarnings("unused")
        final Difficulty difficulty;
        int currentRoomIndex = 0;

        Dungeon(String name, int roomCount, int puzzlesPerRoom, Difficulty diff) {
            this.name = name;
            this.difficulty = diff;
            for (int i = 1; i <= roomCount; i++) {
                rooms.add(new Room(name + " - Room " + i, puzzlesPerRoom));
            }
        }

        /** Returns the current room in the dungeon. */
        Room currentRoom() {
            return (currentRoomIndex >= 0 && currentRoomIndex < rooms.size()) ? rooms.get(currentRoomIndex) : null;
        }

        /** Moves to the next room, returning true if successful. */
        boolean nextRoom() {
            if (currentRoomIndex + 1 < rooms.size()) { currentRoomIndex++; return true; }
            return false;
        }

        /** Returns true if all rooms in the dungeon are complete. */
        boolean isComplete() {
            for (Room r : rooms) if (!r.isComplete()) return false;
            return true;
        }

        /** Returns the dungeon’s difficulty level. */
        public Difficulty getDifficulty() {
            return difficulty;
        }
    }

    private final ArrayList<Dungeon> dungeons = new ArrayList<>();
    private Dungeon activeDungeon;

    /**
     * Returns the UUID of the currently active dungeon.
     * 
     * @return active dungeon ID, or null if none is active
     */
    public UUID getActiveDungeonId() {
        return activeDungeon == null ? null : activeDungeon.id;
    }

    /**
     * Returns the ID of the next dungeon in sequence.
     * 
     * @return next dungeon ID, or null if unavailable
     */
    public UUID getNextDungeonId() {
        if (dungeons.isEmpty()) return null;
        if (activeDungeon == null) return dungeons.size() > 1 ? dungeons.get(1).id : null;
        int idx = dungeons.indexOf(activeDungeon);
        if (idx >= 0 && idx + 1 < dungeons.size()) return dungeons.get(idx + 1).id;
        return null;
    }

    /**
     * Starts the specified dungeon or defaults to the first one if null.
     * Resets and starts the timer.
     * 
     * @param dungeonId ID of the dungeon to start
     * @return ID of the started dungeon, or null if not found
     */
    public UUID startDungeon(UUID dungeonId) {
        if (dungeons.isEmpty()) return null;
        if (dungeonId == null) {
            activeDungeon = dungeons.get(0);
        } else {
            activeDungeon = null;
            for (Dungeon d : dungeons) if (d.id.equals(dungeonId)) { activeDungeon = d; break; }
            if (activeDungeon == null) return null;
        }
        activeDungeon.currentRoomIndex = 0;
        activeDungeon.timer.reset();
        activeDungeon.timer.start();
        return activeDungeon.id;
    }

    /**
     * Enters the specified room or returns the current room if null is provided.
     * 
     * @param roomId room ID to enter
     * @return entered room ID, or null if not found
     */
    public UUID enterRoom(UUID roomId) {
        if (activeDungeon == null) return null;
        if (roomId == null) {
            Room r = activeDungeon.currentRoom();
            return r == null ? null : r.id;
        }
        for (int i = 0; i < activeDungeon.rooms.size(); i++) {
            if (activeDungeon.rooms.get(i).id.equals(roomId)) {
                activeDungeon.currentRoomIndex = i;
                return roomId;
            }
        }
        return null;
    }

    /**
     * Attempts to solve the next unsolved puzzle in the current room.
     * 
     * @param attempt player’s puzzle input
     * @return true if puzzle was solved, false otherwise
     */
    public boolean solveNextPuzzleInRoom(String attempt) {
        if (activeDungeon == null) return false;
        Room r = activeDungeon.currentRoom();
        if (r == null) return false;
        Puzzle p = r.nextUnsolved();
        if (p == null) { markRoomComplete(r.id); return false; }
        boolean solved = p.solve(attempt);
        if (solved && r.isComplete()) { markRoomComplete(r.id); }
        return solved;
    }

    /**
     * Moves to the next room in the active dungeon.
     * 
     * @return true if successfully moved to the next room
     */
    public boolean nextRoom() {
        if (activeDungeon == null) return false;
        return activeDungeon.nextRoom();
    }

    /**
     * Returns true if the currently active dungeon is complete.
     * 
     * @return true if complete, false otherwise
     */
    public boolean isDungeonComplete() {
        if (activeDungeon == null) return false;
        return activeDungeon.isComplete();
    }

    /**
     * Returns the elapsed time for the current dungeon in milliseconds.
     * 
     * @return elapsed time in ms
     */
    public long getElapsedTimeMillis() {
        if (activeDungeon == null) return 0L;
        return activeDungeon.timer.elapsed();
    }

    /**
     * Marks a specific room as complete within the active dungeon.
     * 
     * @param roomId ID of the room to mark complete
     */
    public void markRoomComplete(UUID roomId) {
        if (activeDungeon == null) return;
        for (Room r : activeDungeon.rooms) {
            if (r.id.equals(roomId)) {
                activeDungeon.map.markComplete(r);
                break;
            }
        }
    }

    /**
     * Returns a list of all dungeon IDs.
     * 
     * @return list of dungeon UUIDs
     */
    public List<UUID> listDungeonIds() {
        List<UUID> ids = new ArrayList<>();
        for (Dungeon d : dungeons) ids.add(d.id);
        return ids;
    }

    /**
     * Registers and adds a new dungeon to the service.
     * 
     * @param name dungeon name
     * @param roomCount number of rooms
     * @param puzzlesPerRoom puzzles per room
     * @param difficulty difficulty level
     * @return UUID of the newly registered dungeon
     */
    public UUID registerDungeon(String name, int roomCount, int puzzlesPerRoom, Difficulty difficulty) {
        Dungeon d = new Dungeon(name, roomCount, puzzlesPerRoom, difficulty == null ? Difficulty.EASY : difficulty);
        dungeons.add(d);
        return d.id;
    }

    /**
     * Resets the currently active dungeon.
     */
    public void resetActiveDungeon() {
        activeDungeon = null;
    }
}
