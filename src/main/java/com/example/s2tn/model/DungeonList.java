package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;

public class DungeonList {
    private static DungeonList instance;
    private List<Dungeon> dungeons;

    private DungeonList() {
        dungeons = new ArrayList<>();
    }

    public static DungeonList getInstance() {
        if (instance == null) {
            instance = new DungeonList();
        }
        return instance;
    }

    public Dungeon getDungeon(String id) {
        return null;
    }

    public void addDungeon(Dungeon d) {
    }

    public void removeDungeon(String id) {
    }

    public String toJSON() {
        return "";
    }

    public void fromJSON(String json) {
    }
}
