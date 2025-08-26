package com.example.cafe.order;

import com.example.cafe.menu.IMenuItem;

public interface IOrder {
    boolean isPaid();

    void setPaid(boolean paid);

    OrderMenuItemList getOrderMenuItemList();

    void addItemToOrder(IMenuItem item);

    int getOrderId();

    double getTotalPrice();

    OrderState getStatus();

    void setStatus(OrderState status);
}
