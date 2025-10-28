package com.s2tn.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a room in a dungeon that may contain puzzles, hints, and exits to other rooms.
 */
public class Room {
    private UUID roomID;
    private ArrayList<Puzzle> puzzles;
    private ArrayList<Hint> hints;
    private Room[] exits;
    private Room[] lockedExits;

    /** 
     * Creates a room with puzzles, hints, and exits.
     *
     * @param puzzles list of puzzles in the room
     * @param hints list of hints available in the room
     * @param exits list of connected rooms (open exits)
     * @param lockedExits list of rooms behind locked exits
     */
    public Room(ArrayList<Puzzle> puzzles, ArrayList<Hint> hints, ArrayList<Room> exits, ArrayList<Room> lockedExits) {
        this.roomID = UUID.randomUUID();
        this.puzzles = (puzzles != null) ? puzzles : new ArrayList<>();
        this.hints = (hints != null) ? hints : new ArrayList<>();
        this.exits = new Room[10];
        this.lockedExits = new Room[10];

        ArrayList<Room> ex = (exits != null) ? exits : new ArrayList<>();
        ArrayList<Room> lex = (lockedExits != null) ? lockedExits : new ArrayList<>();

        for (int i = 0; i < this.exits.length; i++) {
            if (i < ex.size()) this.exits[i] = ex.get(i);
            if (i < lex.size()) this.lockedExits[i] = lex.get(i);
        }
    }

    /** Creates an empty room with the given name. */
    public Room(String roomName) {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    /** Returns the list of puzzles in the room. */
    public ArrayList<Puzzle> getPuzzles() { return puzzles; }

    /** Returns the list of hints available in the room. */
    public ArrayList<Hint> getHints() { return hints; }

    /** Returns an array of open exits from this room. */
    public Room[] getExits() { return exits; }

    /** Returns an array of locked exits from this room. */
    public Room[] getLockedExits() { return lockedExits; }

    /** Returns the unique ID for this room. */
    public UUID getRoomID() { return roomID; }

    /**
     * Adds a puzzle to the room if it does not already exist.
     *
     * @param puzzle the puzzle to add
     * @return true if the puzzle was added, false otherwise
     */
    public boolean addPuzzle(Puzzle puzzle) {
        if (puzzle == null) return false;
        for (Puzzle exists : this.puzzles) {
            if (exists == puzzle) return false;
        }
        puzzles.add(puzzle);
        return true;
    }

    /**
     * Adds a hint to the room if it does not already exist.
     *
     * @param hint the hint to add
     * @return true if the hint was added, false otherwise
     */
    public boolean addHint(Hint hint) {
        if (hint == null) return false;
        for (Hint exists : this.hints) {
            if (exists == hint) return false;
        }
        hints.add(hint);
        return true;
    }

    /**
     * Unlocks a previously locked exit, allowing access to the connected room.
     *
     * @param locked the room to unlock
     */
    public void unlock(Room locked) {
        for (int i = 0; i < lockedExits.length; i++) {
            if (locked == lockedExits[i]) {
                lockedExits[i] = null;
            }
        }
    }
}
