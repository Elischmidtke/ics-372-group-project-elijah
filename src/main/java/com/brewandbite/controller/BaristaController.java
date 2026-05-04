package com.brewandbite.controller;

import com.brewandbite.model.Order;
import com.brewandbite.model.OrderItem;
import com.brewandbite.notification.OrderEvent;
import com.brewandbite.notification.OrderEventType;
import com.brewandbite.notification.OrderObserver;
import com.brewandbite.service.OrderService;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BaristaController implements OrderObserver {

    // FXML nodes
    @FXML private TableView<Order>            ordersTable;
    @FXML private TableColumn<Order, String>  colOrderId;
    @FXML private TableColumn<Order, String>  colCustomer;
    @FXML private TableColumn<Order, String>  colStatus;
    @FXML private TableColumn<Order, String>  colTime;
    @FXML private TableColumn<Order, Double>  colTotal;
    @FXML private ComboBox<String>            filterCombo;
    @FXML private ListView<String>            itemsListView;
    @FXML private Label                       selectedOrderLabel;
    @FXML private Label                       statusLabel;

    // State
    private OrderService        orderService;
    private FilteredList<Order> currentOrders;

    @FXML
    public void initialize() {
        orderService = SessionStore.getInstance().getOrderService();
        orderService.addObserver(this);

        setupTable();
        setupFilter();
        setupSelectionListener();

        setStatus("Ready. Use the filter to view pending orders.");
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
        currentOrders = new FilteredList<>(orderService.getOrders(),
                o -> o != null && o.getStatus() != Order.Status.FULFILLED
        );
        ordersTable.setItems(currentOrders);
    }

    private void setupFilter() {
        filterCombo.getItems().clear();
        filterCombo.getItems().addAll("All", "PENDING", "IN_PROGRESS");
        filterCombo.setValue("All");
        filterCombo.setOnAction(e -> applyFilter());
        applyFilter();
    }

    private void applyFilter() {
        String filter = filterCombo.getValue();
        currentOrders.setPredicate(o -> {
            if (o == null) return false;
            if (o.getStatus() == Order.Status.FULFILLED) return false;

            if ("All".equals(filter)) return true;
            return o.getStatus().name().equals(filter);
        });
        ordersTable.refresh();
    }

    private void setupSelectionListener() {
        ordersTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> showDetail(sel));
    }

    private void showDetail(Order order) {
        itemsListView.getItems().clear();
        if (order == null) {
            selectedOrderLabel.setText("No order selected");
            return;
        }

        selectedOrderLabel.setText(
                "Order: " + order.getOrderId() + "  |  " + order.getCustomerName()
                        + "  |  " + order.getStatus().name()
        );

        for (OrderItem item : order.getItems()) {
            itemsListView.getItems().add(
                    item.getQuantity() + "x  " + item.getDisplayDescription()
                            + "  —  USD " + String.format("%.2f", item.getLineTotal())
            );
        }

        itemsListView.getItems().add("─────────────────────────────");
        itemsListView.getItems().add(
                "TOTAL:  USD " + String.format("%.2f", order.getTotalPrice())
        );
    }

    @FXML
    private void handleAcceptSelected() {
        Order sel = ordersTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            setStatus("Select an order first.");
            return;
        }

        if (sel.isAcceptedByBarista() || sel.getStatus() != Order.Status.PENDING) {
            setStatus("That order is not pending.");
            return;
        }

        orderService.acceptOrder(sel);
        setStatus("Accepted order " + sel.getOrderId() + ".");
        applyFilter();
    }

    @FXML
    private void handleMarkFulfilled() {
        Order sel = ordersTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            setStatus("Select an order first.");
            return;
        }

        if (sel.getStatus() != Order.Status.IN_PROGRESS) {
            setStatus("Only IN_PROGRESS orders can be fulfilled.");
            return;
        }

        orderService.updateOrderStatus(sel, Order.Status.FULFILLED);
        setStatus("Fulfilled order " + sel.getOrderId() + ".");

        ordersTable.getSelectionModel().clearSelection();
        showDetail(null);
        applyFilter();
    }

    @FXML
    private void handleLogout() {
        orderService.removeObserver(this);
        SceneManager.switchTo("/com/brewandbite/view/LandingView.fxml", "Brew & Bite");
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    @Override
    public void onOrderEvent(OrderEvent event) {
        if (event == null || event.getOrder() == null) return;

        if (event.getType() == OrderEventType.NEW_ORDER && !event.getOrder().isAcceptedByBarista()) {
            showNewOrderPopup(event.getOrder());
        }
    }

    private void showNewOrderPopup(Order order) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("New Order");
            alert.setHeaderText("New order placed: " + order.getOrderId());
            alert.setContentText(buildOrderSummary(order));

            ButtonType accept = new ButtonType("Accept");
            ButtonType dismiss = new ButtonType("Dismiss", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(accept, dismiss);

            alert.showAndWait().ifPresent(btn -> {
                if (btn == accept) {
                    orderService.acceptOrder(order);
                    setStatus("Accepted order " + order.getOrderId() + ".");
                    applyFilter();
                }
            });
        });
    }

    private String buildOrderSummary(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(order.getCustomerName()).append("\n\n");

        for (OrderItem item : order.getItems()) {
            sb.append(item.getQuantity())
              .append("x ")
              .append(item.getDisplayDescription())
              .append("\n");
        }

        sb.append("\nTOTAL: USD ").append(String.format("%.2f", order.getTotalPrice()));
        return sb.toString();
    }
}