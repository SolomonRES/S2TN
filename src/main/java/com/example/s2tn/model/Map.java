package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Map {
    private ArrayList<Room> exploredRooms;
    private ArrayList<Room> completedRooms;
    private Dungeon dungeon;
    public Map(Dungeon dungeon){
        this.exploredRooms = new ArrayList<>();
        this.completedRooms = new ArrayList<>();
        this.dungeon = dungeon;
    }
    public void markExplored(Room room){
        if(!exploredRooms.contains(room)){
            exploredRooms.add(room);
        }
    }
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
    public Dungeon loadMap(){
        //Will load details of dungeon into a facade
        // I want to load rooms via the exits in a room
        // have a arraylist of size 10 where
        // 1 = north, 2 = east, 3 = south, 4 = west,
        // 5 = north-west, 6 = north-east, 7 = south-east, 8 = south-west,
        // 9 = up, 10 = down though I think the last two will likely go unused
        return dungeon;
    }

    public ArrayList<Room> getCompletedRooms() {
        return completedRooms;
    }
}
