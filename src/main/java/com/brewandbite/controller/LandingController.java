package com.brewandbite.controller;

import com.brewandbite.model.UserRole;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import com.brewandbite.util.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class LandingController {

    @FXML
    private void handleCustomerLogin() {
        // Prompt for customer name (restores previous behavior)
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Customer");
        dialog.setHeaderText("Enter your name");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String name = result.get().trim();
        if (name.isBlank()) return;

        SessionStore.getInstance().setCurrentRole(UserRole.CUSTOMER);
        SessionStore.getInstance().setCurrentUserName(name);

        // Use the view your app expects customers to go to
        SceneManager.switchTo("/com/brewandbite/view/CustomerView.fxml", "Brew & Bite — Customer");
    }

    @FXML
    private void handleStaffLogin() {
        SessionStore.getInstance().setCurrentRole(UserRole.BARISTA);
        SceneManager.switchTo("/com/brewandbite/view/LoginView.fxml", "Brew & Bite — Staff Login");
    }

    @FXML
    private void handleOpenBaristaWindow() {
        SessionStore.getInstance().setCurrentRole(UserRole.BARISTA);
        WindowManager.openWindow("/com/brewandbite/view/BaristaView.fxml",
                "Brew & Bite — Barista (Test Window)");
    }

    @FXML
    private void handleOpenManagerWindow() {
        SessionStore.getInstance().setCurrentRole(UserRole.MANAGER);
        WindowManager.openWindow("/com/brewandbite/view/ManagerView.fxml",
                "Brew & Bite — Manager (Test Window)");
    }
}