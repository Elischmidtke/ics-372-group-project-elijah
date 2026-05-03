package com.brewandbite.service;

import com.brewandbite.model.AppData;
import com.brewandbite.model.MenuItem;
import com.brewandbite.model.Order;
import com.brewandbite.model.OrderItem;
import com.brewandbite.notification.OrderEvent;
import com.brewandbite.notification.OrderEventType;
import com.brewandbite.notification.OrderObserver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages order placement and status updates.
 * Uses an {@link ObservableList} so Barista and Manager tables update live.
 *
 * Persistence: writes all AppData through PersistenceService on each mutation.
 */
public class OrderService {

    private final ObservableList<Order> orders;
    private final InventoryService      inventoryService;
    private final PersistenceService    persistenceService;
    private final MenuService           menuService;
    private final List<OrderObserver>   observers = new CopyOnWriteArrayList<>();

    public OrderService(List<Order>       orders,
                        InventoryService  inventoryService,
                        PersistenceService persistenceService,
                        MenuService       menuService) {
        this.orders             = FXCollections.observableArrayList(orders);
        this.inventoryService   = inventoryService;
        this.persistenceService = persistenceService;
        this.menuService        = menuService;
    }

    public ObservableList<Order> getOrders() { return orders; }

    public String placeOrder(Order order) {
        if (order == null) return "Order is null.";
        if (order.getItems() == null || order.getItems().isEmpty()) return "Order is empty.";

        for (OrderItem oi : order.getItems()) {
            MenuItem mi = findMenuItem(oi.getMenuItemId());
            if (mi == null) {
                return "Menu item not found: " + oi.getMenuItemName();
            }
            List<String> missing = inventoryService.getMissingIngredients(mi, oi.getQuantity());
            if (!missing.isEmpty()) {
                return "Insufficient ingredients for " + oi.getMenuItemName()
                        + ": " + String.join(", ", missing);
            }
        }
        for (OrderItem oi : order.getItems()) {
            MenuItem mi = findMenuItem(oi.getMenuItemId());
            if (mi != null) {
                inventoryService.deductIngredients(mi, oi.getQuantity());
            }
        }

        order.setAcceptedByBarista(false);
        order.setStatus(Order.Status.PENDING);
        orders.add(order);
        saveAll();

        notifyObservers(new OrderEvent(
                OrderEventType.NEW_ORDER,
                order,
                "New order placed: " + order.getOrderId()
        ));

        notifyObservers(new OrderEvent(
                OrderEventType.PENDING_ORDER,
                order,
                "Order placed! Waiting for barista acceptance. ID: " + order.getOrderId()
        ));

        return null; 
    }

 
    public void acceptOrder(Order order) {
        if (order == null) return;

        order.setAcceptedByBarista(true);
        updateOrderStatus(order, Order.Status.IN_PROGRESS);
        notifyObservers(new OrderEvent(
                OrderEventType.ACCEPTED,
                order,
                "Your order " + order.getOrderId() + " was accepted!"
        ));
    }

    public void updateOrderStatus(Order order, Order.Status newStatus) {
        if (order == null || newStatus == null) return;

        order.setStatus(newStatus);
        int idx = orders.indexOf(order);
        if (idx >= 0) {
            orders.set(idx, order);
        }
        saveAll();
        switch (newStatus) {
            case IN_PROGRESS -> notifyObservers(new OrderEvent(
                    OrderEventType.IN_PROGRESS,
                    order,
                    "Your order " + order.getOrderId() + " is now IN PROGRESS."
            ));
            case FULFILLED -> notifyObservers(new OrderEvent(
                    OrderEventType.COMPLETE,
                    order,
                    "Your order " + order.getOrderId() + " is COMPLETE!"
            ));
            default -> { /* no-op */ }
        }

        switch (newStatus) {
            case PENDING -> notifyObservers(new OrderEvent(
                    OrderEventType.PENDING_ORDER,
                    order,
                    "Order pending: " + order.getOrderId()
            ));
            case FULFILLED -> notifyObservers(new OrderEvent(
                    OrderEventType.COMPLETED_ORDER,
                    order,
                    "Order completed: " + order.getOrderId()
            ));
            default -> { /* no-op */ }
        }
    }
    public void addObserver(OrderObserver obs) {
        if (obs != null) observers.add(obs);
    }

    public void removeObserver(OrderObserver obs) {
        observers.remove(obs);
    }

    private void notifyObservers(OrderEvent event) {
        for (OrderObserver o : observers) {
            o.onOrderEvent(event);
        }
    }

    // Helpers

    private MenuItem findMenuItem(String id) {
        return menuService.getAllItems().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst().orElse(null);
    }

    private void saveAll() {
        AppData data = new AppData();
        data.setMenuItems(new ArrayList<>(menuService.getAllItems()));
        data.setIngredients(inventoryService.getInventoryAsList());
        data.setOrders(new ArrayList<>(orders));
        persistenceService.saveData(data);
    }
}
