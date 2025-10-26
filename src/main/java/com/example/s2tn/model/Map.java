package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Represents a map for tracking explored and completed rooms within a dungeon.
 */
public class Map {
    private ArrayList<Room> exploredRooms;
    private ArrayList<Room> completedRooms;
    private Dungeon dungeon;

    /** Creates a map associated with a specific dungeon. */
    public Map(Dungeon dungeon){
        this.exploredRooms = new ArrayList<>();
        this.completedRooms = new ArrayList<>();
        this.dungeon = dungeon;
    }

    /** Marks a room as explored if it has not already been visited. */
    public void markExplored(Room room){
        if(!exploredRooms.contains(room)){
            exploredRooms.add(room);
        }
    }

    /**
     * Marks a room as complete if all its puzzles are solved.
     *
     * @param room the room to mark as complete
     */
    public void markComplete(Room room){
        boolean complete = true;
        for(Puzzle isSolved : room.getPuzzles()){
            if (isSolved.getState() != PuzzleState.SOLVED) {
                complete = false;
                break;
            }
        }
        if(complete){
            completedRooms.add(room);
        }
    }

    /**
     * Loads and returns the dungeon associated with this map.
     * This may later include logic for room connections via exits.
     *
     * @return the associated dungeon
     */
    public Dungeon loadMap(){
        //Will load details of dungeon into a facade
        // I want to load rooms via the exits in a room
        // have a arraylist of size 10 where
        // 1 = north, 2 = east, 3 = south, 4 = west,
        // 5 = north-west, 6 = north-east, 7 = south-east, 8 = south-west,
        // 9 = up, 10 = down though I think the last two will likely go unused
        return dungeon;
    }

    /** Returns a list of all completed rooms. */
    public ArrayList<Room> getCompletedRooms() {
        return completedRooms;
    }
}
