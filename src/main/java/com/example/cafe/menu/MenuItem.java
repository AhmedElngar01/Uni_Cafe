package com.example.cafe.menu;

public class MenuItem implements IMenuItem {
    private final String name;
    private final String description;
    private final double price;
    private final MenuItemCategory category;

    public MenuItem(String name, String description, double price, MenuItemCategory category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public MenuItemCategory getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

}
