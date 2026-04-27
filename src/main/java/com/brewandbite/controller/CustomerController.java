package com.brewandbite.controller;

import com.brewandbite.model.*;
import com.brewandbite.service.InventoryService;
import com.brewandbite.service.MenuService;
import com.brewandbite.service.OrderService;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import com.brewandbite.model.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.brewandbite.notification.OrderEvent;
import com.brewandbite.notification.OrderObserver;
import javafx.application.Platform;

public class CustomerController implements OrderObserver {

    // FXML nodes
    @FXML private Label          welcomeLabel;
    @FXML private VBox           beverageList;
    @FXML private VBox           pastryList;
    @FXML private ListView<String> orderListView;
    @FXML private Label          totalLabel;
    @FXML private Label          statusLabel;

    // State 
    private Order            currentOrder;
    private MenuService      menuService;
    private OrderService     orderService;
    private InventoryService inventoryService;

    // Lifecycle

    @FXML
    public void initialize() {
        SessionStore s = SessionStore.getInstance();
        menuService      = s.getMenuService();
        orderService     = s.getOrderService();
        orderService.addObserver(this);
        inventoryService = s.getInventoryService();

        welcomeLabel.setText("Welcome, " + s.getCurrentUserName() + "!");
        currentOrder = new Order(s.getCurrentUserName());
        refreshMenu();
    }

    // Menu rendering

    private void refreshMenu() {
        beverageList.getChildren().clear();
        pastryList.getChildren().clear();

        for (MenuItem item : menuService.getBeverages()) {
            beverageList.getChildren().add(buildBeverageCard((Beverage) item));
        }
        for (MenuItem item : menuService.getPastries()) {
            pastryList.getChildren().add(buildPastryCard((Pastry) item));
        }
    }

    private VBox buildBeverageCard(Beverage bev) {
        VBox card = new VBox(6);
        card.getStyleClass().add("menu-card");

        // Title
        Label nameLabel = new Label(bev.getName());
        nameLabel.getStyleClass().add("card-title");

        // Size selector
        HBox sizeRow = new HBox(8);
        sizeRow.getChildren().add(new Label("Size:"));
        ToggleGroup sizeGroup = new ToggleGroup();
        List<RadioButton> sizeButtons = new ArrayList<>();
        for (Map.Entry<String, Double> entry : bev.getSizePrices().entrySet()) {
            RadioButton rb = new RadioButton(
                    entry.getKey() + " (USD " + String.format("%.0f", entry.getValue()) + ")");
            rb.setToggleGroup(sizeGroup);
            rb.setUserData(entry.getKey());
            sizeRow.getChildren().add(rb);
            sizeButtons.add(rb);
        }
        if (!sizeButtons.isEmpty()) sizeButtons.get(0).setSelected(true);

        // Customisations
        VBox customBox = new VBox(4);
        if (!bev.getAvailableCustomizations().isEmpty()) {
            customBox.getChildren().add(new Label("Extras:"));
        }
        List<CheckBox> customChecks = new ArrayList<>();
        for (Customization c : bev.getAvailableCustomizations()) {
            CheckBox cb = new CheckBox(
                    c.getName() + " (+USD " + String.format("%.0f", c.getExtraCost()) + ")");
            cb.setUserData(c);
            customBox.getChildren().add(cb);
            customChecks.add(cb);
        }

        // Quantity + Add button
        HBox actionRow = new HBox(8);
        actionRow.getChildren().add(new Label("Qty:"));
        Spinner<Integer> qty = new Spinner<>(1, 10, 1);
        qty.setPrefWidth(75);
        Button addBtn = new Button("Add to Order");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> {
            Toggle selected = sizeGroup.getSelectedToggle();
            if (selected == null && !bev.getSizePrices().isEmpty()) {
                setStatus("Please select a size.");
                return;
            }
            String size  = selected != null ? (String) selected.getUserData() : "";
            double price = bev.getPriceForSize(size);

            List<String> custNames = new ArrayList<>();
            for (CheckBox cb : customChecks) {
                if (cb.isSelected()) {
                    Customization c = (Customization) cb.getUserData();
                    price += c.getExtraCost();
                    custNames.add(c.getName());
                }
            }

            int q = qty.getValue();
            if (!inventoryService.canMakeItem(bev, q)) {
                List<String> missing = inventoryService.getMissingIngredients(bev, q);
                setStatus("Cannot add — " + String.join(", ", missing));
                return;
            }

            currentOrder.addItem(
                    new OrderItem(bev.getId(), bev.getName(), q, size, custNames, price));
            refreshOrderSummary();
            setStatus(bev.getName() + " added to order.");
        });
        actionRow.getChildren().addAll(qty, addBtn);

        card.getChildren().addAll(nameLabel, sizeRow, customBox, actionRow);
        return card;
    }

    private VBox buildPastryCard(Pastry pastry) {
        VBox card = new VBox(6);
        card.getStyleClass().add("menu-card");

        Label nameLabel  = new Label(pastry.getName());
        nameLabel.getStyleClass().add("card-title");
        Label priceLabel = new Label("USD " + String.format("%.2f", pastry.getBasePrice()));

        HBox actionRow = new HBox(8);
        actionRow.getChildren().add(new Label("Qty:"));
        Spinner<Integer> qty = new Spinner<>(1, 10, 1);
        qty.setPrefWidth(75);
        Button addBtn = new Button("Add to Order");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> {
            int q = qty.getValue();
            if (!inventoryService.canMakeItem(pastry, q)) {
                List<String> missing = inventoryService.getMissingIngredients(pastry, q);
                setStatus("Cannot add — " + String.join(", ", missing));
                return;
            }
            currentOrder.addItem(
                    new OrderItem(pastry.getId(), pastry.getName(), q,
                                  null, null, pastry.getBasePrice()));
            refreshOrderSummary();
            setStatus(pastry.getName() + " added to order.");
        });
        actionRow.getChildren().addAll(qty, addBtn);

        card.getChildren().addAll(nameLabel, priceLabel, actionRow);
        return card;
    }

    // Order summary

    private void refreshOrderSummary() {
        orderListView.getItems().clear();
        for (OrderItem item : currentOrder.getItems()) {
            orderListView.getItems().add(
                    item.getQuantity() + "x " + item.getDisplayDescription()
                    + "  —  USD " + String.format("%.2f", item.getLineTotal()));
        }
        totalLabel.setText("Total: USD " + String.format("%.2f", currentOrder.getTotalPrice()));
    }

    // Button handlers

    @FXML
    private void handlePlaceOrder() {
        if (currentOrder.getItems().isEmpty()) {
            setStatus("Your order is empty — please add items first.");
            return;
        }
        String error = orderService.placeOrder(currentOrder);
        if (error != null) {
            setStatus("Order failed: " + error);
            return;
        }
        setStatus("Order placed! ID: " + currentOrder.getOrderId());
        currentOrder = new Order(SessionStore.getInstance().getCurrentUserName());
        refreshOrderSummary();
        refreshMenu();
    }

    @FXML
    private void handleClearOrder() {
        currentOrder = new Order(SessionStore.getInstance().getCurrentUserName());
        refreshOrderSummary();
        setStatus("Order cleared.");
    }

    @FXML
    private void handleLogout() {
    	orderService.removeObserver(this);
        SceneManager.switchTo("/com/brewandbite/view/LandingView.fxml", "Brew & Bite");
    }

    // Helpers

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }
    
    @Override
    public void onOrderEvent(OrderEvent event) {
        // Only notify the logged-in customer about their own orders
        String me = SessionStore.getInstance().getCurrentUserName();
        if (event.getOrder() == null) return;
        if (!me.equals(event.getOrder().getCustomerName())) return;

        Platform.runLater(() -> {
            // Reuse your existing status label
            statusLabel.setText(event.getMessage());
        });
    }
}
