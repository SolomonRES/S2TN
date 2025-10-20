package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.UUID;

public class Room {
    private UUID roomID;
    private ArrayList<Puzzle> puzzles;
    private ArrayList<Hint> hints;
    private Room[] exits;
    private Room[] lockedExits;

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

    public Room(String roomName) {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public ArrayList<Puzzle> getPuzzles() { return puzzles; }
    public ArrayList<Hint> getHints() { return hints; }
    public Room[] getExits() { return exits; }
    public Room[] getLockedExits() { return lockedExits; }
    public UUID getRoomID() { return roomID; }

    public boolean addPuzzle(Puzzle puzzle) {
        if (puzzle == null) return false;
        for (Puzzle exists : this.puzzles) {
            if (exists == puzzle) return false;
        }
        puzzles.add(puzzle);
        return true;
    }

    public boolean addHint(Hint hint) {
        if (hint == null) return false;
        for (Hint exists : this.hints) {
            if (exists == hint) return false;
        }
        hints.add(hint);
        return true;
    }

    public void unlock(Room locked) {
        for (int i = 0; i < lockedExits.length; i++) {
            if (locked == lockedExits[i]) {
                lockedExits[i] = null;
            }
        }
    }
}
