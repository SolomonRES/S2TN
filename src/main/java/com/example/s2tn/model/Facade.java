package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Simplified API for user auth, dungeon/room navigation, puzzles, inventory, and timer control.
 * Wraps lower-level services and models to provide a single entry point for the UI/driver.
 */
public class Facade {

    private Account user;
    private Dungeon dungeon;

    private final Set<String> inventory = new HashSet<>();

    /** Registers a new user with username and password. */
    public boolean register(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null) return false;
        Account a = new Account();
        a.setUserName(userName);
        a.setPassword(password);
        return new UserService().addUser(a);
    }

    /** Attempts to log in a user by username/password and sets it as current user. */
    public boolean login(String userName, String password) {
        Account found = new UserService().getByUserName(userName);
        if (found == null) return false;
        if (!Objects.equals(password, found.getPassword())) return false;
        this.user = found;
        return true;
    }

    /** Logs out the current user. */
    public void logout() { this.user = null; }

    /** Returns the currently logged-in user, or null if none. */
    public Account getCurrentUser() { return user; }

    /** Returns a list of the top N players from the leaderboard. */
    public List<Account> getTopPlayers(int n) {
    return new Leaderboard().getTopPlayers(n);
    }
    
    // Dungeons 
    private static volatile boolean dungeonsLoaded = false;

    /** Lists all available dungeons, loading them once on first access. */
    public List<Dungeon> listDungeons() {
        ensureDungeonsLoadedOnce();
        return DungeonList.getInstance().getAll();
    }

    /** Starts (selects) a dungeon by id; if id is null, selects the first available. */
    public boolean startDungeon(UUID id) {
        ensureDungeonsLoadedOnce();
        List<Dungeon> ds = DungeonList.getInstance().getAll();
        if (ds == null || ds.isEmpty()) return false;
        Dungeon pick = (id == null) ? ds.get(0)
                : ds.stream().filter(d -> id.equals(d.getUUID())).findFirst().orElse(null);
        if (pick == null) return false;
        dungeon = pick;
        return true;
    }

    /** Enters the selected dungeon, starting its timer and moving to the starting room if needed. */
    public boolean enterDungeon() {
        if (dungeon == null) return false;
        if (dungeon.getTimer() != null) dungeon.getTimer().start();
        if (dungeon.getCurrentRoom() == null) {
            Room start = dungeon.getStartingRoom();
            if (start != null) dungeon.changeRoom(start);
        }
        return true;
    }

    /** Exits the current dungeon. */
    public void exitDungeon() { dungeon = null; }

    /** Sets the dungeon difficulty from a string level (easy/normal/hard). */
    public void chooseDifficulty(String level) {
        if (dungeon == null || level == null) return;
        switch (level.toLowerCase()) {
            case "easy"   -> dungeon.setDifficulty(Difficulty.EASY);
            case "hard"   -> dungeon.setDifficulty(Difficulty.HARD);
            default       -> dungeon.setDifficulty(Difficulty.NORMAL);
        }
    }

    // Rooms 

    /** Returns the list of rooms for the current dungeon, or an empty list if none. */
    public List<Room> viewRooms() {
        return dungeon == null ? Collections.emptyList() : dungeon.getRooms();
    }

    /** Returns the UUID of the current room (or starting room), or null if not available. */
    public UUID getCurrentRoomId() {
        if (dungeon == null) return null;
        Room cur = dungeon.getCurrentRoom() != null ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
        return cur == null ? null : cur.getRoomID();
    }

    /** Enters a room by its UUID within the current dungeon. */
    public boolean enterRoom(UUID roomId) {
        if (dungeon == null || roomId == null) return false;
        for (Room r : dungeon.getRooms()) {
            if (roomId.equals(r.getRoomID())) {
                dungeon.changeRoom(r);
                return true;
            }
        }
        return false;
    }

    /** Moves to the next room in sequence; wraps to the first if at the end. */
    public boolean nextRoom() {
        if (dungeon == null) return false;
        List<Room> rooms = dungeon.getRooms();
        if (rooms == null || rooms.isEmpty()) return false;
        Room cur = (dungeon.getCurrentRoom() != null) ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
        if (cur == null) { dungeon.changeRoom(rooms.get(0)); return true; }
        int idx = rooms.indexOf(cur);
        int nxt = (idx < 0 || idx + 1 >= rooms.size()) ? 0 : idx + 1;
        dungeon.changeRoom(rooms.get(nxt));
        return true;
    }

    // Puzzles 

    /** Answers a riddle puzzle by id; grants reward item on success. */
    public boolean answerRiddle(UUID puzzleId, String answer) {
        Puzzle p = findPuzzle(puzzleId);
        if (!(p instanceof Riddle)) return false;
        ValidationResult res = p.enterInput(answer);
        if (res != null && res.isValid() && res.getNewState() == PuzzleState.SOLVED) grantRewardItem(p);
        return res != null && res.isValid();
    }

    /** Answers a word scramble (or generic) puzzle by id; grants reward item on success. */
    public boolean answerScramble(UUID puzzleId, String answer) {
        Puzzle p = findPuzzle(puzzleId);
        if (p == null) return false;

        if (p.getClass().getSimpleName().toLowerCase().contains("scramble")) {
            ValidationResult res = p.enterInput(answer);
            if (res != null && res.isValid() && res.getNewState() == PuzzleState.SOLVED) grantRewardItem(p);
            return res != null && res.isValid();
        }

        ValidationResult res = p.enterInput(answer);
        if (res != null && res.isValid() && res.getNewState() == PuzzleState.SOLVED) grantRewardItem(p);
        return res != null && res.isValid();
    }

    /** Attempts a code-based puzzle by id, enforcing required item checks; grants reward on success. */
    public boolean attemptCodePuzzle(UUID puzzleId, String code) {
        Puzzle p = findPuzzle(puzzleId);
        if (p == null) return false;
        if (!hasRequiredItem(p)) {
            return false;
        }

        ValidationResult res = p.enterInput(code);
        if (res != null && res.isValid() && res.getNewState() == PuzzleState.SOLVED) grantRewardItem(p);
        return res != null && res.isValid();
    }

    /** Finds a puzzle by UUID in the current dungeon, or null if not found. */
    private Puzzle findPuzzle(UUID puzzleId) {
        if (dungeon == null || puzzleId == null) return null;
        for (Room r : dungeon.getRooms()) {
            if (r.getPuzzles() == null) continue;
            for (Puzzle p : r.getPuzzles()) {
                if (puzzleId.equals(p.getPuzzleID())) return p;
            }
        }
        return null;
    }

    // Inventory 

    /** Returns a snapshot list of inventory keys acquired by the player. */
    public List<String> getInventoryKeys() { return new ArrayList<>(inventory); }

    /** Uses an item by key if present in the inventory. */
    public boolean useItemByKey(String key) {
        if (key == null) return false;
        if (inventory.contains(key)) {
            System.out.println("[Inventory] Used: " + key);
            return true;
        }
        return false;
    }

    /** Grants a reward item to the inventory based on puzzle metadata or title heuristics. */
    @SuppressWarnings("UseSpecificCatch")
    private void grantRewardItem(Puzzle p) {
        if (p == null) return;
        try {
            var m = p.getClass().getMethod("getRewardItem");
            Object v = m.invoke(p);
            if (v instanceof String key && !key.isBlank()) {
                inventory.add(key);
                System.out.println("[Inventory] You acquired: " + key);
                return;
            }
        } catch (Throwable ignored) {}

        String t = (p.getTitle() == null) ? "" : p.getTitle().toLowerCase();
        if (t.contains("riddle")) {
            inventory.add("CIPHER_WHEEL");
            System.out.println("[Inventory] You acquired: CIPHER_WHEEL");
        } else if (t.contains("scramble")) {
            inventory.add("GOLD_COIN");
            System.out.println("[Inventory] You acquired: GOLD_COIN");
        }
    }

    /** Returns true if any required item for the puzzle is in the inventory. */
    @SuppressWarnings({"UseSpecificCatch", "UnnecessaryUnboxing"})
    private boolean hasRequiredItem(Puzzle p) {
        boolean requires = false;
        String key = null;
        try {
            var m1 = p.getClass().getMethod("isRequiresItem");
            Object v1 = m1.invoke(p);
            requires = (v1 instanceof Boolean b) ? b.booleanValue() : false;
        } catch (Throwable ignored) {}
        try {
            var m2 = p.getClass().getMethod("getRequiredItemKey");
            Object v2 = m2.invoke(p);
            if (v2 instanceof String s && !s.isBlank()) key = s;
        } catch (Throwable ignored) {}

        if (!requires && key == null) {
            String title = p.getTitle() == null ? "" : p.getTitle().toLowerCase();
            if (title.contains("sentinel") || title.contains("code") || title.contains("keypad")) {
                requires = true;
                key = "CIPHER";
            }
        }

        if (!requires) return true;
        if (key == null || key.isBlank()) return true; 
        return inventory.contains(key);
    }

    //  Timer 

    /** Pauses the active dungeon timer, if running. */
    public void pauseTimer() {
        if (dungeon != null && dungeon.getTimer() != null && dungeon.getTimer().isRunning()) {
            dungeon.getTimer().stop();
        }
    }

    /** Resumes the active dungeon timer, if paused. */
    public void resumeTimer() {
        if (dungeon != null && dungeon.getTimer() != null && !dungeon.getTimer().isRunning()) {
            dungeon.getTimer().unPause();
        }
    }

    // Loader helper 

    /** Ensures dungeon data is loaded exactly once for the process lifetime. */
    private void ensureDungeonsLoadedOnce() {
        if (dungeonsLoaded) return;
        synchronized (Facade.class) {
            if (dungeonsLoaded) return;
            DataLoader loader = new DataLoader();
            loader.loadDungeons();
            List<Dungeon> loaded = loader.getDungeons();
            if (loaded != null && !loaded.isEmpty()) {
                DungeonList.getInstance().replaceAll(loaded);
            }
            dungeonsLoaded = true;
        }
    }
}
