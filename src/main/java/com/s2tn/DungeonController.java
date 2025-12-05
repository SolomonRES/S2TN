package com.s2tn;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.s2tn.model.Facade;
import com.s2tn.model.Puzzle;
import com.s2tn.model.PuzzleState;
import com.s2tn.model.Room;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class DungeonController {

    @FXML private Button btnBackToMap;
    @FXML private Label  lblDungeonName;
    @FXML private Label  lblDifficulty;

    @FXML private ListView<Room> miniMapList;

    @FXML private Label     lblPuzzleTitle;
    @FXML private Label     lblPuzzleState;
    @FXML private TextArea  txtQuestion;
    @FXML private TextField txtAnswer;
    @FXML private Label     lblFeedback;
    @FXML private Button    btnShowHint;
    @FXML private Button    btnSubmit;

    @FXML private ListView<Puzzle> puzzlesListView;

    @FXML private Label            lblRoomStatusTitle;
    @FXML private Label            lblRoomStatus;
    @FXML private ListView<String> inventoryList;

    private final Facade facade = App.getFacade();

    private List<Room> rooms = new ArrayList<>();
    private Room       currentRoom;
    private Puzzle     selectedPuzzle;

    // HINT CYCLING
    private int hintIndex = 0;

    // ICONS
    private Image mapEmoji;
    private Image puzzleEmoji;
    private Image checkEmoji;
    private Image moneyBagEmoji;
    private Image fireEmoji;
    private Image doorEmoji;
    private Image gearEmoji;
    private Image lockEmoji;
    private Image targetEmoji;
    private Image tombEmoji;
    private Image playerAvatar;
    private Image mapLegendAvailable;
    private Image mapLegendCompleted;
    private Image mapLegendLocked;

    @FXML
    private void initialize() {

        loadIconImages();
        wireHeaderIcons();
        configureMiniMapList();
        configurePuzzlesList();

        loadDungeonState();
        refreshRoomStatus();
        refreshInventory();
    }

    // load icons 
    private void loadIconImages() {

        mapEmoji             = loadAsset("mapEmoji.png");
        puzzleEmoji          = loadAsset("puzzleEmoji.png");
        checkEmoji           = loadAsset("checkEmoji.png");
        moneyBagEmoji        = loadAsset("moneyBagEmoji.png");
        fireEmoji            = loadAsset("fireEmoji.png");
        doorEmoji            = loadAsset("doorEmoji.png");
        gearEmoji            = loadAsset("gearEmoji.png");
        lockEmoji            = loadAsset("lockEmoji.png");
        targetEmoji          = loadAsset("targetEmoji.png");
        tombEmoji            = loadAsset("tombEmoji.png");
        playerAvatar         = loadAsset("playerAvatar.png");
        mapLegendAvailable   = loadAsset("mapLegendAvailable.png");
        mapLegendCompleted   = loadAsset("mapLegendCompleted.png");
        mapLegendLocked      = loadAsset("mapLegendLocked.png");
    }


    private void wireHeaderIcons() {
        if (btnBackToMap != null && doorEmoji != null) {
            btnBackToMap.setGraphic(makeIcon(doorEmoji, 18));
        }
        if (lblDungeonName != null && puzzleEmoji != null) {
            lblDungeonName.setGraphic(makeIcon(puzzleEmoji, 18));
        }
        if (lblDifficulty != null && fireEmoji != null) {
            lblDifficulty.setGraphic(makeIcon(fireEmoji, 14));
        }
        if (lblRoomStatusTitle != null && checkEmoji != null) {
            lblRoomStatusTitle.setGraphic(makeIcon(checkEmoji, 14));
        }
    }

    // rooms 
    private void configureMiniMapList() {
        miniMapList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                String name = getRoomDisplayName(item);
                int solved = 0;
                int total  = 0;

                if (item.getPuzzles() != null) {
                    total = item.getPuzzles().size();
                    for (Puzzle p : item.getPuzzles()) {
                        if (p.getState() == PuzzleState.SOLVED) solved++;
                    }
                }

                String label = name;
                if (total > 0) label += String.format("  (%d/%d solved)", solved, total);

                setText(label);
                Image icon = (total > 0 && solved == total) ? checkEmoji : mapEmoji;
                setGraphic(icon != null ? makeIcon(icon, 14) : null);
            }
        });
    }

    // puzzle list 
    private void configurePuzzlesList() {
        puzzlesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Puzzle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                String title = safe(item.getTitle());
                String text  = title + " [" + item.getState() + "]";
                setText(text);

                Image icon = puzzleEmoji;
                String lower = title.toLowerCase(Locale.ROOT);
                if (lower.contains("riddle"))      icon = fireEmoji;
                else if (lower.contains("scramble")) icon = mapEmoji;

                setGraphic(icon != null ? makeIcon(icon, 14) : null);
            }
        });
    }

    // load state 
    private void loadDungeonState() {
        rooms = facade.viewRooms();
        if (rooms == null) rooms = new ArrayList<>();

        miniMapList.getItems().setAll(rooms);

        Room room = ensureCurrentRoomEntered();
        if (room != null) {
            currentRoom = room;
            miniMapList.getSelectionModel().select(currentRoom);
            updateRoomPanel();
        }
    }

    private Room ensureCurrentRoomEntered() {
        if (rooms == null || rooms.isEmpty()) {
            showFeedback("No rooms. Start a dungeon from the map.");
            return null;
        }
        if (facade.getCurrentRoomId() == null) {
            boolean ok = facade.enterRoom(rooms.get(0).getRoomID());
            if (!ok) {
                showFeedback("Could not enter the first room.");
                return null;
            }
        }
        UUID curId = facade.getCurrentRoomId();
        return rooms.stream()
                    .filter(r -> r.getRoomID().equals(curId))
                    .findFirst()
                    .orElse(null);
    }

    // update panels 
    private void updateRoomPanel() {
        if (currentRoom == null) {
            lblDungeonName.setText("Dungeon");
            puzzlesListView.getItems().clear();
            clearPuzzlePanel();
            refreshRoomStatus();
            return;
        }

        lblDungeonName.setText(getRoomDisplayName(currentRoom));

        List<Puzzle> puzzles = currentRoom.getPuzzles();
        puzzlesListView.getItems().setAll(puzzles != null ? puzzles : new ArrayList<>());

        clearPuzzlePanel();
        refreshRoomStatus();
    }

    private void clearPuzzlePanel() {
        selectedPuzzle = null;
        hintIndex = 0;
        lblPuzzleTitle.setText("Select a puzzle");
        lblPuzzleState.setText("");
        txtQuestion.clear();
        txtAnswer.clear();
        lblFeedback.setText("");
    }

    @FXML
    private void onBackToMap(ActionEvent event) {
        facade.exitDungeon();
        App.setRoot("guestmap");
    }

    // select puzzle 
    @FXML
    private void onMiniMapClicked(MouseEvent event) {
        Room selected = miniMapList.getSelectionModel().getSelectedItem();
        if (selected != null && selected != currentRoom) changeRoom(selected);
    }

    private void changeRoom(Room room) {
        if (room == null) return;

        boolean ok = facade.enterRoom(room.getRoomID());
        if (!ok) {
            showFeedback("Could not enter room.");
            return;
        }

        currentRoom = room;
        miniMapList.getSelectionModel().select(room);
        updateRoomPanel();
    }

    @FXML
    private void onPuzzleListClicked(MouseEvent event) {
        Puzzle p = puzzlesListView.getSelectionModel().getSelectedItem();
        if (p != null) selectPuzzle(p);
    }

    private void selectPuzzle(Puzzle p) {
        selectedPuzzle = p;
        hintIndex = 0;  // reset hint cycling

        lblPuzzleTitle.setText(safe(p.getTitle()));
        lblPuzzleState.setText(String.valueOf(p.getState()));

        String q = buildQuestion(p);
        txtQuestion.setText((q == null || q.isBlank()) ? "No question available." : q);

        txtAnswer.clear();
        lblFeedback.setText("");
    }

    // hints 
    @FXML
    private void onShowHint(ActionEvent event) {
        if (selectedPuzzle == null) {
            showFeedback("Select a puzzle first.");
            return;
        }

        List<String> hints = extractHints(selectedPuzzle);

        if (hints.isEmpty()) {
            showFeedback("No hint available.");
            return;
        }

        String hint = hints.get(hintIndex);
        hintIndex = (hintIndex + 1) % hints.size();

        showFeedback("Hint: " + hint);
    }

    @SuppressWarnings("unchecked")
    private List<String> extractHints(Puzzle p) {
        try {
            Method m = p.getClass().getMethod("getHints");
            Object result = m.invoke(p);

            if (result instanceof List<?> list) {
                List<String> out = new ArrayList<>();

                for (Object o : list) {
                    if (o instanceof String s) out.add(s);
                    else {
                        try {
                            Method gm = o.getClass().getMethod("getText");
                            Object txt = gm.invoke(o);
                            if (txt != null) out.add(String.valueOf(txt));
                        } catch (Throwable ignored) {
                            out.add(String.valueOf(o));
                        }
                    }
                }

                return out;
            }

        } catch (Throwable ignored) {}

        return new ArrayList<>();
    }

    // answer 
    @FXML
    private void onSubmitAnswer(ActionEvent event) {
        if (selectedPuzzle == null) {
            showFeedback("Select a puzzle first.");
            return;
        }

        String answer = txtAnswer.getText();
        if (answer == null || answer.trim().isEmpty()) {
            showFeedback("Enter an answer first.");
            return;
        }
        answer = answer.trim();

        UUID pid = selectedPuzzle.getPuzzleID();
        String titleLower = safe(selectedPuzzle.getTitle()).toLowerCase(Locale.ROOT);

        boolean ok;
        if (hasMethod(selectedPuzzle, "getQuestion") || titleLower.contains("riddle")) {
            ok = facade.answerRiddle(pid, answer);
        } else if (hasMethod(selectedPuzzle, "getScrambledWord") || titleLower.contains("scramble")) {
            ok = facade.answerScramble(pid, answer);
        } else {
            ok = facade.attemptCodePuzzle(pid, answer);
        }

        lblFeedback.setText(ok ? "✔ Correct! Puzzle solved."
                               : "✖ Incorrect answer or missing item.");

        puzzlesListView.refresh();
        lblPuzzleState.setText(String.valueOf(selectedPuzzle.getState()));
        refreshRoomStatus();
        refreshInventory();
    }

    // status of room and inventory
    private void refreshRoomStatus() {
        if (currentRoom == null || currentRoom.getPuzzles() == null) {
            lblRoomStatus.setText("No room selected.");
            return;
        }
        int total = currentRoom.getPuzzles().size();
        int solved = (int) currentRoom.getPuzzles()
                .stream()
                .filter(p -> p.getState() == PuzzleState.SOLVED)
                .count();

        lblRoomStatus.setText("Solved " + solved + " / " + total);
    }

    private void refreshInventory() {
        try {
            List<String> keys = facade.getInventoryKeys();
            inventoryList.getItems().setAll(keys != null ? keys : new ArrayList<>());
        } catch (Throwable ignored) {
            inventoryList.getItems().clear();
        }
    }
    
    // room name 
    private String getRoomDisplayName(Room room) {
        if (room == null) return "Unknown Room";

        String explicit = tryGet(room, "getName");
        if (explicit == null || explicit.isBlank()) {
            explicit = tryGet(room, "getDisplayName");
        }
        if (explicit != null && !explicit.isBlank()) return explicit;

        int riddleCount = 0, scrambleCount = 0, codeCount = 0;

        if (room.getPuzzles() != null) {
            for (Puzzle p : room.getPuzzles()) {
                String t = safe(p.getTitle()).toLowerCase(Locale.ROOT);

                if (hasMethod(p, "getQuestion") || t.contains("riddle")) {
                    riddleCount++;
                } else if (hasMethod(p, "getScrambledWord") || t.contains("scramble")) {
                    scrambleCount++;
                } else {
                    codeCount++;
                }
            }
        }

        String theme;
        if (riddleCount >= scrambleCount && riddleCount >= codeCount && riddleCount > 0) theme = "riddle";
        else if (scrambleCount >= riddleCount && scrambleCount >= codeCount && scrambleCount > 0) theme = "scramble";
        else if (codeCount > 0) theme = "code";
        else theme = "mixed";

        int index = rooms.indexOf(room);
        if (index < 0) index = 0;

        switch (theme) {
            case "riddle":
                return new String[]{"Hall of Torches", "Chamber of Echoes", "Torchlight Hall"}[index % 3];
            case "scramble":
                return new String[]{"Vault Sigil Room", "Cipher Antechamber", "Arcane Glyph Vault"}[index % 3];
            case "code":
                return new String[]{"Sentinel Control Room", "Ancient Lock Chamber", "Mechanism Gallery"}[index % 3];
            default:
                return new String[]{"Puzzle Antechamber", "Trial Nexus", "Forgotten Gallery"}[index % 3];
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private String buildQuestion(Puzzle p) {
        if (p == null) return null;
        try {
            Method m = p.getClass().getMethod("getQuestion");
            Object q = m.invoke(p);
            if (q instanceof String s && !s.trim().isEmpty()) return s;
        } catch (Throwable ignored) {}

        try {
            Method m = p.getClass().getMethod("getScrambledWord");
            Object s = m.invoke(p);
            if (s != null) return "Unscramble the letters: " + s;
        } catch (Throwable ignored) {}

        try {
            try {
                Object s = p.getClass().getMethod("getCodePrompt").invoke(p);
                if (s instanceof String str && !str.trim().isEmpty()) return str;
            } catch (NoSuchMethodException ignore) {}

            try {
                Object s2 = p.getClass().getMethod("getPrompt").invoke(p);
                if (s2 instanceof String str2 && !str2.trim().isEmpty()) return str2;
            } catch (NoSuchMethodException ignore) {}
        } catch (Throwable ignored) {}

        String t = safe(p.getTitle());
        return t.isEmpty() ? "Solve the puzzle." : t;
    }

    // helper methods
    private boolean hasMethod(Object obj, String name) {
        try {
            obj.getClass().getMethod(name);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private String tryGet(Object obj, String getter) {
        try {
            Method m = obj.getClass().getMethod(getter);
            Object v = m.invoke(obj);
            return v == null ? null : String.valueOf(v);
        } catch (Throwable t) {
            return null;
        }
    }

    private String safe(Object o) {
        return (o == null) ? "" : String.valueOf(o);
    }

    private void showFeedback(String msg) {
        lblFeedback.setText(msg == null ? "" : msg);
    }

    private Image loadAsset(String file) {
        try {
            return new Image(getClass().getResourceAsStream("/com/s2tn/assets/" + file));
        } catch (Exception e) {
            System.err.println("Missing asset: " + file);
            return null;
        }
    }

    private ImageView makeIcon(Image img, double size) {
        if (img == null) return null;
        ImageView view = new ImageView(img);
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        return view;
    }
}
