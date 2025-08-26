package com.example.cafe.order;

import com.example.cafe.menu.IMenuItem;

public interface IOrderMenuItem {
    IMenuItem getMenuItem();

    IOrder getOrder();
}