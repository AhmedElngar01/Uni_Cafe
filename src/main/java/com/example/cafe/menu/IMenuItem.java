package com.example.cafe.menu;

public interface IMenuItem {
    double getPrice();

    String getName();

    String getDescription();

    MenuItemCategory getCategory();

}
