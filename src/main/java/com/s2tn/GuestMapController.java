package com.s2tn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.s2tn.model.Account;
import com.s2tn.model.Dungeon;
import com.s2tn.model.Facade;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GuestMapController {


    @FXML private ImageView mapImage;

    // Stats
    @FXML private Label totalDungeonsLabel;
    @FXML private Label completedLabel;
    @FXML private Label remainingLabel;
    @FXML private Button statusPill;

    // User section
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;

    // Top-right buttons
    @FXML private Button btnInventory;
    @FXML private Button btnSettings;
    @FXML private Button btnLogout;
    @FXML private Button btnProfile;

    // Dungeon Map Nodes
    @FXML private Button btnLevel1;
    @FXML private Label lblLevel1Name;
    @FXML private Label lblLevel1Diff;
    @FXML private ImageView imgLevel1;

    @FXML private Button btnLevel2;
    @FXML private Label lblLevel2Name;
    @FXML private Label lblLevel2Diff;
    @FXML private ImageView imgLevel2;

    @FXML private Button btnLevel3;
    @FXML private Label lblLevel3Name;
    @FXML private Label lblLevel3Diff;
    @FXML private ImageView imgLevel3;

    private final Facade facade = App.getFacade();
    private List<Dungeon> dungeonList = new ArrayList<>();
    private int selectedIndex = -1;

    @FXML
    public void initialize() {
        loadImages();
        updateUserProfile();
        refreshDungeonData();
    }

    private Image loadAsset(String file) {
        try {
            return new Image(getClass().getResourceAsStream("/com/s2tn/assets/" + file));
        } catch (Exception e) {
            System.err.println("Missing asset: " + file);
            return null;
        }
    }

    private ImageView makeIcon(String file, double size) {
        Image img = loadAsset(file);
        if (img == null) return null;
        ImageView v = new ImageView(img);
        v.setFitWidth(size);
        v.setFitHeight(size);
        v.setPreserveRatio(true);
        return v;
    }

    private void loadImages() {

        // map background
        Image mp = loadAsset("map.png");
        if (mp != null) mapImage.setImage(mp);

        // stats/leaderboard icon
        ImageView trophy = makeIcon("trophyEmoji.png", 18);
        if (trophy != null) statusPill.setGraphic(trophy);

        // Top-right icons
        ImageView inventory = makeIcon("moneyBagEmoji.png", 22);
        if (inventory != null) btnInventory.setGraphic(inventory);

        ImageView settings = makeIcon("gearEmoji.png", 22);
        if (settings != null) btnSettings.setGraphic(settings);

        ImageView logout = makeIcon("doorEmoji.png", 22);
        if (logout != null) btnLogout.setGraphic(logout);

        ImageView profile = makeIcon("playerAvatar.png", 22);
        if(profile != null) btnProfile.setGraphic(profile);

        // dungeon icons
        if (imgLevel1 != null) imgLevel1.setImage(loadAsset("fireEmoji.png"));
        if (imgLevel2 != null) imgLevel2.setImage(loadAsset("moneyBagEmoji.png"));
        if (imgLevel3 != null) imgLevel3.setImage(loadAsset("tombEmoji.png"));
    }


    private void updateUserProfile() {
        Account user = facade.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getUserName());
            userRoleLabel.setText("Adventurer");
        } else {
            userNameLabel.setText("Guest Explorer");
            userRoleLabel.setText("Visitor");
        }
    }

    private void refreshDungeonData() {
        dungeonList = facade.listDungeons();
        if (dungeonList == null) dungeonList = new ArrayList<>();

        int total = dungeonList.size();

        int completed = 0;
        try {
            completed = facade.getCompletedDungeonCount();
        } catch (Exception ignored) {}

        int remaining = total - completed;

        totalDungeonsLabel.setText(String.valueOf(total));
        completedLabel.setText(String.valueOf(completed));
        remainingLabel.setText(String.valueOf(remaining));

        statusPill.setText(completed + "/" + total + " Completed");

        configureNode(0, btnLevel1, lblLevel1Name, lblLevel1Diff);
        configureNode(1, btnLevel2, lblLevel2Name, lblLevel2Diff);
        configureNode(2, btnLevel3, lblLevel3Name, lblLevel3Diff);

        updateSelectionHighlight();
    }

    private void configureNode(int index, Button btn, Label nameLbl, Label diffLbl) {

        btn.getStyleClass().removeAll("map-node-available", "map-node-inprogress",
                "map-node-completed", "map-node-locked");

        if (index < dungeonList.size()) {

            Dungeon d = dungeonList.get(index);

            nameLbl.setText(d.getName());
            String diff = d.getDifficulty() == null ? "Normal" : toTitle(d.getDifficulty().toString());
            diffLbl.setText(diff);

            btn.setDisable(false);
            btn.getStyleClass().add("map-node-available");

        } else {
            nameLbl.setText("Locked");
            diffLbl.setText("---");

            btn.setDisable(true);
            btn.getStyleClass().add("map-node-locked");
        }
    }

    private String toTitle(String raw) {
        raw = raw.toLowerCase();
        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
    }

    private void attemptEnterDungeon(int index) {
        if (index >= dungeonList.size()) return;

        Dungeon d = dungeonList.get(index);
        UUID dungeonId = d.getUUID();

        // Start dungeon session
        if (!facade.startDungeon(dungeonId)) {
            System.err.println("Failed to start dungeon: " + dungeonId);
            return;
        }

        selectedIndex = index;
        updateSelectionHighlight();

        // Enter first room
        if (!facade.enterDungeon()) {
            System.err.println("facade.enterDungeon() failed");
            return;
        }

        UUID roomId = facade.getCurrentRoomId();
        System.out.println("Entering Room: " + roomId);

        App.transitionTo("dungeon");
    }

    private void updateSelectionHighlight() {
        Button[] btns = { btnLevel1, btnLevel2, btnLevel3 };

        for (int i = 0; i < btns.length; i++) {
            Button b = btns[i];
            if (b == null) continue;

            b.getStyleClass().remove("map-node-selected");
            if (i == selectedIndex && !b.isDisabled()) {
                b.getStyleClass().add("map-node-selected");
            }
        }
    }

    @FXML private void onLevel1Clicked(ActionEvent e) { attemptEnterDungeon(0); }
    @FXML private void onLevel2Clicked(ActionEvent e) { attemptEnterDungeon(1); }
    @FXML private void onLevel3Clicked(ActionEvent e) { attemptEnterDungeon(2); }

    @FXML private void onSettingsClicked(ActionEvent e) {
        App.transitionTo("settings");
    }

    @FXML private void onInventoryClicked(ActionEvent e) {
        App.transitionTo("inventory");
    }

    @FXML private void onLogoutClicked(ActionEvent e) {
        facade.logout();
        App.transitionTo("landing");
    }
    @FXML private void onProfileClicked(ActionEvent e){
        App.transitionTo("profile");
    }
}