package com.example.s2tn.model;

import java.util.*;

public class Facade {

    private Account user;
    private Dungeon dungeon;

    private final Set<String> inventory = new HashSet<>();

    //  Users 
    public boolean register(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null) return false;
        Account a = new Account();
        a.setUserName(userName);
        a.setPassword(password);
        return new UserService().addUser(a);
    }

    public boolean login(String userName, String password) {
        Account found = new UserService().getByUserName(userName);
        if (found == null) return false;
        if (!Objects.equals(password, found.getPassword())) return false;
        this.user = found;
        return true;
    }

    public void logout() { this.user = null; }
    public Account getCurrentUser() { return user; }
    // leaderboard

    public List<Account> getTopPlayers(int n) {
    return new Leaderboard().getTopPlayers(n);
    }
    
    // Dungeons 
    private static volatile boolean dungeonsLoaded = false;

    public List<Dungeon> listDungeons() {
        ensureDungeonsLoadedOnce();
        return DungeonList.getInstance().getAll();
    }

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

    public boolean enterDungeon() {
        if (dungeon == null) return false;
        if (dungeon.getTimer() != null) dungeon.getTimer().start();
        if (dungeon.getCurrentRoom() == null) {
            Room start = dungeon.getStartingRoom();
            if (start != null) dungeon.changeRoom(start);
        }
        return true;
    }

    public void exitDungeon() { dungeon = null; }

    public void chooseDifficulty(String level) {
        if (dungeon == null || level == null) return;
        switch (level.toLowerCase()) {
            case "easy"   -> dungeon.setDifficulty(Difficulty.EASY);
            case "hard"   -> dungeon.setDifficulty(Difficulty.HARD);
            default       -> dungeon.setDifficulty(Difficulty.NORMAL);
        }
    }

    // Rooms 
    public List<Room> viewRooms() {
        return dungeon == null ? Collections.emptyList() : dungeon.getRooms();
    }

    public UUID getCurrentRoomId() {
        if (dungeon == null) return null;
        Room cur = dungeon.getCurrentRoom() != null ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
        return cur == null ? null : cur.getRoomID();
    }

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
    public boolean answerRiddle(UUID puzzleId, String answer) {
        Puzzle p = findPuzzle(puzzleId);
        if (!(p instanceof Riddle)) return false;
        ValidationResult res = p.enterInput(answer);
        if (res != null && res.isValid() && res.getNewState() == PuzzleState.SOLVED) grantRewardItem(p);
        return res != null && res.isValid();
    }

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
    public List<String> getInventoryKeys() { return new ArrayList<>(inventory); }

    public boolean useItemByKey(String key) {
        if (key == null) return false;
        if (inventory.contains(key)) {
            System.out.println("[Inventory] Used: " + key);
            return true;
        }
        return false;
    }

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
    public void pauseTimer() {
        if (dungeon != null && dungeon.getTimer() != null && dungeon.getTimer().isRunning()) {
            dungeon.getTimer().stop();
        }
    }

    public void resumeTimer() {
        if (dungeon != null && dungeon.getTimer() != null && !dungeon.getTimer().isRunning()) {
            dungeon.getTimer().unPause();
        }
    }

    // Loader helper 
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
