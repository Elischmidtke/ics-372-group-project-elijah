package com.brewandbite.controller;

import com.brewandbite.model.Order;
import com.brewandbite.notification.OrderEvent;
import com.brewandbite.notification.OrderObserver;
import com.brewandbite.service.OrderService;
import com.brewandbite.util.SessionStore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class CustomerOrderStatusController implements OrderObserver {

    @FXML private Label orderIdLabel;
    @FXML private Label customerLabel;
    @FXML private Label statusValueLabel;
    @FXML private ListView<String> notificationsListView;

    private OrderService orderService;
    private String customerName;
    private String orderIdToTrack;

    @FXML
    public void initialize() {
        SessionStore s = SessionStore.getInstance();
        orderService = s.getOrderService();
        orderService.addObserver(this);

        customerName = s.getCurrentUserName();
        statusValueLabel.setText("Waiting for updates...");

        // Unsubscribe even if user closes window via the X button
        Platform.runLater(() -> {
            Stage stage = (Stage) statusValueLabel.getScene().getWindow();
            stage.setOnCloseRequest(e -> {
                if (orderService != null) orderService.removeObserver(this);
            });
        });
    }

    public void setOrderToTrack(Order order) {
        if (order == null) return;

        this.customerName = order.getCustomerName();
        this.orderIdToTrack = order.getOrderId();

        orderIdLabel.setText("Order: " + orderIdToTrack);
        customerLabel.setText("Customer: " + customerName);
        statusValueLabel.setText(order.getStatus().name());

        notificationsListView.getItems().add("Tracking order " + orderIdToTrack + "...");
    }

    @Override
    public void onOrderEvent(OrderEvent event) {
        if (event == null || event.getOrder() == null) return;
        if (customerName == null || customerName.isBlank()) return;
        if (orderIdToTrack == null || orderIdToTrack.isBlank()) return;

        String eventCustomer = event.getOrder().getCustomerName();
        String eventOrderId  = event.getOrder().getOrderId();
        if (eventCustomer == null || eventOrderId == null) return;

        if (!customerName.equals(eventCustomer)) return;
        if (!orderIdToTrack.equals(eventOrderId)) return;

        Platform.runLater(() -> {
            notificationsListView.getItems().add(event.getMessage());
            statusValueLabel.setText(event.getOrder().getStatus().name());
        });
    }

    @FXML
    private void handleClose() {
        if (orderService != null) {
            orderService.removeObserver(this);
        }
        Stage stage = (Stage) statusValueLabel.getScene().getWindow();
        stage.close();
    }
}