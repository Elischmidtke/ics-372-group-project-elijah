package com.brewandbite.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Beverage extends MenuItem {

    @JsonProperty("beverageType")
    private String beverageType;                              // e.g. "Coffee", "Tea"

    @JsonProperty("sizePrices")
    private Map<String, Double> sizePrices = new LinkedHashMap<>();

    @JsonProperty("availableCustomizations")
    private List<Customization> availableCustomizations = new ArrayList<>();

    public Beverage() {}

    public Beverage(String id, String name, String beverageType, double basePrice) {
        super(id, name, "Beverage", basePrice);
        this.beverageType = beverageType;
    }

    public String getBeverageType() { return beverageType; }
    public void setBeverageType(String v) { this.beverageType = v; }

    public Map<String, Double> getSizePrices() { return sizePrices; }
    public void setSizePrices(Map<String, Double> v) { this.sizePrices = v; }

    public List<Customization> getAvailableCustomizations() { return availableCustomizations; }
    public void setAvailableCustomizations(List<Customization> v){ this.availableCustomizations = v; }

    /**
     * Returns the price for the given size key, falling back to {@code basePrice}
     * if the key is absent or null.
     */
    public double getPriceForSize(String size) {
        if (size == null || sizePrices == null) return getBasePrice();
        return sizePrices.getOrDefault(size, getBasePrice());
    }

    @Override
    public String getDisplayType() { return beverageType != null ? beverageType : "Beverage"; }

    @Override
    public String toString() { return getName(); }
}
