package com.brewandbite.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MenuItemRequest {
    private String id = "";

    private String name = "";

    private String category = "";
    
    private double basePrice = 0.0;

    private boolean available = true;
    
    private List<IngredientRequirement> ingredientRequirement = new LinkedList<>();
    
    private String subType = "";

    private String variation = "";
   
    private Map<String, Double> sizePrices = new LinkedHashMap<>();
    
    private List<Customization> availableCustomizations = new ArrayList<>();
    
    private String type = "";
    
    //getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public List<IngredientRequirement> getIngredientRequirement() {
		return ingredientRequirement;
	}

	public void setIngredientRequirement(List<IngredientRequirement> ingredientRequirement) {
		this.ingredientRequirement = ingredientRequirement;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getVariation() {
		return variation;
	}

	public void setVariation(String variation) {
		this.variation = variation;
	}

	public Map<String, Double> getSizePrices() {
		return sizePrices;
	}

	public void setSizePrices(Map<String, Double> sizePrices) {
		this.sizePrices = sizePrices;
	}

	public List<Customization> getAvailableCustomizations() {
		return availableCustomizations;
	}

	public void setAvailableCustomizations(List<Customization> availableCustomizations) {
		this.availableCustomizations = availableCustomizations;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
}
