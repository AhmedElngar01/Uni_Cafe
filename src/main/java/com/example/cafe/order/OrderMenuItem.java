package com.example.cafe.order;

import com.example.cafe.menu.IMenuItem;

public class OrderMenuItem implements IOrderMenuItem {
    IOrder order;
    IMenuItem menuItem;

    public OrderMenuItem(IOrder order, IMenuItem menuItem) {
        this.order = order;
        this.menuItem = menuItem;
    }

    public IOrder getOrder() {
        return order;
    }

    public IMenuItem getMenuItem() {
        return menuItem;
    }
}
