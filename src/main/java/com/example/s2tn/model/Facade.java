package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Facade {

    private Account user;
    private Dungeon dungeon;
    private static volatile boolean dungeonsLoaded = false;


// --------Account---------------------------------------------------------------------------------------

    public Account signUp(String username, String password) {
        if (username == null || password == null) return null;
        UserList ul = UserList.getInstance();
        if (ul.getUser(username) != null) return null;
        Account a = new Account(username, password);
        a.setScore(0);
        a.setRank(0);
        if (!ul.addUser(a)) return null;
        new DataWriter().saveUsers();
        this.user = a;
        return a;
    }

    public boolean login(String username, String password) {
    UserList ul = UserList.getInstance();
    Account a = ul.getUser(username);
    if (a != null && a.login(username, password)) {
        this.user = a;
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

    public void chooseDifficulty(String level) {
    if (dungeon == null || level == null) return;
    switch (level.toLowerCase()) {
        case "easy" -> dungeon.setDifficulty(Difficulty.EASY);
        case "normal" -> dungeon.setDifficulty(Difficulty.NORMAL);
        case "hard" -> dungeon.setDifficulty(Difficulty.HARD);
        default -> { }
    }
    }

    public UUID enterDungeon(UUID dungeonId) {
    Dungeon picked = selectDungeon(dungeonId);
    if (picked == null) return null;
    if (picked.getTimer() != null) picked.getTimer().start();
    return picked.getUUID();
    }


    public void startDungeon(UUID dungeonId) {
        enterDungeon(dungeonId);
    }

    public void restartDungeon() {

    }

    public void exitDungeon() {
        if (dungeon != null && dungeon.getTimer() != null && dungeon.getTimer().isRunning()) {
        dungeon.getTimer().stop();
    }
    }

    public void completeDungeon() {
    }

    public com.example.s2tn.model.Map getMap() {
        return dungeon == null ? null : dungeon.getMap();
    }

    public java.util.UUID enterRoom(java.util.UUID roomId) {
    if (dungeon == null) return null;
    if (roomId == null) {
        Room cur = (dungeon.getCurrentRoom() != null) ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
        return (cur == null) ? null : cur.getRoomID();
    }
    for (Room r : dungeon.getRooms()) {
        if (roomId.equals(r.getRoomID())) {
            dungeon.changeRoom(r);
            return roomId;
        }
    }
    return null;
    }


    public List<Room> viewRooms() {
        return dungeon == null ? new ArrayList<>() : dungeon.getRooms();
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
    ArrayList<Room> rooms = dungeon.getRooms();
    if (rooms == null || rooms.isEmpty()) return false;
    Room current = (dungeon.getCurrentRoom() != null) ? dungeon.getCurrentRoom() : dungeon.getStartingRoom();
    if (current == null) return false;
    int idx = rooms.indexOf(current);
    if (idx >= 0 && idx + 1 < rooms.size()) {
        dungeon.changeRoom(rooms.get(idx + 1));
        return true;
    }
    return false;
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
        attemptCodePuzzle(code);
    }

    public void moveInMaze(UUID puzzleId, String direction) {
    }

    public boolean slideShape(UUID puzzleId, Object shape, String direction, int steps) {
        return false;
    }

    public void alignShapes(UUID puzzleId, List<Object> shapes) {
    }

    public boolean answerRiddle(UUID puzzleId, String answer) {
        Riddle riddle = new Riddle();
        riddle.displayRiddle();
        return riddle.checkAnswer(answer);
    }

    public boolean answerScramble(UUID puzzleId, String answer) {
        WordScramble scramble = new WordScramble();
        scramble.displayScramble();
        return scramble.checkAnswer(answer);
    }

    public boolean attemptCodePuzzle(String code) {
        CodePuzzle cp = new CodePuzzle();
        return cp.submit(code);
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

    private void markRoomComplete(UUID roomId) {
    for (Room findRoom : dungeon.getRooms()) {
        if (roomId != null && roomId.equals(findRoom.getRoomID())) {
            dungeon.getMap().markComplete(findRoom);
        }
    }
    }

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
}
