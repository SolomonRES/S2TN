package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Progress {

    private String userName;
    private String dungeonID;
    private String currentRoomID;
    private Map<String, String> puzzleState;
    private long elapsedTime;
    private String slot;

    public Progress() {
        this.userName = "";
        this.dungeonID = "";
        this.currentRoomID = "";
        this.puzzleState = new HashMap<>();
        this.elapsedTime = 0L;
        this.slot = "";
    }

    public void progress(String slot) {
    }

    public Progress load(String slot) {
        return new Progress();
    }

    public List<String> listSlots() {
        return new ArrayList<>();
    }

    public void delete(String slot) {
    }
}
