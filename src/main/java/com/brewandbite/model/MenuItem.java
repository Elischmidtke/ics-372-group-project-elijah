package com.brewandbite.model;

import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Beverage.class, name = "beverage"),
    @JsonSubTypes.Type(value = Pastry.class,   name = "pastry")
})
public abstract class MenuItem {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("basePrice")
    private double basePrice;

    @JsonProperty("available")
    private boolean available = true;

    @JsonProperty("ingredientRequirements")
    private List<IngredientRequirement> ingredientRequirements = new ArrayList<>();

    public MenuItem() {}

    public MenuItem(String id, String name, String category, double basePrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
    }

    public String getId() { return id; }
    public void setId(String v) { this.id = v; }

    public String getName() { return name; }
    public void setName(String v) { 
    	if(!(v.isBlank()) && !(v == null)) {
    		this.name = v; 
    	}
    }

    public String getCategory() { return category; }
    public void setCategory(String v) { 
    	if(!(v.isBlank()) && !(v == null)) {
    		this.category = v; 
    	}
    	
    }

    public double  getBasePrice() { return basePrice; }
    public void setBasePrice(double v) { 
    	if(v >= 0.0) {
    		this.basePrice = v; 
    	}
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean v) { this.available = v; }

    public List<IngredientRequirement> getIngredientRequirements() { return ingredientRequirements; }
    public void setIngredientRequirements(List<IngredientRequirement> v) { this.ingredientRequirements = v; }
    
    
    //change ingredient requirement
    public void editRequirement(String name, double cost) {
    	//get the ingredient requirement
    	//check if the ingredient requirement exists and if so change its amount
    	for(IngredientRequirement temp: ingredientRequirements) {
    		if(temp.getIngredientName().equals(name)) {
    			temp.setAmount(cost);
    			return;
    		}
    	}
    }

    /** Short label used in UI cards and type columns. */
    public abstract String getDisplayType();
    
    public void editItem(MenuItemRequest req) {
    	this.setName(req.getName());
    	this.setBasePrice(req.getBasePrice());
    	this.setCategory(req.getCategory());
    	
    	List<IngredientRequirement> tempRequire = req.getIngredientRequirement();
    	
    	//for each of the changed ingredient requirements change them
    	for(IngredientRequirement requirement: tempRequire) {
    		editRequirement(requirement.getIngredientName(), requirement.getAmount());
    	}
    	
    }
    
}
