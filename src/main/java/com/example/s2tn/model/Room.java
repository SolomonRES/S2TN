package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.UUID;

public class Room {
    private UUID roomID;
    private ArrayList<Puzzle> puzzles;
    private ArrayList<Hint> hints;
    private ArrayList<Room> exits;
    private ArrayList<Room> lockedExits;

    public ArrayList<Puzzle> getPuzzles(){
        return puzzles;
    }

    public ArrayList<Hint> getHints() {
        return hints;
    }

    public ArrayList<Room> getExits() {
        return exits;
    }

    public ArrayList<Room> getLockedExits() {
        return lockedExits;
    }

    public boolean addPuzzle(Puzzle puzzle){
        boolean isPresent = false;
        for(Puzzle exists : this.puzzles){
            if(exists == puzzle){
                isPresent = true;
            }
        }
        if(!isPresent){
            puzzles.add(puzzle);
            return true;
        }
        return false;
    }

    public boolean addHint(Hint hint){
        boolean isPresent = false;
        for(Hint exists : this.hints){
            if(exists == hint){
                isPresent = true;
            }
        }
        if(!isPresent){
            hints.add(hint);
            return true;
        }
        return false;
    }
}
