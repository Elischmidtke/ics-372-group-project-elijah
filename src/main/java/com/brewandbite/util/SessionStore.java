package com.brewandbite.util;

import com.brewandbite.model.UserRole;
import com.brewandbite.service.AuthService;
import com.brewandbite.service.InventoryService;
import com.brewandbite.service.MenuService;
import com.brewandbite.service.OrderService;
import com.brewandbite.service.PersistenceService;

/**
 * Application-wide singleton that holds the current session state and
 * references to all services. Controllers access services via
 * {@code SessionStore.getInstance()} instead of receiving them as constructor
 * parameters, which avoids tight coupling between controllers.
 */
public class SessionStore {

    private static SessionStore instance;

    private UserRole           currentRole;
    private String             currentUserName;
    private AuthService        authService;
    private MenuService        menuService;
    private InventoryService   inventoryService;
    private OrderService       orderService;
    private PersistenceService persistenceService;

    private SessionStore() {}

    public static SessionStore getInstance() {
        if (instance == null) instance = new SessionStore();
        return instance;
    }

    public UserRole getCurrentRole() { return currentRole; }
    public void setCurrentRole(UserRole v) { this.currentRole = v; }

    public String getCurrentUserName() { return currentUserName; }
    public void setCurrentUserName(String v) { this.currentUserName = v; }

    public AuthService getAuthService() { return authService; }
    public void setAuthService(AuthService v) { this.authService = v; }

    public MenuService getMenuService() { return menuService; }
    public void setMenuService(MenuService v) { this.menuService = v; }

    public InventoryService getInventoryService()                     { return inventoryService; }
    public void setInventoryService(InventoryService v)   { this.inventoryService = v; }

    public OrderService getOrderService() { return orderService; }
    public void setOrderService(OrderService v) { this.orderService = v; }

    public PersistenceService getPersistenceService() { return persistenceService; }
    public void setPersistenceService(PersistenceService v){ this.persistenceService = v; }
}
