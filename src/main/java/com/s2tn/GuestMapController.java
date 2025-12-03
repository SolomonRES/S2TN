package com.s2tn;

import com.s2tn.model.Account;
import com.s2tn.model.Dungeon;
import com.s2tn.model.Facade;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuestMapController {

    @FXML private ImageView mapImage;

    @FXML private Label totalDungeonsLabel;
    @FXML private Label completedLabel;
    @FXML private Label remainingLabel;
    @FXML private Button statusPill;

    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;

    @FXML private Button btnLevel1;
    @FXML private Label lblLevel1Name;
    @FXML private Label lblLevel1Diff;

    @FXML private Button btnLevel2;
    @FXML private Label lblLevel2Name;
    @FXML private Label lblLevel2Diff;

    @FXML private Button btnLevel3;
    @FXML private Label lblLevel3Name;
    @FXML private Label lblLevel3Diff;

    private final Facade facade = App.getFacade();
    private List<Dungeon> dungeonList;

    @FXML
    public void initialize() {

        loadImages();

        updateUserProfile();

        refreshDungeonData();
    }

    private void loadImages() {
        try {
            Image map = new Image(getClass().getResourceAsStream("/com/s2tn/assets/map.png"));
            mapImage.setImage(map);
            
        } catch (Exception e) {
            System.err.println("Asset missing: " + e.getMessage());
        }
    }

    private void updateUserProfile() {
        Account user = facade.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getUserName());
            userRoleLabel.setText("Adventurer"); // Could be dynamic based on score
        } else {
            userNameLabel.setText("Guest Explorer");
            userRoleLabel.setText("Visitor");
        }
    }

    private void refreshDungeonData() {
        dungeonList = facade.listDungeons();

        if (dungeonList == null) {
            dungeonList = new ArrayList<>(); // if backend fails, this will stopit from crashing
        }

        int total = dungeonList.size();
        int completed = 0; 
        int remaining = total - completed;

        totalDungeonsLabel.setText(String.valueOf(total));
        completedLabel.setText(String.valueOf(completed));
        remainingLabel.setText(String.valueOf(remaining));
        statusPill.setText(String.format("🏆 %d/%d Completed", completed, total));

        configureNode(0, btnLevel1, lblLevel1Name, lblLevel1Diff);
        configureNode(1, btnLevel2, lblLevel2Name, lblLevel2Diff);
        configureNode(2, btnLevel3, lblLevel3Name, lblLevel3Diff);
    }

    private void configureNode(int index, Button btn, Label nameLbl, Label diffLbl) {
        if (index < dungeonList.size()) {
            Dungeon d = dungeonList.get(index);
            nameLbl.setText(d.getName());
            
            String diff = (d.getDifficulty() != null) ? d.getDifficulty().toString() : "NORMAL";
            diffLbl.setText(diff.charAt(0) + diff.substring(1).toLowerCase()); // Title Case
            
            btn.setDisable(false);
        } else {

            nameLbl.setText("Locked");
            diffLbl.setText("---");
            btn.setDisable(true);
            btn.setStyle("-fx-opacity: 0.5;");
        }
    }

    private void attemptEnterDungeon(int index) {
        if (index >= dungeonList.size()) return;

        Dungeon selected = dungeonList.get(index);
        UUID id = selected.getUUID();

        // Using facade to start
        if (facade.startDungeon(id)) {
            System.out.println("Selected Dungeon: " + selected.getName());
            
            if (facade.enterDungeon()) {
                System.out.println("Entering Room: " + facade.getCurrentRoomId());
                // Uncomment when we start the game view
                // App.setRoot("game_view"); 
            } else {
                System.err.println("Error: Could not enter starting room.");
            }
        }
    }
    
    @FXML
    private void onLevel1Clicked(ActionEvent event) {
        attemptEnterDungeon(0);
    }

    @FXML
    private void onLevel2Clicked(ActionEvent event) {
        attemptEnterDungeon(1);
    }

    @FXML
    private void onLevel3Clicked(ActionEvent event) {
        attemptEnterDungeon(2);
    }

    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Settings clicked");
    }

    @FXML 
    private void onInventoryClicked(ActionEvent event) {
        System.out.println("Inventory clicked");
    }

    @FXML
    private void onProfileClicked(ActionEvent event) {
        System.out.println("Profile clicked");
    }

    @FXML
    private void onLogoutClicked(ActionEvent event) {
        facade.logout();
        App.setRoot("landing");
    }
}

