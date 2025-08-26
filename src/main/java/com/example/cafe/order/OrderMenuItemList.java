package com.example.cafe.order;

import java.util.ArrayList;
import java.util.List;

public class OrderMenuItemList {
    private final List<IOrderMenuItem> orderMenuItemList;

    public OrderMenuItemList() {
        this.orderMenuItemList = new ArrayList<>();
    }

    public void addMenuItemToOrderList(IOrderMenuItem orderMenuItem) {
        this.orderMenuItemList.add(orderMenuItem);
    }

    public List<IOrderMenuItem> getOrderMenuItems() {
        return orderMenuItemList;
    }
}