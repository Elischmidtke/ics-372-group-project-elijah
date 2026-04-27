package com.brewandbite.service;
import com.brewandbite.notification.OrderEvent;
import com.brewandbite.notification.OrderEventType;
import com.brewandbite.notification.OrderObserver;

import java.util.concurrent.CopyOnWriteArrayList;
import com.brewandbite.model.AppData;
import com.brewandbite.model.MenuItem;
import com.brewandbite.model.Order;
import com.brewandbite.model.OrderItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages order placement and status updates.
 * Uses an {@link ObservableList} so Barista and Manager tables update live.
 */
public class OrderService {

    private final ObservableList<Order> orders;
    private final InventoryService      inventoryService;
    private final PersistenceService    persistenceService;
    private final MenuService           menuService;
    private final List<OrderObserver> observers = new CopyOnWriteArrayList<>();

    public OrderService(List<Order>         orders,
                        InventoryService     inventoryService,
                        PersistenceService   persistenceService,
                        MenuService          menuService) {
        this.orders             = FXCollections.observableArrayList(orders);
        this.inventoryService   = inventoryService;
        this.persistenceService = persistenceService;
        this.menuService        = menuService;
    }

    // Read

    /** All orders — suitable for Manager sales view. */
    public ObservableList<Order> getOrders() { return orders; }

    // Write

    /**
     * Validates stock for every line item, deducts ingredients, persists, and
     * adds the order to the live list.
     *
     * @return {@code null} on success, or an error message string on failure.
     */
    public String placeOrder(Order order) {
        // 1. Validate all items before touching any stock
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

        // 2. All checks passed — deduct stock
        for (OrderItem oi : order.getItems()) {
            MenuItem mi = findMenuItem(oi.getMenuItemId());
            if (mi != null) {
                inventoryService.deductIngredients(mi, oi.getQuantity());
            }
        }

        // 3. Commit the order
        order.setStatus(Order.Status.PENDING);
        orders.add(order);
        saveAll();
     // Barista notification: new order
        notifyObservers(new OrderEvent(
                OrderEventType.NEW_ORDER,
                order,
                "New order placed: " + order.getOrderId()
        ));

        // Customer notification: treat PENDING as "ACCEPTED" (since no ACCEPTED status exists)
        notifyObservers(new OrderEvent(
                OrderEventType.ACCEPTED,
                order,
                "Your order was accepted! ID: " + order.getOrderId()
        ));
        return null; // null = success
    }

    /**
     * Advances an order to the given status and persists the change.
     */
    public void updateOrderStatus(Order order, Order.Status newStatus) {
        order.setStatus(newStatus);
        // Refresh the ObservableList so bound TableViews repaint
        int idx = orders.indexOf(order);
        if (idx >= 0) {
            orders.set(idx, order);
        }
        saveAll();
     // Customer-facing status notifications
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

        // Barista-facing notifications
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
}
