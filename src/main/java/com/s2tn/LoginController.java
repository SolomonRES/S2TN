package com.s2tn;

import com.s2tn.model.Facade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController {

    @FXML private ImageView logoImage;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final Facade facade = App.getFacade();

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/com/s2tn/assets/logo.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.err.println("Logo not found.");
        }
    }

    @FXML
    private void onLoginClicked(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (facade.login(user, pass)) {
            System.out.println("Login Successful for: " + user);
            // Go to the map view (same as guest, but now Facade has the user stored)
            App.setRoot("guestmap");
        } else {
            errorLabel.setText("Invalid username or password.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void onGuestClicked(ActionEvent event) {
        App.setRoot("guestmap");
    }

    @FXML
    private void onSignUpClicked(ActionEvent event) {
        System.out.println("Navigate to Sign Up...");
        App.setRoot("signup");
    }

    @FXML
    private void onForgotPasswordClicked(ActionEvent event) {
        System.out.println("Forgot password clicked");
        // App.setRoot("forgotpassword"); // Future implementation
    }

    @FXML
    private void onBackToHomeClicked(ActionEvent event) {
        App.setRoot("landing");
    }
}