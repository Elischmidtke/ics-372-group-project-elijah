package com.brewandbite.controller;

import com.brewandbite.model.*;
import com.brewandbite.service.InventoryService;
import com.brewandbite.service.MenuService;
import com.brewandbite.service.OrderService;
import com.brewandbite.service.PersistenceService;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import com.brewandbite.model.MenuItem;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class ManagerController {

    // Menu tab
    @FXML private TableView<MenuItem>           menuTable;
    @FXML private TableColumn<MenuItem, String> colId;
    @FXML private TableColumn<MenuItem, String> colName;
    @FXML private TableColumn<MenuItem, String> colCategory;
    @FXML private TableColumn<MenuItem, Double> colPrice;
    @FXML private TableColumn<MenuItem, Boolean> colAvail;
    @FXML private Label                         menuStatusLabel;

    // Inventory tab
    @FXML private TableView<Ingredient>           inventoryTable;
    @FXML private TableColumn<Ingredient, String> colIngName;
    @FXML private TableColumn<Ingredient, Double> colIngQty;
    @FXML private TableColumn<Ingredient, String> colIngUnit;
    @FXML private TextField                       restockNameField;
    @FXML private TextField                       restockAmountField;
    @FXML private Label                           inventoryStatusLabel;

    // Sales tab
    @FXML private TableView<Order>              salesTable;
    @FXML private TableColumn<Order, String>    colOrdId;
    @FXML private TableColumn<Order, String>    colOrdCustomer;
    @FXML private TableColumn<Order, String>    colOrdStatus;
    @FXML private TableColumn<Order, String>    colOrdTime;
    @FXML private TableColumn<Order, Double>    colOrdTotal;
    @FXML private Label                         totalSalesLabel;

    // Services
    private MenuService        menuService;
    private InventoryService   inventoryService;
    private OrderService       orderService;
    private PersistenceService persistenceService;

    // Lifecycle

    @FXML
    public void initialize() {
        SessionStore s = SessionStore.getInstance();
        menuService        = s.getMenuService();
        inventoryService   = s.getInventoryService();
        orderService       = s.getOrderService();
        persistenceService = s.getPersistenceService();

        setupMenuTable();
        setupInventoryTable();
        setupSalesTable();
    }

    // Table setup

    private void setupMenuTable() {
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getId()));
        colName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));
        colCategory.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCategory()));
        colPrice.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getBasePrice()).asObject());
        colAvail.setCellValueFactory(c ->
                new SimpleBooleanProperty(c.getValue().isAvailable()).asObject());
        menuTable.setItems(menuService.getAllItems());
    }

    private void setupInventoryTable() {
        colIngName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));
        colIngQty.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getQuantity()).asObject());
        colIngUnit.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getUnit()));
        inventoryTable.setItems(inventoryService.getInventory());
    }

    private void setupSalesTable() {
        colOrdId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getOrderId()));
        colOrdCustomer.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCustomerName()));
        colOrdStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));
        colOrdTime.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTimestamp()));
        colOrdTotal.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getTotalPrice()).asObject());
        salesTable.setItems(orderService.getOrders());

        orderService.getOrders().addListener(
                (javafx.collections.ListChangeListener<Order>) c -> refreshTotalSales());
        refreshTotalSales();
    }

    private void refreshTotalSales() {
        double total = orderService.getOrders().stream()
                .filter(o -> o.getStatus() == Order.Status.FULFILLED)
                .mapToDouble(Order::getTotalPrice)
                .sum();
        totalSalesLabel.setText("Total Fulfilled Sales: USD " + String.format("%.2f", total));
    }

    // Menu CRUD handlers

    @FXML
    private void handleAddMenuItem() {
        showMenuItemDialog(null);
    }

    @FXML
    private void handleEditMenuItem() {
        MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null) { menuStatusLabel.setText("Select a menu item to edit."); return; }
        showMenuItemDialog(sel);
    }

    @FXML
    private void handleRemoveMenuItem() {
        MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null) { menuStatusLabel.setText("Select a menu item to remove."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove \"" + sel.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Remove");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                menuService.removeItem(sel.getId());
                menuStatusLabel.setText("\"" + sel.getName() + "\" removed.");
            }
        });
    }

    @FXML
    private void handleToggleAvailability() {
        MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null) { menuStatusLabel.setText("Select a menu item."); return; }
        sel.setAvailable(!sel.isAvailable());
        menuService.updateItem(sel);
        menuTable.refresh();
        menuStatusLabel.setText(sel.getName() + " availability: " + sel.isAvailable());
    }

    // Inventory handler

    @FXML
    private void handleRestock() {
        String name   = restockNameField.getText().trim();
        String amtStr = restockAmountField.getText().trim();

        if (name.isEmpty() || amtStr.isEmpty()) {
            inventoryStatusLabel.setText("Enter both ingredient name and amount.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) throw new NumberFormatException("non-positive");
        } catch (NumberFormatException ex) {
            inventoryStatusLabel.setText("Amount must be a positive number.");
            return;
        }

        inventoryService.restock(name, amount);
        saveAll();
        inventoryTable.refresh();
        inventoryStatusLabel.setText("Restocked " + name + " by " + amount + ".");
        restockNameField.clear();
        restockAmountField.clear();
    }

    // Add / Edit dialog

    private void showMenuItemDialog(MenuItem existing) {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Menu Item" : "Edit Menu Item");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> typeCombo  = new ComboBox<>();
        typeCombo.getItems().addAll("Beverage", "Pastry");

        TextField nameField    = new TextField();
        TextField priceField   = new TextField();
        TextField subTypeField = new TextField();

        grid.add(new Label("Type:"),     0, 0); grid.add(typeCombo,  1, 0);
        grid.add(new Label("Name:"),     0, 1); grid.add(nameField,  1, 1);
        grid.add(new Label("Base Price:"),0,2); grid.add(priceField, 1, 2);
        grid.add(new Label("Sub-type:"), 0, 3); grid.add(subTypeField,1,3);

        if (existing != null) {
            typeCombo.setValue(existing instanceof Beverage ? "Beverage" : "Pastry");
            nameField.setText(existing.getName());
            priceField.setText(String.valueOf(existing.getBasePrice()));
            if (existing instanceof Beverage b) subTypeField.setText(b.getBeverageType());
            else if (existing instanceof Pastry p) subTypeField.setText(p.getPastryType());
        } else {
            typeCombo.setValue("Beverage");
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            String name = nameField.getText().trim();
            if (name.isEmpty()) return null;
            double price;
            try { price = Double.parseDouble(priceField.getText().trim()); }
            catch (NumberFormatException e) { return null; }
            String subType = subTypeField.getText().trim();

            if ("Beverage".equals(typeCombo.getValue())) {
                Beverage bev = (existing instanceof Beverage b)
                        ? b : new Beverage(menuService.generateId("BEV"), name, subType, price);
                bev.setName(name);
                bev.setBasePrice(price);
                bev.setBeverageType(subType);
                if (bev.getSizePrices().isEmpty()) {
                    bev.getSizePrices().put("Small",  price);
                    bev.getSizePrices().put("Medium", price + 50);
                    bev.getSizePrices().put("Large",  price + 100);
                }
                return bev;
            } else {
                Pastry p = (existing instanceof Pastry pt)
                        ? pt : new Pastry(menuService.generateId("PST"), name, subType, "", price);
                p.setName(name);
                p.setBasePrice(price);
                p.setPastryType(subType);
                return p;
            }
        });

        dialog.showAndWait().ifPresent(item -> {
            if (existing == null) {
                menuService.addItem(item);
                menuStatusLabel.setText("Added: " + item.getName());
            } else {
                menuService.updateItem(item);
                menuStatusLabel.setText("Updated: " + item.getName());
            }
            menuTable.refresh();
        });
    }

    // Logout

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("/com/brewandbite/view/LandingView.fxml", "Brew & Bite");
    }

    // Helpers

    private void saveAll() {
        AppData data = new AppData();
        data.setMenuItems(new ArrayList<>(menuService.getAllItems()));
        data.setIngredients(inventoryService.getInventoryAsList());
        data.setOrders(new ArrayList<>(orderService.getOrders()));
        persistenceService.saveData(data);
    }
}
