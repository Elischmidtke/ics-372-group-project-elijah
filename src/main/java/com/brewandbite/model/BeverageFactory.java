package com.brewandbite.model;

import com.brewandbite.util.SessionStore;

public class BeverageFactory extends MenuItemFactory{
	public MenuItem createItem(MenuItemRequest request) {
		//create item and set up its prices
		SessionStore s = SessionStore.getInstance();
		
		Beverage bev = new Beverage(s.getMenuService().generateId("BEV"), request.getName(), request.getSubType(), request.getBasePrice());
		bev.setSizePrices(request.getSizePrices());
		bev.setIngredientRequirements(request.getIngredientRequirement());
		return bev;
	}
}
