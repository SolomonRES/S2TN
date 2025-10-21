package com.example.s2tn.model;

import java.util.ArrayList;

public class GenerateFromCode {
    public static void main(String[] args){
        ArrayList<Puzzle> puzzles = new ArrayList<>();
        ArrayList<Hint> hints = new ArrayList<>();
        ArrayList<Exit> exits = new ArrayList<>();
        ArrayList<Object> items = new ArrayList<>();
        Room home = new Room(puzzles,hints,exits, items);
        ArrayList<Room> rooms =new ArrayList<>();
        rooms.add(home);
        Dungeon test = new Dungeon("Halls of Madness",rooms, 3600000, Difficulty.NORMAL, home);
    }
}
