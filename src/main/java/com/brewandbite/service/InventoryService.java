package com.brewandbite.service;

import com.brewandbite.model.Ingredient;
import com.brewandbite.model.IngredientRequirement;
import com.brewandbite.model.MenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages ingredient stock: availability checks, deductions, and restocking.
 * Exposes an {@link ObservableList} so the Manager's inventory table updates live.
 */
public class InventoryService {

    private final ObservableList<Ingredient> inventory;

    public InventoryService(List<Ingredient> ingredients) {
        this.inventory = FXCollections.observableArrayList(ingredients);
    }

    // Public API

    /**
     * Returns {@code true} if all ingredients required for {@code quantity}
     * units of {@code item} are available in stock.
     */
    public boolean canMakeItem(MenuItem item, int quantity) {
        for (IngredientRequirement req : item.getIngredientRequirements()) {
            Ingredient ing = find(req.getIngredientName());
            if (ing == null || ing.getQuantity() < req.getAmount() * quantity) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a human-readable list of shortage descriptions.
     * An empty list means all ingredients are sufficiently stocked.
     */
    public List<String> getMissingIngredients(MenuItem item, int quantity) {
        List<String> missing = new ArrayList<>();
        for (IngredientRequirement req : item.getIngredientRequirements()) {
            Ingredient ing    = find(req.getIngredientName());
            double     needed = req.getAmount() * quantity;
            if (ing == null) {
                missing.add(req.getIngredientName() + " (not in inventory)");
            } else if (ing.getQuantity() < needed) {
                missing.add(String.format("%s (need %.1f %s, have %.1f)",
                        ing.getName(), needed, ing.getUnit(), ing.getQuantity()));
            }
        }
        return missing;
    }

    /**
     * Deducts the ingredients consumed by {@code quantity} units of {@code item}.
     * Quantities are clamped to 0 — they will never go negative.
     */
    public void deductIngredients(MenuItem item, int quantity) {
        for (IngredientRequirement req : item.getIngredientRequirements()) {
            Ingredient ing = find(req.getIngredientName());
            if (ing != null) {
                ing.setQuantity(Math.max(0.0, ing.getQuantity() - req.getAmount() * quantity));
            }
        }
    }

    /**
     * Adds {@code amount} to the named ingredient's stock.
     * Does nothing if the ingredient name is not found.
     */
    public void restock(String ingredientName, double amount) {
        Ingredient ing = find(ingredientName);
        if (ing != null) {
            ing.setQuantity(ing.getQuantity() + amount);
        }
    }

    /** Looks up an ingredient by name (case-insensitive). Returns {@code null} if not found. */
    public Ingredient find(String name) {
        if (name == null) return null;
        return inventory.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name.trim()))
                .findFirst().orElse(null);
    }

    /** Live list suitable for binding directly to a JavaFX {@code TableView}. */
    public ObservableList<Ingredient> getInventory() { return inventory; }

    /** Returns a plain snapshot list for serialisation. */
    public List<Ingredient> getInventoryAsList() { return new ArrayList<>(inventory); }
}
