package com.brewandbite.controller;

import com.brewandbite.model.UserRole;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         statusLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Please enter both username and password.");
            return;
        }

        UserRole role = SessionStore.getInstance()
                                    .getAuthService()
                                    .authenticate(username, password);
        if (role == null) {
            setStatus("Invalid credentials. Please try again.");
            passwordField.clear();
            return;
        }

        SessionStore.getInstance().setCurrentRole(role);
        SessionStore.getInstance().setCurrentUserName(username);

        if (role == UserRole.BARISTA) {
            SceneManager.switchTo("/com/brewandbite/view/BaristaView.fxml",
                                  "Brew & Bite — Barista");
        } else {
            SceneManager.switchTo("/com/brewandbite/view/ManagerView.fxml",
                                  "Brew & Bite — Manager");
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo("/com/brewandbite/view/LandingView.fxml", "Brew & Bite");
    }

    // Helpers

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }
}
