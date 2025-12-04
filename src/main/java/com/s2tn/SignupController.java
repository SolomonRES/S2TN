package com.s2tn;

import com.s2tn.model.Facade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SignupController {

    @FXML private ImageView logoImage;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox termsCheckbox;
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
    private void onCreateAccountClicked(ActionEvent event) {
        String email = emailField.getText();
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (user.isBlank() || pass.isBlank()) {
            showError("Username and password are required.");
            return;
        }

        if (!pass.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        if (!termsCheckbox.isSelected()) {
            showError("You must agree to the Terms and Conditions.");
            return;
        }

        // Register with Facade
        boolean success = facade.register(user, pass);

        if (success) {
            System.out.println("Account created for: " + user + " (Email: " + email + ")");
            
            // Auto-login after signing up (Go straight to map)
            facade.login(user, pass);
            
            App.setRoot("guestmap");
        } else {
            showError("Username already exists or invalid.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void onGuestClicked(ActionEvent event) {
        App.setRoot("guestmap");
    }

    @FXML
    private void onSignInClicked(ActionEvent event) {
        App.setRoot("login");
    }

    @FXML
    private void onBackToHomeClicked(ActionEvent event) {
        App.setRoot("landing");
    }
}