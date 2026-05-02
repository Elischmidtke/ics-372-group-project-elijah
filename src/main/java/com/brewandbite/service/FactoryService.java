package com.brewandbite.service;

import java.util.Map;

import com.brewandbite.model.*;

public class FactoryService {
	private static FactoryService factoryService = null;
	private Map<String, MenuItemFactory> factories = Map.of(
	        "Pastry", new PastryFactory(),
	        "Beverage", new BeverageFactory());
	
	private FactoryService() {
		
	}
	
	//get the singleton
	public static FactoryService getInstance() {
		if(factoryService == null) {
			return new FactoryService();
		}
		
		return factoryService;
	}
	
	//create item
	public MenuItem createItem(MenuItemRequest request) {
		MenuItemFactory factory = factories.get(request.getType());
		return factory.createItem(request);
	}
}
