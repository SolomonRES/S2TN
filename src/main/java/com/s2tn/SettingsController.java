package com.s2tn;

import com.s2tn.model.Account;
import com.s2tn.model.DataWriter;
import com.s2tn.model.Facade;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class SettingsController {

    @FXML private Slider sliderMasterVolume;
    @FXML private CheckBox chkTextToSpeech;
    @FXML private Button btnchangePassword;
    @FXML private Button btnResetData;
    @FXML private Button btnExportData;
    @FXML private Button btnDeleteAccount;

    private final Facade facade = App.getFacade();

    @FXML
    private void initialize() {
        chkTextToSpeech.setSelected(Speak.isEnabled());

        chkTextToSpeech.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != Speak.isEnabled()) {
                Speak.toggle();
            }

            if (Speak.isEnabled()) {
                Speak.speak("Voice enabled.");
            } else {
                Speak.toggle();
                Speak.speak("Voice disabled.");
                Speak.toggle();
            }
        });
    }

    @FXML
    private void onBack(ActionEvent e) {
        App.transitionTo("guestmap");
    }

    @FXML
    private void onReset(ActionEvent e) {
        Account user = facade.getCurrentUser();
        if(user == null){
            return;
        }
        Alert reseting = new Alert(Alert.AlertType.WARNING);
        reseting.setTitle("WARNING");
        reseting.setHeaderText("You are about to rest your data");
        reseting.setContentText("Are you sure you want to reset your data?");

        ButtonType okButton = new ButtonType("Ok");
        ButtonType cancelButton = new ButtonType("Cancel");

        reseting.getButtonTypes().setAll(okButton,cancelButton);

        Optional<ButtonType> result = reseting.showAndWait();
        if(result.isPresent() && result.get() == okButton){
            System.out.println("Delete data");
            user.setScore(0);
            user.setAchievements(new ArrayList<>());
            DataWriter writer = new DataWriter();
            writer.saveUsers();
            App.transitionTo("guestmap");
        }
    }

    @FXML
    private void onExport(ActionEvent e) {
        Account user = facade.getCurrentUser();
        if(user == null){
            return;
        }
        DataWriter writer = new DataWriter();
        String out = DataWriter.userToJson(user);
        Path writeTo = Paths.get( "export",user.getUserName() + "-export.txt");
        try{
            Files.write(writeTo, Collections.singleton(out), StandardCharsets.UTF_8);
        } catch (IOException fileError){
            System.out.println("Failed" + fileError);
            return;
        }
    }

    @FXML
    private void onDelete(ActionEvent e) {
        Account user = facade.getCurrentUser();
        if(user == null){
            return;
        }
        Alert deleteing = new Alert(Alert.AlertType.WARNING);
        deleteing.setTitle("WARNING");
        deleteing.setHeaderText("You are about to delete your account");
        deleteing.setContentText("Are you sure you want to delete your account?");

        ButtonType okButton = new ButtonType("Ok");
        ButtonType cancelButton = new ButtonType("Cancel");

        deleteing.getButtonTypes().setAll(okButton,cancelButton);

        Optional<ButtonType> result = deleteing.showAndWait();
        if(result.isPresent() && result.get() == okButton){
            user.setAccountID(null);
            user.setScore(0);
            user.setUserName(null);
            user.setRank(0);
            user.setPassword(null);
            user = null;
            //I don't think the backend actually has a way to delete a user
            DataWriter writer = new DataWriter();
            writer.saveUsers();
            App.transitionTo("landing");
        }
    }
}
