package com.brewandbite;

import com.brewandbite.model.AppData;
import com.brewandbite.service.*;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        // Bootstrap services
        PersistenceService ps   = new PersistenceService();
        AppData            data = ps.loadData();

        InventoryService   is   = new InventoryService(data.getIngredients());
        MenuService        ms   = new MenuService(data.getMenuItems(), ps, is);
        OrderService       os   = new OrderService(data.getOrders(), is, ps, ms);
        AuthService        as   = new AuthService();

        // Wire session store
        SessionStore session = SessionStore.getInstance();
        session.setPersistenceService(ps);
        session.setInventoryService(is);
        session.setMenuService(ms);
        session.setOrderService(os);
        session.setAuthService(as);

        // Save on exit
        primaryStage.setOnCloseRequest(e -> {
            AppData save = new AppData();
            save.setMenuItems(new ArrayList<>(ms.getAllItems()));
            save.setIngredients(is.getInventoryAsList());
            save.setOrders(new ArrayList<>(os.getOrders()));
            ps.saveData(save);
        });

        SceneManager.switchTo("/com/brewandbite/view/LandingView.fxml", "Brew & Bite");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
