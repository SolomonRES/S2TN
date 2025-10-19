package com.example.s2tn.model;

import java.util.*;

/**
 * Sliding puzzle where shapes move one grid cell at a time.
 * Positions are provided like "A:3,5" (id:x,y). Order does not matter.
 *
 * Notes:
 * - Uses a simple grid with bounds [0..width-1] × [0..height-1].
 * - Prevents overlapping two shapes in the same cell.
 * - checkAnswer() succeeds when current == target layout.
 * - getHint() nudges one shape toward its target using Manhattan distance.
 */
public class SlideShapePuzzle extends Puzzle {

    // --------- Types ---------
    private static final class Pos {
        int x, y;
        Pos(int x, int y){ this.x = x; this.y = y; }
        Pos copy(){ return new Pos(x, y); }
    }

    // --------- State ---------
    private final Map<String, Pos> current = new HashMap<>();
    private final Map<String, Pos> target  = new HashMap<>();

    private int width  = 10;  // default board size; tweak if your game uses different dims
    private int height = 10;
    private int movesMade = 0;

    // --------- Ctors ---------
    public SlideShapePuzzle() {}

    /** Example input: start=["A:0,0","B:1,0"], end=["A:2,0","B:1,2"] */
    public SlideShapePuzzle(List<String> startConfiguration, List<String> endConfiguration) {
        loadConfig(startConfiguration, current);
        loadConfig(endConfiguration,  target);
    }

    public SlideShapePuzzle(List<String> startConfiguration, List<String> endConfiguration, int width, int height) {
        this(startConfiguration, endConfiguration);
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    // --------- Helpers ---------
    private static void loadConfig(List<String> list, Map<String, Pos> into){
        if (list == null) return;
        for (String s : list) {
            // "A:10,20"
            String[] p = s.split(":");
            if (p.length != 2) continue;
            String id = p[0].trim();
            String[] xy = p[1].split(",");
            if (xy.length != 2) continue;
            int x = Integer.parseInt(xy[0].trim());
            int y = Integer.parseInt(xy[1].trim());
            into.put(id, new Pos(x, y));
        }
    }

    private boolean inBounds(int x, int y){
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    private boolean occupiedExcept(String ignoreId, int x, int y){
        for (Map.Entry<String, Pos> e : current.entrySet()){
            if (e.getKey().equals(ignoreId)) continue;
            Pos q = e.getValue();
            if (q.x == x && q.y == y) return true;
        }
        return false;
    }

    private static int sgn(int v){ return (v == 0) ? 0 : (v > 0 ? 1 : -1); }

    // --------- API required by UML / game ---------

    /** Optional visual; keep simple. */
    public void displayPuzzle() { /* UI handled elsewhere */ }

    /**
     * Slide a shape one step in the given direction.
     * @param shapeId  id like "A"
     * @param direction one of {"up","down","left","right"}
     * @return true if a legal move occurred
     */
    public boolean slide(String shapeId, String direction) {
        if (shapeId == null || direction == null) return false;
        Pos p = current.get(shapeId);
        if (p == null) return false;

        int dx = 0, dy = 0;
        switch (direction.toLowerCase(Locale.ROOT)) {
            case "up":    dy = -1; break;
            case "down":  dy =  1; break;
            case "left":  dx = -1; break;
            case "right": dx =  1; break;
            default: return false;
        }

        int nx = p.x + dx, ny = p.y + dy;
        if (!inBounds(nx, ny)) return false;
        if (occupiedExcept(shapeId, nx, ny)) return false;

        p.x = nx; p.y = ny;
        movesMade++;
        return true;
    }

    /**
     * Overload: slide multiple steps (Facade calls may pass steps).
     */
    public boolean slide(String shapeId, String direction, int steps){
        if (steps <= 0) return false;
        boolean moved = false;
        for (int i = 0; i < steps; i++){
            if (!slide(shapeId, direction)) break;
            moved = true;
        }
        return moved;
    }

    /** True iff all shapes match target positions exactly. */
    public boolean checkAnswer(String ignoredUserInput) {
        if (current.size() != target.size()) return false;
        for (String id : target.keySet()) {
            Pos t = target.get(id);
            Pos c = current.get(id);
            if (c == null || c.x != t.x || c.y != t.y) return false;
        }
        return true;
    }

    /**
     * Provide a minimal, safe hint:
     * Pick one shape not in place and suggest the axis that reduces Manhattan distance.
     */
    public String getHint() {
        for (String id : target.keySet()) {
            Pos t = target.get(id);
            Pos c = current.get(id);
            if (c == null) continue;
            if (c.x == t.x && c.y == t.y) continue;

            int dx = t.x - c.x, dy = t.y - c.y;
            String dir;
            if (Math.abs(dx) >= Math.abs(dy)) dir = (sgn(dx) > 0) ? "right" : "left";
            else                              dir = (sgn(dy) > 0) ? "down"  : "up";

            // If blocked or out-of-bounds, try the other axis.
            int tryX = c.x + (dir.equals("right") ? 1 : dir.equals("left") ? -1 : 0);
            int tryY = c.y + (dir.equals("down")  ? 1 : dir.equals("up")   ? -1 : 0);
            if (!inBounds(tryX, tryY) || occupiedExcept(id, tryX, tryY)) {
                dir = (Math.abs(dx) >= Math.abs(dy)) ? ((sgn(dy) > 0) ? "down" : "up")
                                                      : ((sgn(dx) > 0) ? "right" : "left");
            }
            return "Try moving " + id + " one step " + dir + ".";
        }
        return "Looks solved—submit!";
    }

    // --------- Accessors for tests / UI ---------
    public int getMovesMade() { return movesMade; }
    public Map<String, int[]> getCurrentPositions(){
        Map<String, int[]> out = new HashMap<>();
        for (Map.Entry<String, Pos> e : current.entrySet()){
            out.put(e.getKey(), new int[]{e.getValue().x, e.getValue().y});
        }
        return Collections.unmodifiableMap(out);
    }
    public void setBoardSize(int width, int height){
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }
    public void setStart(List<String> start){ current.clear(); loadConfig(start, current); }
    public void setTarget(List<String> end){ target.clear(); loadConfig(end, target); }
}
