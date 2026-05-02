package com.brewandbite.model;

import com.brewandbite.util.SessionStore;

public class PastryFactory extends MenuItemFactory {
	public MenuItem createItem(MenuItemRequest request) {
		//create item and set up its prices
		SessionStore s = SessionStore.getInstance();
	
		MenuItem pastry = new Pastry(s.getMenuService().generateId("PST"), request.getName(), request.getSubType(), request.getVariation(), request.getBasePrice());
		pastry.setIngredientRequirements(request.getIngredientRequirement());
		return pastry;
	}
}
