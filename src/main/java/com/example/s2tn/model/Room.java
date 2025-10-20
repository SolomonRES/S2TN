package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.UUID;

public class Room {
    private UUID roomID;
    private ArrayList<Puzzle> puzzles;
    private ArrayList<Hint> hints;
    private Room[] exits;
    private Room[] lockedExits;

    public Room(ArrayList<Puzzle> puzzles, ArrayList<Hint> hints, ArrayList<Room> exits, ArrayList<Room> lockedExits){
        this.puzzles = puzzles;
        this.hints = hints;
        this.exits = new Room[10];
        this.lockedExits = new Room[10];
        for(int i = 0; i< this.exits.length; i++){
            if(i < exits.size()){
                this.exits[i] = exits.get(i);
            }
            if(i < lockedExits.size()){
                this.lockedExits[i] = lockedExits.get(i);
            }
        }
    }

    public ArrayList<Puzzle> getPuzzles(){
        return puzzles;
    }

    public ArrayList<Hint> getHints() {
        return hints;
    }

    public Room[] getExits() {
        return exits;
    }

    public Room[] getLockedExits() {
        return lockedExits;
    }

    public UUID getRoomID(){
        return roomID;
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
    public void unlock(Room locked){
        for(int i = 0; i < lockedExits.length; i++){
            if (locked == lockedExits[i]){
                lockedExits[i] = null;
            }
        }
    }
}
