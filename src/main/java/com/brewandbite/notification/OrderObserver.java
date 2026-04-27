package com.brewandbite.notification;

public interface OrderObserver {
    void onOrderEvent(OrderEvent event);
}
