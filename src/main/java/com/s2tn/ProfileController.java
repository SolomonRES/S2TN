package com.s2tn;

import com.s2tn.model.Account;
import com.s2tn.model.Facade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ProfileController {

    @FXML private Label playerScore;
    @FXML private Label completion;
    @FXML private Label rank;
    @FXML private Label name;
    @FXML private Label title;
    @FXML private ListView achivementList;
    @FXML private ListView leaderboard;

    private final Facade facade = App.getFacade();

    @FXML
    private void initialize(){
        Account user = facade.getCurrentUser();
        if (user != null) {
            name.setText(user.getUserName());
            title.setText("Adventurer");
            playerScore.setText(String.valueOf(user.getScore()));
            rank.setText("UNK"); // Change
            completion.setText(String.valueOf("0"));
        } else {
            name.setText("Guest Explorer");
            title.setText("Visitor");
            playerScore.setText("0");
            rank.setText("UNK"); // Change
            completion.setText(String.valueOf("0"));
        }
    }

    @FXML
    private void onBack(ActionEvent e) {
        App.transitionTo("guestmap");
    }
}
