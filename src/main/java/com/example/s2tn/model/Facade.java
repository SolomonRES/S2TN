package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Facade {

    private Account user;
    private Dungeon dungeon;
    private Progress progress;
    private Leaderboard leaderboard;
    private static volatile boolean dungeonsLoaded = false;


// --------Account---------------------------------------------------------------------------------------

    public boolean signUp(String username, String password) {
        ensureUsersLoadedOnce();
        if (username == null || username.isBlank() || password == null) return false;
        Account a = new Account();
        a.setUserName(username.trim());
        a.setPassword(password);     
        return new UserService().addUser(a); 
    }

    public boolean login(String username, String password) {
        ensureUsersLoadedOnce();
        Account found = new UserService().getByUserName(username);
        if (found == null) return false;

        if (found.login(username, password)) {
            this.user = found;
            return true;
        }
        return false;
    }

    public void logout() {
        this.user = null;
    }

    public Account getAccount() {
        return user;
    }

// ----------------Progress-----------------------------------------------------------------------------

    public void saveProgress() {
    }

    public Progress loadProgress(String userId) {
        return new Progress();
    }

    public List<String> listSaves() {
        return new ArrayList<>();
    }

    public Progress loadSave(String slot) {
        return new Progress();
    }

    public void deleteSave(String slot) {
    }

    public void resetProgress() {
    }

    public Progress getCurrentStatus() {
        return new Progress();
    }

// --------------------Dungeon/Map----------------------------------------------------------------------

    public Dungeon selectDungeon(java.util.UUID dungeonId) {
        ensureDungeonsLoaded();
        java.util.List<Dungeon> ds = DungeonList.getInstance().getAll();
        if (ds.isEmpty()) return null;

        if (dungeonId == null) {
            dungeon = ds.get(0);
            return dungeon;
        }
        for (Dungeon d : ds) {
            if (d.getUUID().equals(dungeonId)) {
                dungeon = d;
                return dungeon;
            }
        }
        return null;
    }

    public java.util.List<Dungeon> listDungeons() {
        ensureDungeonsLoadedOnce();
        return DungeonList.getInstance().getAll();
    }

    public void chooseDifficulty(String level) {
    if (dungeon == null || level == null) return;
    switch (level.toLowerCase()) {
        case "easy" -> dungeon.setDifficulty(Difficulty.EASY);
        case "normal" -> dungeon.setDifficulty(Difficulty.NORMAL);
        case "hard" -> dungeon.setDifficulty(Difficulty.HARD);
        default -> { }
    }
    }

    public boolean enterDungeon() {
        if (this.dungeon == null) return false;
        if (dungeon.getTimer() != null) dungeon.getTimer().start();
        if (dungeon.getCurrentRoom() == null) {
            Room start = dungeon.getStartingRoom();
            if (start != null) dungeon.changeRoom(start);
        }
        progress = new Progress();
        return true;
    }

    public boolean startDungeon(java.util.UUID id) {
        ensureDungeonsLoadedOnce();
        java.util.List<Dungeon> ds = DungeonList.getInstance().getAll();
        if (ds.isEmpty()) return false;
        Dungeon pick = (id == null) ? ds.get(0)
                : ds.stream().filter(d -> id.equals(d.getUUID())).findFirst().orElse(null);
        if (pick == null) return false;
        this.dungeon = pick;
        return true;
    }

    public boolean restartDungeon() {
        // Set progress to null
        // Reload dungeonID and roomID
        if(dungeon == null || progress == null){
            return false;
        }
        UUID id = dungeon.getUUID();
        dungeon = null;
        progress = null;
        startDungeon(id);
        enterRoom(id);
        return true;
    }

    public void exitDungeon() {
        this.dungeon = null;
    }

    public boolean completeDungeon() {
        if(dungeon == null){
            return false;
        }
        if(dungeon.getRooms().size() == dungeon.getMap().getCompletedRooms().size()){
            dungeon.getTimer().stop();
            int pointsAwarded = (int) (dungeon.getMaxTimeAllowed() - dungeon.getTimer().elapsedTime()) / 10;
            switch (dungeon.getDifficulty()){
                case EASY -> pointsAwarded *= 2;
                case NORMAL -> pointsAwarded *= 4;
                case HARD -> pointsAwarded *= 6;
            }
            user.updateScore(pointsAwarded);

            return true;
        }
        return false;
    }

    public com.example.s2tn.model.Map getMap() {
        return dungeon == null ? null : dungeon.getMap();
    }

    public boolean enterRoom(java.util.UUID roomId) {
        if (dungeon == null || roomId == null) return false;
        for (Room r : dungeon.getRooms()) {
            if (roomId.equals(r.getRoomID())) {
                dungeon.changeRoom(r);
                return true;
            }
        }
        return false;
    }


    public java.util.List<Room> viewRooms() {
        return dungeon == null ? new java.util.ArrayList<>() : dungeon.getRooms();
    }

    public void changeRoom(java.util.UUID roomID) {
    for (Room findRoom : dungeon.getRooms()) {
        if (roomID != null && roomID.equals(findRoom.getRoomID())) {
            dungeon.changeRoom(findRoom);
            break;
        }
    }
    }

    public boolean nextRoom() {
        if (dungeon == null) return false;
        java.util.List<Room> rooms = dungeon.getRooms();
        if (rooms == null || rooms.isEmpty()) return false;
        Room cur = (dungeon.getCurrentRoom() != null) ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
        if (cur == null) { dungeon.changeRoom(rooms.get(0)); return true; }
        int idx = rooms.indexOf(cur);
        int nxt = (idx < 0 || idx + 1 >= rooms.size()) ? 0 : idx + 1;
        dungeon.changeRoom(rooms.get(nxt));
        return true;
    }


    public void previousRoom() {
    if (dungeon == null) return;
    Room prev = dungeon.getPreviousRoom();
    if (prev != null) dungeon.changeRoom(prev);
    }


// -------------------Puzzle(s)-------------------------------------------------------------------------

    public void attemptPuzzle(UUID puzzleId, String input) {
    }

    public Hint requestHint(UUID puzzleId, int level) {
        return new Hint();
    }

    public void skipPuzzle(UUID puzzleId) {
    }

    public void solvePuzzle(UUID puzzleId) {
    }

    public void submitCode(UUID puzzleId, String code) {
        attemptCodePuzzle(puzzleId, code);
    }

    public void moveInMaze(UUID puzzleId, String direction) {
    }

    public boolean slideShape(UUID puzzleId, Object shape, String direction, int steps) {
        return false;
    }

    public void alignShapes(UUID puzzleId, List<Object> shapes) {
    }

    public ValidationResult attemptCodePuzzle(String input) {
        if (dungeon == null) {
            return ValidationResult.invalidFormat("No active dungeon.", PuzzleState.INIT);
        }
        Room cur = (dungeon.getCurrentRoom() != null) ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
        if (cur == null || cur.getPuzzles() == null || cur.getPuzzles().isEmpty()) {
            return ValidationResult.invalidFormat("No puzzle in this room.", PuzzleState.INIT);
        }
        for (Puzzle p : cur.getPuzzles()) {
            if (p.getState() != PuzzleState.SOLVED) {
                return p.enterInput(input);
            }
        }
        return ValidationResult.invalidFormat("All puzzles solved in this room.", PuzzleState.SOLVED);
    }

    public boolean answerRiddle(UUID puzzleId, String answer) {
        Puzzle p = findPuzzleInActiveDungeon(puzzleId);
        if (!(p instanceof Riddle)) return false;
        ValidationResult res = p.enterInput(answer);
        return res != null && res.isValid();
    }

    public boolean answerScramble(UUID puzzleId, String answer) {
        WordScramble scramble = new WordScramble();
        scramble.displayScramble();
        return scramble.checkAnswer(answer);
    }

    public boolean attemptCodePuzzle(UUID puzzleId, String code) {
        Puzzle p = findPuzzleInActiveDungeon(puzzleId);
        if (!(p instanceof CodePuzzle)) return false;
        ValidationResult res = p.enterInput(code);
        return res != null && res.isValid();
    }

    private Puzzle findPuzzleInActiveDungeon(UUID puzzleId) {
    if (dungeon == null || puzzleId == null) return null;
    for (Room r : dungeon.getRooms()) {
        if (r.getPuzzles() == null) continue;
        for (Puzzle p : r.getPuzzles()) {
            if (puzzleId.equals(p.getPuzzleID())) return p;
        }
    }
    return null;
    }

// -----------------------Start/Stop Time-------------------------------------------------------

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

// ------------------also progress/leaderboard/map? unsure------------------------------

    @SuppressWarnings("unused")
    private void unlockExit(UUID fromRoom, UUID toRoom) {
    for (Room findRoom : dungeon.getRooms()) {
        if (fromRoom != null && fromRoom.equals(findRoom.getRoomID())) {
            for (Room isLocked : findRoom.getExits()) {
                if (toRoom != null && toRoom.equals(isLocked.getRoomID())) {
                    isLocked.unlock(isLocked);
                }
            }
        }
    }
    }

    @SuppressWarnings("unused")
    private void markRoomExplored(UUID fromRoom, UUID toRoom) {
    for (Room findRoom : dungeon.getRooms()) {
        if (fromRoom != null && fromRoom.equals(findRoom.getRoomID())) {
            for (Room unexplored : findRoom.getExits()) {
                if (toRoom != null && toRoom.equals(unexplored.getRoomID())) {
                    dungeon.getMap().markExplored(unexplored);
                }
            }
        }
    }
    }

    @SuppressWarnings("unused")
    private void markRoomComplete(UUID roomId) {
    for (Room findRoom : dungeon.getRooms()) {
        if (roomId != null && roomId.equals(findRoom.getRoomID())) {
            dungeon.getMap().markComplete(findRoom);
        }
    }
    }

    @SuppressWarnings("unused")
    private void submitScore(String userName, int score, long elapsedTime) {

    }

// -----------------------------helper methods-------------------------------------------------------------------------

private void ensureDungeonsLoaded() {
    if (dungeonsLoaded) return;
    synchronized (Facade.class) {
        if (dungeonsLoaded) return;

        DataLoader loader = new DataLoader();
        loader.loadDungeons();
        java.util.List<Dungeon> loaded = loader.getDungeons();
        if (loaded != null && !loaded.isEmpty()) {
            DungeonList.getInstance().replaceAll(loaded);
        }
        dungeonsLoaded = true;
    }
}

private void ensureDungeonsLoadedOnce() {
    if (dungeonsLoaded) return;
    synchronized (Facade.class) {
        if (dungeonsLoaded) return;
        DataLoader loader = new DataLoader();
        loader.loadDungeons();                     
        java.util.List<Dungeon> loaded = loader.getDungeons();
        if (loaded != null && !loaded.isEmpty()) {
            DungeonList.getInstance().replaceAll(loaded);
        }
        dungeonsLoaded = true;
    }
}

private static volatile boolean usersLoaded = false;
private void ensureUsersLoadedOnce() {
    if (usersLoaded) return;
    synchronized (Facade.class) {
        if (usersLoaded) return;
        DataLoader loader = new DataLoader();
        loader.loadUsers();                 
        usersLoaded = true;
    }
}

public java.util.UUID getCurrentRoomId() {
    if (dungeon == null) return null;
    Room cur = dungeon.getCurrentRoom() != null ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
    return cur == null ? null : cur.getRoomID();
}

public java.util.List<String> debugListLoadedDungeons() {
    ensureDungeonsLoadedOnce();
    java.util.List<String> rows = new java.util.ArrayList<>();
    for (Dungeon d : DungeonList.getInstance().getAll()) {
        int rc = d.getRooms() == null ? 0 : d.getRooms().size();
        rows.add(d.getUUID() + " :: " + d.getName() + " (rooms: " + rc + ")");
    }
    return rows;
}

public String getCurrentPuzzleQuestion() {
    if (dungeon == null) return null;
    Room cur = (dungeon.getCurrentRoom() != null) ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
    if (cur == null || cur.getPuzzles() == null) return null;
    for (Puzzle p : cur.getPuzzles()) {
        if (p.getState() != PuzzleState.SOLVED) {
            if (p instanceof Riddle r) return r.getQuestion();
            return null;
        }
    }
    return null;
}

    @SuppressWarnings("UseSpecificCatch")
    public String getCurrentPuzzleHint() {
    if (dungeon == null) return null;
    Room cur = (dungeon.getCurrentRoom() != null) ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
    if (cur == null || cur.getPuzzles() == null) return null;

    for (Puzzle p : cur.getPuzzles()) {
        if (p.getState() != PuzzleState.SOLVED) {

            if (p instanceof Riddle r && r.getHint() != null && !r.getHint().isBlank()) {
            return r.getHint();
            }

            if (p instanceof CodePuzzle cp && cp.getHint() != null && !cp.getHint().isBlank()) {
            return cp.getHint();
            }

            try {
                java.lang.reflect.Method m = p.getClass().getMethod("getHints");
                Object res = m.invoke(p);
                if (res instanceof java.util.List<?> list && !list.isEmpty()) {
                    Object maybeHint = list.get(0);
                    if (maybeHint instanceof Hint h) return h.getText();
                    return String.valueOf(maybeHint);
                }
            } catch (Throwable ignored) {}
            return "No hint available for this puzzle.";
        }
    }
    return null;
}

}
