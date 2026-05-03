package com.brewandbite.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Opens additional JavaFX windows (Stages) without replacing the primary scene.
 * Also provides a clamp method to keep windows within the visible screen area.
 *
 * Key behavior: clamp is deferred to the next JavaFX pulse to avoid
 * "controls only appear after moving/resizing the window" issues.
 */
public final class WindowManager {

    private WindowManager() {}

    public static Stage openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            String css = "/com/brewandbite/css/style.css";
            if (WindowManager.class.getResource(css) != null) {
                scene.getStylesheets().add(WindowManager.class.getResource(css).toExternalForm());
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
            Platform.runLater(() -> clampStageToVisibleBounds(stage));

            return stage;
        } catch (IOException e) {
            throw new RuntimeException("Cannot open window: " + fxmlPath, e);
        }
    }

    public static void clampStageToVisibleBounds(Stage stage) {
        if (stage == null) return;

        Rectangle2D vb = Screen.getPrimary().getVisualBounds();

        stage.sizeToScene();

        double w = Math.min(stage.getWidth(), vb.getWidth());
        double h = Math.min(stage.getHeight(), vb.getHeight());

        stage.setWidth(w);
        stage.setHeight(h);

        stage.setX(vb.getMinX() + (vb.getWidth() - w) / 2.0);
        stage.setY(vb.getMinY() + (vb.getHeight() - h) / 2.0);

        stage.setMaxWidth(vb.getWidth());
        stage.setMaxHeight(vb.getHeight());
    }
}