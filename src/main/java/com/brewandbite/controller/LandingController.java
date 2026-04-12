package com.brewandbite.controller;

import com.brewandbite.model.UserRole;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LandingController {

    @FXML private Button customerBtn;
    @FXML private Button staffBtn;

    @FXML
    private void handleCustomer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Customer Name");
        dialog.setHeaderText("Welcome to Brew & Bite!");
        dialog.setContentText("Please enter your name:");
        dialog.showAndWait().ifPresent(name -> {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) {
                showStatus("Name cannot be empty.");
                return;
            }
            SessionStore.getInstance().setCurrentRole(UserRole.CUSTOMER);
            SessionStore.getInstance().setCurrentUserName(trimmed);
            SceneManager.switchTo("/com/brewandbite/view/CustomerView.fxml",
                                  "Brew & Bite — Order");
        });
    }

    @FXML
    private void handleStaffLogin() {
        SceneManager.switchTo("/com/brewandbite/view/LoginView.fxml",
                              "Brew & Bite — Staff Login");
    }

    //  Helpers

    private void showStatus(String msg) {
        Alert alert = new Alert(Alert.AlertType.NONE, msg, ButtonType.OK);
        alert.setTitle("Notice");
        alert.showAndWait();
    }
}
