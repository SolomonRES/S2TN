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

    @FXML private Label totalDungeonsLabel;
    @FXML private Label completedLabel;
    @FXML private Label remainingLabel;
    @FXML private Button statusPill;

    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;

    // top-right icons
    @FXML private Button btnInventory;
    @FXML private Button btnSettings;
    @FXML private Button btnLogout;

    // Map nodes
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
    private List<Dungeon> dungeonList;

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
        ImageView view = new ImageView(img);
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        return view;
    }

    private void loadImages() {

        // background map
        Image mp = loadAsset("map.png");
        if (mp != null) mapImage.setImage(mp);

        ImageView trophy = makeIcon("trophyEmoji.png", 18);
        if (trophy != null) statusPill.setGraphic(trophy);

        // Top-right icons
        ImageView inventory = makeIcon("moneyBagEmoji.png", 22);
        if (inventory != null) btnInventory.setGraphic(inventory);

        ImageView settings = makeIcon("gearEmoji.png", 22);
        if (settings != null) btnSettings.setGraphic(settings);

        ImageView logout = makeIcon("doorEmoji.png", 22);
        if (logout != null) btnLogout.setGraphic(logout);

        Image fire = loadAsset("fireEmoji.png");
        if (fire != null) imgLevel1.setImage(fire);

        Image money = loadAsset("moneyBagEmoji.png");
        if (money != null) imgLevel2.setImage(money);

        Image tomb = loadAsset("tombEmoji.png");
        if (tomb != null) imgLevel3.setImage(tomb);
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
        int completed = 0; // future: connect to actual progress
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

        btn.getStyleClass().removeAll(
                "map-node-available",
                "map-node-inprogress",
                "map-node-completed",
                "map-node-locked"
        );

        if (index < dungeonList.size()) {

            Dungeon d = dungeonList.get(index);

            nameLbl.setText(d.getName());

            String diff = d.getDifficulty() != null
                    ? d.getDifficulty().toString()
                    : "NORMAL";
            diffLbl.setText(toTitle(diff));

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
        UUID id = d.getUUID();

        if (facade.startDungeon(id)) {
            selectedIndex = index;
            updateSelectionHighlight();

            if (facade.enterDungeon()) {
                System.out.println("Entering Room: " + facade.getCurrentRoomId());
                // TODO: navigate to puzzle/game page here
            }
        }
    }

    private void updateSelectionHighlight() {
        Button[] arr = { btnLevel1, btnLevel2, btnLevel3 };

        for (int i = 0; i < arr.length; i++) {
            Button b = arr[i];
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

    @FXML private void onSettingsClicked(ActionEvent e) { System.out.println("Settings clicked"); }
    @FXML private void onInventoryClicked(ActionEvent e) { System.out.println("Inventory clicked"); }
    @FXML private void onProfileClicked(ActionEvent e) { System.out.println("Profile clicked"); }

    @FXML
    private void onLogoutClicked(ActionEvent e) {
        facade.logout();
        App.setRoot("landing");
    }
}