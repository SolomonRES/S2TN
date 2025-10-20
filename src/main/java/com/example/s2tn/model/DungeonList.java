package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DungeonList {
    private static DungeonList instance;
    private static List<Dungeon> dungeons;

    private DungeonList() {
        this.dungeons = new ArrayList<>();
    }

    public static DungeonList getInstance() {
        if (instance == null) {
            instance = new DungeonList();
        }
        return instance;
    }

    public java.util.List<Dungeon> getAll() {
        return new java.util.ArrayList<>(dungeons);
    }

    public void replaceAll(java.util.List<Dungeon> newList) {
        dungeons.clear();
        if (newList != null) {
            dungeons.addAll(newList);
        }
    }

    public List<UUID> getAllIds() {
        List<UUID> ids = new ArrayList<>();
        for (Dungeon d : dungeons) {
            ids.add(d.getUUID());
        }
        return ids;
    }

    public Dungeon getDungeon(UUID id) {
        if (id == null) return null;
        for (Dungeon d : dungeons) {
            if (id.equals(d.getUUID())) return d;
        }
        return null;
    }

    public Dungeon getDungeon(String id) {
        try {
            return getDungeon(UUID.fromString(id));
        } catch (Exception e) {
            return null;
        }
    }

    public void addDungeon(Dungeon d) {
        if (d != null && !dungeons.contains(d)) {
            dungeons.add(d);
        }
    }

    public boolean removeDungeon(UUID id) {
        if (id == null) return false;
        return dungeons.removeIf(d -> id.equals(d.getUUID()));
    }

    public String toJSON() {
        return "";
    }

    public void fromJSON(String json) {
    }
}
