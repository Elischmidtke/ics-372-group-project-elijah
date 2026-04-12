package com.brewandbite.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/** Root object written to / read from appdata.json. */
public class AppData {

    @JsonProperty("menuItems")
    private List<MenuItem> menuItems = new ArrayList<>();

    @JsonProperty("ingredients")
    private List<Ingredient> ingredients = new ArrayList<>();

    @JsonProperty("orders")
    private List<Order> orders = new ArrayList<>();

    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> v) { this.menuItems = v; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> v) { this.ingredients = v; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> v) { this.orders = v; }
}
