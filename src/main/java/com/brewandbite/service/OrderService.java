package com.brewandbite.service;

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
