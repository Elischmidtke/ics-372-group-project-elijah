package com.brewandbite.controller;

import com.brewandbite.model.Order;
import com.brewandbite.model.OrderItem;
import com.brewandbite.service.OrderService;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BaristaController {

    // FXML nodes
    @FXML private TableView<Order>              ordersTable;
    @FXML private TableColumn<Order, String>    colOrderId;
    @FXML private TableColumn<Order, String>    colCustomer;
    @FXML private TableColumn<Order, String>    colStatus;
    @FXML private TableColumn<Order, String>    colTime;
    @FXML private TableColumn<Order, Double>    colTotal;
    @FXML private ComboBox<String>              filterCombo;
    @FXML private ListView<String>              itemsListView;
    @FXML private Label                         selectedOrderLabel;
    @FXML private Label                         statusLabel;

    // State
    private OrderService       orderService;
    private FilteredList<Order> filteredOrders;

    // Lifecycle

    @FXML
    public void initialize() {
        orderService = SessionStore.getInstance().getOrderService();

        setupTable();
        setupFilter();
        setupSelectionListener();
    }

    private void setupTable() {
        colOrderId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getOrderId()));
        colCustomer.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCustomerName()));
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));
        colTime.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTimestamp()));
        colTotal.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getTotalPrice()).asObject());

        filteredOrders = new FilteredList<>(orderService.getOrders(), o -> true);
        ordersTable.setItems(filteredOrders);
    }

    private void setupFilter() {
        filterCombo.getItems().addAll("All", "PENDING", "IN_PROGRESS", "FULFILLED");
        filterCombo.setValue("All");
        filterCombo.setOnAction(e -> applyFilter());
    }

    private void setupSelectionListener() {
        ordersTable.getSelectionModel().selectedItemProperty()
                   .addListener((obs, old, sel) -> showDetail(sel));
    }

    // Filter

    private void applyFilter() {
        String filter = filterCombo.getValue();
        filteredOrders.setPredicate(o ->
                "All".equals(filter) || o.getStatus().name().equals(filter));
    }

    // Detail panel

    private void showDetail(Order order) {
        itemsListView.getItems().clear();
        if (order == null) {
            selectedOrderLabel.setText("No order selected");
            return;
        }
        selectedOrderLabel.setText(
                "Order: " + order.getOrderId() + "  |  " + order.getCustomerName());
        for (OrderItem item : order.getItems()) {
            itemsListView.getItems().add(
                    item.getQuantity() + "x  " + item.getDisplayDescription()
                    + "  —  USD " + String.format("%.2f", item.getLineTotal()));
        }
        itemsListView.getItems().add("─────────────────────────────");
        itemsListView.getItems().add(
                "TOTAL:  USD " + String.format("%.2f", order.getTotalPrice()));
    }

    // Button handlers

    @FXML
    private void handleMarkInProgress() {
        Order sel = ordersTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select an order first."); return; }
        if (sel.getStatus() != Order.Status.PENDING) {
            setStatus("Only PENDING orders can be moved to IN PROGRESS."); return;
        }
        orderService.updateOrderStatus(sel, Order.Status.IN_PROGRESS);
        ordersTable.refresh();
        setStatus("Order " + sel.getOrderId() + " is now IN PROGRESS.");
    }

    @FXML
    private void handleMarkFulfilled() {
        Order sel = ordersTable.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select an order first."); return; }
        if (sel.getStatus() == Order.Status.FULFILLED) {
            setStatus("Order is already fulfilled."); return;
        }
        orderService.updateOrderStatus(sel, Order.Status.FULFILLED);
        ordersTable.refresh();
        setStatus("Order " + sel.getOrderId() + " marked FULFILLED.");
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("/com/brewandbite/view/LandingView.fxml", "Brew & Bite");
    }

    // Helpers

    private void setStatus(String msg) { statusLabel.setText(msg); }
}
