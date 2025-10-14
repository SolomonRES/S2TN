package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.UUID;

public class Room {
    private UUID roomID;
    private ArrayList<Puzzle> puzzles;
    private ArrayList<Hint> hints;
    private ArrayList<Exit> exits;
    private ArrayList<Object> items;
    public Room(ArrayList<Puzzle> puzzles,ArrayList<Hint> hints,ArrayList<Exit> exits){
        this.roomID = UUID.randomUUID();
        this.puzzles = puzzles;
        this.hints = hints;
        this.exits = exits;
    }

    public ArrayList<Puzzle> getPuzzles(){
        return puzzles;
    }

    public ArrayList<Hint> getHints() {
        return hints;
    }

    public ArrayList<Exit> getExits() {
        return exits;
    }
}
