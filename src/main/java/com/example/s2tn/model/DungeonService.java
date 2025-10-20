package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class DungeonService {

    private static final DungeonService INSTANCE = new DungeonService();
    public static DungeonService getInstance() { return INSTANCE; }
    private DungeonService() {
        // testing to see if if works properly
        dungeons.add(new Dungeon("Dungeon One", 3, 2, Difficulty.EASY));
        dungeons.add(new Dungeon("Dungeon Two", 2, 3, Difficulty.NORMAL));
    }

    public enum Difficulty { EASY, NORMAL, HARD }

    private static final class Timer {
        private long start;
        void start() { start = System.currentTimeMillis(); }
        long elapsed() { return start == 0L ? 0L : (System.currentTimeMillis() - start); }
        void reset() { start = 0L; }
    }

    private static final class Puzzle {
        final UUID id = UUID.randomUUID();
        final String type;
        boolean solved = false;
        Puzzle(String type) { this.type = type; }
        boolean solve(String attempt) {
            if (!solved && attempt != null) { solved = true; return true; }
            return false;
        }
    }

    private static final class Room {
        final UUID id = UUID.randomUUID();
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
        boolean isComplete() { for (Puzzle p : puzzles) if (!p.solved) return false; return true; }
        Puzzle nextUnsolved() { for (Puzzle p : puzzles) if (!p.solved) return p; return null; }
    }

    private static final class GameMap {
        private final HashMap<UUID, Boolean> completeByRoom = new HashMap<>();
        void markComplete(Room r) { completeByRoom.put(r.id, true); }
        boolean isMarked(Room r) { return completeByRoom.getOrDefault(r.id, false); }
    }

    private static final class Dungeon {
        final UUID id = UUID.randomUUID();
        final String name;
        final List<Room> rooms = new ArrayList<>();
        final GameMap map = new GameMap();
        final Timer timer = new Timer();
        final Difficulty difficulty;
        int currentRoomIndex = 0;
        Dungeon(String name, int roomCount, int puzzlesPerRoom, Difficulty diff) {
            this.name = name;
            this.difficulty = diff;
            for (int i = 1; i <= roomCount; i++) {
                rooms.add(new Room(name + " - Room " + i, puzzlesPerRoom));
            }
        }
        Room currentRoom() {
            return (currentRoomIndex >= 0 && currentRoomIndex < rooms.size()) ? rooms.get(currentRoomIndex) : null;
        }
        boolean nextRoom() {
            if (currentRoomIndex + 1 < rooms.size()) { currentRoomIndex++; return true; }
            return false;
        }
        boolean isComplete() {
            for (Room r : rooms) if (!r.isComplete()) return false;
            return true;
        }
    }

    private final ArrayList<Dungeon> dungeons = new ArrayList<>();
    private Dungeon activeDungeon;

    public UUID getActiveDungeonId() {
        return activeDungeon == null ? null : activeDungeon.id;
    }

    public UUID getNextDungeonId() {
        if (dungeons.isEmpty()) return null;
        if (activeDungeon == null) return dungeons.size() > 1 ? dungeons.get(1).id : null;
        int idx = dungeons.indexOf(activeDungeon);
        if (idx >= 0 && idx + 1 < dungeons.size()) return dungeons.get(idx + 1).id;
        return null;
    }

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

    public boolean nextRoom() {
        if (activeDungeon == null) return false;
        return activeDungeon.nextRoom();
    }

    public boolean isDungeonComplete() {
        if (activeDungeon == null) return false;
        return activeDungeon.isComplete();
    }

    public long getElapsedTimeMillis() {
        if (activeDungeon == null) return 0L;
        return activeDungeon.timer.elapsed();
    }

    public void markRoomComplete(UUID roomId) {
        if (activeDungeon == null) return;
        for (Room r : activeDungeon.rooms) {
            if (r.id.equals(roomId)) {
                activeDungeon.map.markComplete(r);
                break;
            }
        }
    }

    public List<UUID> listDungeonIds() {
        List<UUID> ids = new ArrayList<>();
        for (Dungeon d : dungeons) ids.add(d.id);
        return ids;
    }

    public UUID registerDungeon(String name, int roomCount, int puzzlesPerRoom, Difficulty difficulty) {
        Dungeon d = new Dungeon(name, roomCount, puzzlesPerRoom, difficulty == null ? Difficulty.EASY : difficulty);
        dungeons.add(d);
        return d.id;
    }

    public void resetActiveDungeon() {
        activeDungeon = null;
    }
}
