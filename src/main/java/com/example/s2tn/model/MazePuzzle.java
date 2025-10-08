package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;

/** Grid maze puzzle. Legend: S=start, G=goal, W=wall, 0/space=empty. */
public class MazePuzzle extends Puzzle {

    private List<String> gridRows = new ArrayList<>(); // e.g., ["S00W0","0W0G0"]
    private int r = 0, c = 0;                          // current player row/col
    private int rows = 0, cols = 0;

    public MazePuzzle() { }

    public MazePuzzle(List<String> grid) {
        setGrid(grid);
    }

    public void setGrid(List<String> grid) {
        gridRows = (grid == null) ? new ArrayList<>() : new ArrayList<>(grid);
        rows = gridRows.size();
        cols = rows == 0 ? 0 : gridRows.get(0).length();

        // find start 'S'
        for (int i = 0; i < rows; i++) {
            int j = gridRows.get(i).indexOf('S');
            if (j >= 0) { r = i; c = j; break; }
        }
    }

    public List<String> getGrid() { return new ArrayList<>(gridRows); }
    public int getRow() { return r; }
    public int getCol() { return c; }

    private boolean inBounds(int nr, int nc) {
        return nr >= 0 && nc >= 0 && nr < rows && nc < cols;
    }
    private char cell(int rr, int cc) { return gridRows.get(rr).charAt(cc); }

    /** Move with "UP", "DOWN", "LEFT", "RIGHT". Ignores invalids & walls. */
    public void move(String dir) {
        if (dir == null) return;
        int nr = r, nc = c;
        switch (dir.toUpperCase()) {
            case "UP"    -> nr--;
            case "DOWN"  -> nr++;
            case "LEFT"  -> nc--;
            case "RIGHT" -> nc++;
            default -> { return; }
        }
        if (inBounds(nr, nc) && cell(nr, nc) != 'W') { r = nr; c = nc; }
    }

    /** Solved when player stands on 'G'. */
    public boolean isSolved() {
        return rows > 0 && cols > 0 && cell(r, c) == 'G';
    }

    @Override
    public void enterInput(String input) {
        // allow UI/Facade to send moves as simple strings
        move(input);
    }
}
