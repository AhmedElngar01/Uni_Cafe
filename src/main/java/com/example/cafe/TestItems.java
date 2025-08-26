package com.example.cafe;

public class TestItems {
    int id;
    String name;
    String price;
    String category;
    String Description;

    public TestItems(int id, String description, String category, String price, String name) {
        this.id = id;
        Description = description;
        this.category = category;
        this.price = price;
        this.name = name;
    }

    public int getID() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

