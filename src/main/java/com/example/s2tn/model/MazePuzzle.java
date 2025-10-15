package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;

/** Grid maze: S=start, G=goal, W=wall, 0/space=empty. */

/**
 * -----
 * commenting out so that code compiles, to let you know we have Maze.java, so a bit unsure about MazePuzzle.java is. 
 * if you could put the code in there that would be great!
 * ----- 
 */


// public class MazePuzzle extends Puzzle {

//     private List<String> gridRows = new ArrayList<>(); // e.g., ["S00W0","0W0G0"]
//     private int r = 0, c = 0;                          // current player row/col
//     private int rows = 0, cols = 0;

//     public MazePuzzle() { }

//     public MazePuzzle(List<String> grid) { setGrid(grid); }

//     /** Load/replace the maze layout. Rows must be equal length. */
//     public void setGrid(List<String> grid) {
//         gridRows = (grid == null) ? new ArrayList<>() : new ArrayList<>(grid);
//         rows = gridRows.size();
//         cols = (rows == 0) ? 0 : gridRows.get(0).length();

//         // Normalize: ensure all rows same length
//         for (int i = 0; i < rows; i++) {
//             if (gridRows.get(i).length() != cols) {
//                 throw new IllegalArgumentException("All maze rows must be the same length.");
//             }
//         }

//         // find start 'S' (default 0,0 if not found)
//         r = 0; c = 0;
//         for (int i = 0; i < rows; i++) {
//             int j = gridRows.get(i).indexOf('S');
//             if (j >= 0) { r = i; c = j; break; }
//         }
//     }

//     public List<String> getGrid() { return new ArrayList<>(gridRows); }
//     public int getRow() { return r; }
//     public int getCol() { return c; }

//     private boolean inBounds(int nr, int nc) { return nr >= 0 && nc >= 0 && nr < rows && nc < cols; }
//     private char cell(int rr, int cc) { return gridRows.get(rr).charAt(cc); }

//     /** Returns true if the move string is valid & the destination is not a wall. */
//     public boolean canMove(String dir) {
//         if (dir == null || rows == 0 || cols == 0) return false;
//         int nr = r, nc = c;
//         switch (dir.toUpperCase()) {
//             case "UP"    -> nr--;
//             case "DOWN"  -> nr++;
//             case "LEFT"  -> nc--;
//             case "RIGHT" -> nc++;
//             default -> { return false; }
//         }
//         return inBounds(nr, nc) && cell(nr, nc) != 'W';
//     }

//     /** Executes a move if valid; silently ignores invalid input. */
//     public void move(String dir) {
//         if (!canMove(dir)) return;
//         switch (dir.toUpperCase()) {
//             case "UP"    -> r--;
//             case "DOWN"  -> r++;
//             case "LEFT"  -> c--;
//             case "RIGHT" -> c++;
//         }
//     }

//     /** Solved when player stands on 'G'. */
//     public boolean isSolved() { return rows > 0 && cols > 0 && cell(r, c) == 'G'; }

//     @Override
//     public void enterInput(String input) { move(input); }

//     @Override
//     public void reset() {
//         // Re-locate 'S' as start
//         for (int i = 0; i < rows; i++) {
//             int j = gridRows.get(i).indexOf('S');
//             if (j >= 0) { r = i; c = j; return; }
//         }
//         r = 0; c = 0;
//     }
// }
