package com.brewandbite.service;

import com.brewandbite.model.MenuItem;
import com.brewandbite.model.Order;

import java.util.List;

public class BrewFacade {

    private final MenuService menuService;
    private final OrderService orderService;
    private final InventoryService inventoryService;

    public BrewFacade(MenuService menuService, OrderService orderService, InventoryService inventoryService) {
        this.menuService = menuService;
        this.orderService = orderService;
        this.inventoryService = inventoryService;
    }

    public List<MenuItem> getBeverages() {
        return menuService.getBeverages();
    }

    public List<MenuItem> getPastries() {
        return menuService.getPastries();
    }

    public boolean canMakeItem(MenuItem item, int quantity) {
        return inventoryService.canMakeItem(item, quantity);
    }

    public List<String> getMissingIngredients(MenuItem item, int quantity) {
        return inventoryService.getMissingIngredients(item, quantity);
    }

    public String placeOrder(Order order) {
        return orderService.placeOrder(order);
    }
}