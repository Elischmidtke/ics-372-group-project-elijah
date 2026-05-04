package com.brewandbite.notification;

import com.brewandbite.model.Order;

public class OrderEvent {
    private final OrderEventType type;
    private final Order order;
    private final String message;

    public OrderEvent(OrderEventType type, Order order, String message) {
        this.type = type;
        this.order = order;
        this.message = message;
    }

    public OrderEventType getType() { 
    	return type; 
    }
    public Order getOrder() { 
    	return order; 
    }
    public String getMessage() { 
    	return message; 
    }
}