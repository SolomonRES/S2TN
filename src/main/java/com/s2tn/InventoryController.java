package com.s2tn;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class InventoryController {

    @FXML private ListView<String> inventoryList;
    @FXML private Label lblItemName;
    @FXML private TextArea txtItemDescription;
    @FXML private Button btnUseItem;

    @FXML
    private void initialize() {
        List<String> inv = App.getFacade().getInventoryKeys();
        inventoryList.getItems().setAll(inv);

        inventoryList.setOnMouseClicked(e -> {
            String selected = inventoryList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                lblItemName.setText(selected);
                txtItemDescription.setText(getDescription(selected));
                btnUseItem.setDisable(false);
            }
        });
    }

    private String getDescription(String item) {
        return "A mysterious item: " + item + "\n\nUsed in puzzles or unlocking rooms.";
    }

    @FXML
    private void onUseItem() {
        String selected = inventoryList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtItemDescription.setText("You used: " + selected);
        }
    }

    @FXML
    private void onBack() {
        App.transitionTo("guestmap");
    }
}
