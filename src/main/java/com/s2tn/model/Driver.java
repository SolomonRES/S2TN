package com.s2tn.model;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import com.s2tn.Speak;

/**
 * Console driver for interacting with the escape-room system via menus.
 * Handles account, dungeon, rooms, puzzles, timer, inventory, progress, and leaderboard flows.
 */
public class Driver {
    private static Puzzle selectedPuzzle = null;

    /** Entry point: runs the interactive menu loop. */
    public static void main(String[] args) {
        Facade facade = new Facade();
        try (Scanner in = new Scanner(System.in)) {
            
            // Start speaking immediately when the driver starts
            if (Speak.isEnabled()) {
                Speak.speak("Welcome to Virtual Escape Room. Voice is on. Use the menu to begin.");
            }
            boolean running = true;
            while (running) {
                int choice = mainMenu(in);
                switch (choice) {
                    case 1 -> accountMenu(in, facade);
                    case 2 -> dungeonMenu(in, facade);
                    case 3 -> roomsMenu(in, facade);
                    case 4 -> puzzlesMenu(in, facade);
                    case 5 -> timerMenu(in, facade);
                    case 6 -> inventoryMenu(in, facade);
                    case 7 -> // Progress
                        progressMenu(facade);
                    case 8 -> // Leaderboard
                        leaderboardMenu(facade);
                    case 9 -> {
                        Speak.toggle();
                        println("Voice is now " + (Speak.isEnabled() ? "ON" : "OFF"));
                    }
                    case 0 -> {
                        println("Goodbye");
                        running = false;
                    }
                    default -> println("Invalid selection");
                }
            }
        }
    }

    /** Shows the main menu and returns the user's selection. */
    private static int mainMenu(Scanner in) {
        println("");
        println("==== Virtual Escape Room ====");
        println("1) Account");
        println("2) Dungeon");
        println("3) Rooms");
        println("4) Puzzles");
        println("5) Timer");
        println("6) Inventory");
        println("7) Progress");
        println("8) Leaderboard");
        println("9) Voice (On/Off)");  
        println("0) Quit");
        return askInt(in, "Select: ");
    }

    /** Handles account actions: sign up, login, logout. */
    private static void accountMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Account —");
            println("1) Sign up");
            println("2) Login");
            println("3) Logout");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 ->  {
                    String u = ask(in, "Username: ");
                    String p = ask(in, "Password: ");
                    boolean ok = facade.register(u, p);
                    println(ok ? "Sign up successful" : "Sign up failed");
                }
                case 2 ->  {
                    String u = ask(in, "Username: ");
                    String p = ask(in, "Password: ");
                    boolean ok = facade.login(u, p);
                    println(ok ? "Login successful." : "Login failed");
                }
                case 3 ->  {
                    facade.logout();
                    println("Logged out");
                }
                case 0 -> back = true;
                default -> println("Invalid selection");
            }
        }
    }

    /** Handles dungeon operations: choose difficulty, list/start/exit dungeons. */
    private static void dungeonMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Dungeon —");
            println("1) Choose difficulty (easy/normal/hard)");
            println("2) List dungeons");
            println("3) Start dungeon by number");
            println("4) Exit current dungeon");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 ->  {
                    String diff = ask(in, "Difficulty [easy|normal|hard]: ");
                    facade.chooseDifficulty(diff);
                    println("Difficulty set");
                }
                case 2 ->  {
                    List<Dungeon> ds = facade.listDungeons();
                    if (ds == null || ds.isEmpty()) {
                        println("No dungeons loaded. Ensure rooms.json is present");
                    }
                    println("Available dungeons:");
                    for (int i = 0; i < ds.size(); i++) {
                        Dungeon d = ds.get(i);
                        String name = d.getName() != null ? d.getName() : ("Dungeon " + (i + 1));
                        String dDiff = d.getDifficulty() != null ? d.getDifficulty().name() : "UNKNOWN";
                        println(" " + (i + 1) + ") " + name + " [" + dDiff + "]  id=" + d.getUUID());
                    }
                }
                case 3 ->  {
                    List<Dungeon> ds = facade.listDungeons();
                    if (ds == null || ds.isEmpty()) {
                        println("No dungeons available; check rooms.json");
                    }
                    int idx = askInt(in, "Enter dungeon number (1.." + ds.size() + "): ");
                    if (idx < 1 || idx > ds.size()) {
                        println("Invalid selection");
                    }
                    Dungeon chosen = ds.get(idx - 1);
                    boolean started = facade.startDungeon(chosen.getUUID());
                    if (!started) {
                        println("Failed to select dungeon.");
                    }
                    boolean entered = facade.enterDungeon();
                    println(entered ? "Started: " + chosen.getName() : "Failed to enter dungeon");
                }
                case 4 ->  {
                    facade.exitDungeon();
                    println("Exited current dungeon");
                    selectedPuzzle = null;
                }
                case 0 -> back = true;
                default -> println("Invalid selection");
            }
        }
    }

    /** Handles room navigation: list rooms, show current, enter by number, or move to next. */
    private static void roomsMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("= Rooms =");
            println("1) List rooms");
            println("2) Show current room ID");
            println("3) Enter room by number");
            println("4) Next room");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 ->  {
                    List<Room> rooms = facade.viewRooms();
                    if (rooms == null || rooms.isEmpty()) {
                        println("No rooms; Start a dungeon first");
                    }
                    println("Rooms:");
                    for (int i = 0; i < rooms.size(); i++) {
                        Room r = rooms.get(i);
                        int count = (r.getPuzzles() == null) ? 0 : r.getPuzzles().size();
                        println(" " + (i + 1) + ") id=" + r.getRoomID() + " puzzles=" + count);
                    }
                }
                case 2 ->  {
                    UUID id = facade.getCurrentRoomId();
                    println(id != null ? "Current room ID: " + id : "No current room");
                }
                case 3 ->  {
                    List<Room> rooms = facade.viewRooms();
                    if (rooms == null || rooms.isEmpty()) {
                        println("No rooms. Start a dungeon first");
                    }
                    int r = askInt(in, "Room number (1.." + rooms.size() + "): ");
                    if (r < 1 || r > rooms.size()) {
                        println("Invalid room number");
                    }
                    boolean ok = facade.enterRoom(rooms.get(r - 1).getRoomID());
                    selectedPuzzle = null;
                    println(ok ? "Entered room" : "Failed to enter room");
                }
                case 4 ->  {
                    boolean moved = facade.nextRoom();
                    selectedPuzzle = null;
                    println(moved ? "Moved to next room" : "No next room");
                }
                case 0 -> back = true;
                default -> println("Invalid selection.");
            }
        }
    }

    /** Handles puzzle operations: list/select puzzles, show question/hint, and answer. */
    private static void puzzlesMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Puzzles —");
            println("1) List puzzles in CURRENT room");
            println("2) Select a puzzle BY NUMBER");
            println("3) Show question for SELECTED puzzle");
            println("4) Get a HINT for SELECTED puzzle");
            println("5) Answer SELECTED puzzle");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> listPuzzles(facade);
                case 2 -> selectPuzzleByNumber(in, facade);
                case 3 -> showSelectedQuestion();
                case 4 -> showSelectedHint();
                case 5 -> answerSelected(in, facade);
                case 0 -> back = true;
                default -> println("Invalid selection.");
            }
        }
    }

    /** Lists the puzzles available in the current room. */
    private static void listPuzzles(Facade facade) {
        Room cur = ensureCurrentRoomEntered(facade);
        if (cur == null) return;
        List<Puzzle> puzzles = cur.getPuzzles();
        if (puzzles == null || puzzles.isEmpty()) {
            println("This room has 0 puzzles");
            return;
        }
        println("Puzzles in current room:");
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle p = puzzles.get(i);
            println(" " + (i + 1) + ") id=" + p.getPuzzleID()
                    + "  title=\"" + safe(p.getTitle()) + "\"  state=" + p.getState());
        }
    }

    /** Prompts the user to select a puzzle by number and marks it as selected. */
    private static void selectPuzzleByNumber(Scanner in, Facade facade) {
        Room cur = ensureCurrentRoomEntered(facade);
        if (cur == null) return;
        List<Puzzle> puzzles = cur.getPuzzles();
        if (puzzles == null || puzzles.isEmpty()) {
            println("This room has 0 puzzles");
            return;
        }
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle p = puzzles.get(i);
            println(" " + (i + 1) + ") " + safe(p.getTitle()) + " [" + p.getState() + "]");
        }
        int ix = askInt(in, "Pick puzzle number (1.." + puzzles.size() + "): ");
        if (ix < 1 || ix > puzzles.size()) {
            println("Invalid selection");
            return;
        }
        selectedPuzzle = puzzles.get(ix - 1);
        println("Selected: " + safe(selectedPuzzle.getTitle()));
    }

    /** Prints the question or prompt for the currently selected puzzle. */
    private static void showSelectedQuestion() {
        if (selectedPuzzle == null) {
            println("No puzzle selected.");
            return;
        }
        String q = buildQuestion(selectedPuzzle);
        println((q == null || q.trim().isEmpty()) ? "No question available." : ("Question: " + q));
    }

    /** Prints a hint for the currently selected puzzle, if available. */
    private static void showSelectedHint() {
        if (selectedPuzzle == null) {
            println("No puzzle selected.");
            return;
        }
        String hint = buildHint(selectedPuzzle);
        println(hint == null ? "No hint available." : ("Hint: " + hint));
    }

    /** Accepts input for the selected puzzle, dispatching to the appropriate facade method. */
    private static void answerSelected(Scanner in, Facade facade) {
        if (selectedPuzzle == null) {
            println("No puzzle selected.");
            return;
        }
        String q = buildQuestion(selectedPuzzle);
        if (q != null && !q.trim().isEmpty()) {
            println("");
            println("Question: " + q);
        }
        String input = ask(in, "Your answer (or 'H' for hint): ");
        if ("H".equalsIgnoreCase(input)) {
            String hint = buildHint(selectedPuzzle);
            println(hint == null ? "No hint available." : ("Hint: " + hint));
            input = ask(in, "Your answer: ");
        }

        UUID pid = selectedPuzzle.getPuzzleID();
        String titleLower = safe(selectedPuzzle.getTitle()).toLowerCase(Locale.ROOT);

        boolean ok;
        if (hasMethod(selectedPuzzle, "getQuestion") || titleLower.contains("riddle")) {
            ok = facade.answerRiddle(pid, input);
            println(ok ? "[\u2713] You solved the riddle  New state: SOLVED" : "[x] Incorrect answer.");
        } else if (hasMethod(selectedPuzzle, "getScrambledWord") || titleLower.contains("scramble")) {
            ok = facade.answerScramble(pid, input);
            println(ok ? "[\u2713] Correct! Unscramble solved.  New state: SOLVED" : "[x] Not quite—try again.");
        } else {
            ok = facade.attemptCodePuzzle(pid, input);
            println(ok ? "[\u2713] Correct code!  New state: SOLVED"
                       : "[x] Incorrect or missing required item.");
        }
    }

    /** Inventory menu for listing and using items. */
    private static void inventoryMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Inventory —");
            println("1) Show items");
            println("2) Use item (by key)");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 ->  {
                    List<String> items = facade.getInventoryKeys();
                    if (items == null || items.isEmpty()) println("(empty)");
                    else for (String k : items) println(" - " + k);
                }
                case 2 ->  {
                    String key = ask(in, "Item key: ");
                    boolean ok = facade.useItemByKey(key);
                    println(ok ? "Used " + key : "You don’t have that item.");
                }
                case 0 -> back = true;
                default -> println("Invalid selection");
            }
        }
    }

    /** Timer menu for pausing and resuming the game timer. */
    private static void timerMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("= Timer =");
            println("1) Pause");
            println("2) Resume");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> {
                    facade.pauseTimer();
                    println("Timer paused");
                }
                case 2 -> {
                    facade.resumeTimer();
                    println("Timer resumed");
                }
                case 0 -> back = true;
                default -> println("Invalid selection");
            }
        }
    }

    /** Prints a summary of progress (puzzles solved vs total) in the current dungeon. */
    private static void progressMenu(Facade facade) {
        List<Room> rooms = facade.viewRooms();
        if (rooms == null || rooms.isEmpty()) {
            println("No progress yet. Start a dungeon and enter a room first.");
            return;
        }
        int total = 0, solved = 0;
        for (Room r : rooms) {
            List<Puzzle> ps = r.getPuzzles();
            if (ps == null) continue;
            for (Puzzle p : ps) {
                total++;
                Object st = p.getState();
                if (st != null && "SOLVED".equalsIgnoreCase(String.valueOf(st))) {
                    solved++;
                }
            }
        }
        double pct = total == 0 ? 0.0 : (100.0 * solved / total);
        println(String.format(Locale.US, "Progress: %d/%d solved (%.1f%%)", solved, total, pct));
    }

    /** Displays the leaderboard via the facade, showing top players and scores. */
    private static void leaderboardMenu(Facade facade) {
        List<?> top;
        try {
            top = facade.getTopPlayers(10);
        } catch (Throwable t) {
            println("Leaderboard not available via Facade: " + t.getMessage());
            return;
        }
        if (top == null || top.isEmpty()) {
            println("No leaderboard data yet.");
            return;
        }
        println(String.format("%-4s %-20s %s", "Rank", "User", "Score"));
        println("---------------------------------------");
        int rank = 1;
        for (Object entry : top) {
            String name = tryGet(entry, "getUserName");
            if (name == null || name.isEmpty()) name = tryGet(entry, "getUsername");
            String score = tryGet(entry, "getScore");
            if (score == null || score.isEmpty()) score = "0";
            println(String.format("%-4d %-20s %s", rank++, name == null ? "(unknown)" : name, score));
        }
    }

    /**
     * Ensures a current room is entered; if none, attempts to enter the first room.
     * Returns the current room or null if unavailable.
     */
    private static Room ensureCurrentRoomEntered(Facade facade) {
        List<Room> rooms = facade.viewRooms();
        if (rooms == null || rooms.isEmpty()) {
            println("No rooms. Start a dungeon (Dungeon > Start) first");
            return null;
        }
        if (facade.getCurrentRoomId() == null) {
            boolean ok = facade.enterRoom(rooms.get(0).getRoomID());
            if (!ok) {
                println("Could not enter the first room");
                return null;
            }
        }
        UUID curId = facade.getCurrentRoomId();
        for (Room r : rooms) {
            if (r.getRoomID().equals(curId)) return r;
        }
        println("Current room not found");
        return null;
    }

    /** Builds a human-readable question/prompt string for a puzzle via reflection. */
    @SuppressWarnings("UseSpecificCatch")
    private static String buildQuestion(Puzzle p) {
        if (p == null) return null;
        try {
            Method m = p.getClass().getMethod("getQuestion");
            Object q = m.invoke(p);
            if (q instanceof String) {
                String s = (String) q;
                if (!s.trim().isEmpty()) return s;
            }
        } catch (Throwable ignored) {}
        try {
            Method m = p.getClass().getMethod("getScrambledWord");
            Object s = m.invoke(p);
            if (s != null) return "Unscramble the letters: " + s;
        } catch (Throwable ignored) {}
        try {
            try {
                Object s = p.getClass().getMethod("getCodePrompt").invoke(p);
                if (s instanceof String) {
                    String str = (String) s;
                    if (!str.trim().isEmpty()) return str;
                }
            } catch (NoSuchMethodException ignore) {}
            try {
                Object s2 = p.getClass().getMethod("getPrompt").invoke(p);
                if (s2 instanceof String) {
                    String str2 = (String) s2;
                    if (!str2.trim().isEmpty()) return str2;
                }
            } catch (NoSuchMethodException ignore) {}
        } catch (Throwable ignored) {}
        String t = safe(p.getTitle());
        return t.isEmpty() ? "Solve the puzzle." : t;
    }

    /** Attempts to read a hint from a puzzle using common getter shapes (reflection). */
    @SuppressWarnings("UseSpecificCatch")
    private static String buildHint(Puzzle p) {
        if (p == null) return null;
        try {
            Method m = p.getClass().getMethod("getHint");
            Object h = m.invoke(p);
            if (h instanceof String) {
                String s = (String) h;
                if (!s.trim().isEmpty()) return s;
            }
        } catch (Throwable ignored) {}
        try {
            Method m = p.getClass().getMethod("getHints");
            Object res = m.invoke(p);
            if (res instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) res;
                if (!list.isEmpty()) {
                    Object first = list.get(0);
                    if (first instanceof String) return (String) first;
                    try {
                        Method gm = first.getClass().getMethod("getText");
                        Object txt = gm.invoke(first);
                        return (txt == null) ? null : String.valueOf(txt);
                    } catch (Throwable ignored2) {
                        return String.valueOf(first);
                    }
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /** Returns true if the given object exposes a method with the specified name. */
    @SuppressWarnings("UseSpecificCatch")
    private static boolean hasMethod(Object obj, String name) {
        try {
            obj.getClass().getMethod(name);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /** Invokes a no-arg getter reflectively and returns its String value, or null on failure. */
    private static String tryGet(Object obj, String getter) {
        try {
            Method m = obj.getClass().getMethod(getter);
            Object v = m.invoke(obj);
            return v == null ? null : String.valueOf(v);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Prompts for a line of text and returns the trimmed input (never null). */
    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        String s = in.nextLine();
        return (s == null) ? "" : s.trim();
    }

    /** Prompts repeatedly until a valid integer is entered; returns -1 if input stream ends. */
    private static int askInt(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine();
            if (s == null) return -1;
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                println("Please enter a number.");
            }
        }
    }

    /** Prints a line to stdout, safely handling nulls. */
    private static void println(String s) { System.out.println(s == null ? "" : s); }

    /** Returns a non-null string; empty string if input is null. */
    private static String safe(String s) { return s == null ? "" : s; }
}
