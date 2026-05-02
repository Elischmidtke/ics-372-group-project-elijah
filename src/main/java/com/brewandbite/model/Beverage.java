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
    
    //change customizations
    public void editCustomizationPrice(String name, double cost) {
    	for(Customization customization: availableCustomizations){
    		if(customization.getName().equals(name) && customization.getExtraCost() >= 0.0) {
        		customization.setExtraCost(cost);
        	}
    	}
    	
    }
    
    //add customization
    public void addCustomization(Customization customization) {
    	for(Customization temp: availableCustomizations){ //check if it exists already
    		if(temp.getName().equals(customization.getName())) {
        		return;
        	}
    	}
    	
    	//check if the customization extra cost is a negative value
    	if(customization.getExtraCost() >= 0.0) { //if not negative add
    		availableCustomizations.add(customization);
    	}
    }
    //change the price of a size
    public void setSizePrice(String size, double price) {
    	if(size != null && sizePrices.containsKey(size) && price >= 0.0) { //price can't be negative
    		sizePrices.replace(size, price);
    	}
    }
    
    //remove customization
    public void removeCustomization(String name) {
    	for(Customization temp: availableCustomizations){ //check if it exists 
    		if(temp.getName().equals(name)) {
        		availableCustomizations.remove(temp);
        		break;
        	}
    	}
    }
    

    @Override
    public String getDisplayType() { return beverageType != null ? beverageType : "Beverage"; }

    @Override
    public String toString() { return getName(); }
    
    public void editItem(MenuItemRequest req) {
    	//do base item changes
    	super.editItem(req);
    	
    	//do beverage item changes
    	this.setBeverageType(req.getSubType());
    	Map<String, Double> newSizesPrices = req.getSizePrices();
    	
    	//update beverage sizes
    	for (Map.Entry<String, Double> entry : newSizesPrices.entrySet()) {
            this.setSizePrice(entry.getKey(), entry.getValue());
        }
    	
    	//edit customizations
    	List<Customization> customizations = req.getAvailableCustomizations();
    	
    	for(Customization customization: customizations) {
    		this.editCustomizationPrice(customization.getName(), customization.getExtraCost());
    	}
    	
    }
}
