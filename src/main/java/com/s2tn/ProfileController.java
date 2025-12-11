package com.s2tn;

import com.s2tn.model.Account;
import com.s2tn.model.Achievement;
import com.s2tn.model.Facade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProfileController {

    @FXML private Label playerScore;
    @FXML private Label completion;
    @FXML private Label rank;
    @FXML private Label name;
    @FXML private Label title;
    @FXML private ListView achivementList;
    @FXML private TableView<Account> leaderboard;
    @FXML private TableColumn<Account, Integer> score;
    @FXML private TableColumn<Account, String> boardName;
    @FXML private TableColumn<Account, Integer> ranking;

    private final Facade facade = App.getFacade();

    @FXML
    private void initialize(){
        boardName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        score.setCellValueFactory(new PropertyValueFactory<>("score"));

        Account user = facade.getCurrentUser();
        if (user != null) {
            name.setText(user.getUserName());
            title.setText("Adventurer");
            playerScore.setText(String.valueOf(user.getScore()));
            rank.setText("UNK"); // Change
            completion.setText(String.valueOf("0"));
            for(Achievement a : user.getAchievements()) {
                achivementList.getItems().add(a.getName());
            }
            if(achivementList.getItems().isEmpty()){
                achivementList.getItems().add("No Achievements yet");
            }
        } else {
            name.setText("Guest Explorer");
            title.setText("Visitor");
            playerScore.setText("0");
            rank.setText("UNK"); // Change
            completion.setText(String.valueOf("0"));
            achivementList.getItems().add("No Achievements yet");
        }

        leaderboard.getItems().setAll(facade.getTopPlayers(10));
    }

    @FXML
    private void onBack(ActionEvent e) {
        App.transitionTo("guestmap");
    }
}
