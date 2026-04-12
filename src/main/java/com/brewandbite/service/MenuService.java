package com.brewandbite.service;

import com.brewandbite.model.AppData;
import com.brewandbite.model.Beverage;
import com.brewandbite.model.MenuItem;
import com.brewandbite.model.Pastry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages the menu catalogue. Exposes an {@link ObservableList} so any
 * JavaFX table or list bound to it updates automatically on CRUD operations.
 */
public class MenuService {

    private final ObservableList<MenuItem> menuItems;
    private final PersistenceService       persistenceService;
    private final InventoryService         inventoryService;

    public MenuService(List<MenuItem> items,
                       PersistenceService persistenceService,
                       InventoryService   inventoryService) {
        this.menuItems          = FXCollections.observableArrayList(items);
        this.persistenceService = persistenceService;
        this.inventoryService   = inventoryService;
    }

    // Read

    /** All menu items (available and unavailable). Suitable for Manager views. */
    public ObservableList<MenuItem> getAllItems() { return menuItems; }

    /** Available beverages only. Used by the customer ordering screen. */
    public List<MenuItem> getBeverages() {
        return menuItems.stream()
                .filter(m -> m instanceof Beverage && m.isAvailable())
                .collect(Collectors.toList());
    }

    /** Available pastries only. Used by the customer ordering screen. */
    public List<MenuItem> getPastries() {
        return menuItems.stream()
                .filter(m -> m instanceof Pastry && m.isAvailable())
                .collect(Collectors.toList());
    }

    // Write

    public void addItem(MenuItem item) {
        menuItems.add(item);
        save();
    }

    public void updateItem(MenuItem item) {
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId().equals(item.getId())) {
                menuItems.set(i, item);
                break;
            }
        }
        save();
    }

    public void removeItem(String itemId) {
        menuItems.removeIf(m -> m.getId().equals(itemId));
        save();
    }

    // Helpers

    /** Generates a unique ID with the given prefix, e.g. {@code "BEV-3F2A1C9E"}. */
    public String generateId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void save() {
        AppData data = new AppData();
        data.setMenuItems(new ArrayList<>(menuItems));
        data.setIngredients(inventoryService.getInventoryAsList());
        persistenceService.saveData(data);
    }
}
