package com.example.cafe.menu;

import com.example.cafe.dataSerializer.IDataSerializer;

public class MenuItemSerializer implements IDataSerializer<IMenuItem> {
    @Override
    public String serialize(IMenuItem obj) {
        return obj.getName() + "," + obj.getDescription() + "," + obj.getPrice() + "," + obj.getCategory();
    }

    @Override
    public IMenuItem deserialize(String str) {
        String[] parts = str.split(",");
        if (parts.length < 4)
            return null;
        try {
            return new MenuItem(parts[0], parts[1], Double.parseDouble(parts[2]), MenuItemCategory.valueOf(parts[3]));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getId(IMenuItem obj) {
        return obj.getName();
    }
}