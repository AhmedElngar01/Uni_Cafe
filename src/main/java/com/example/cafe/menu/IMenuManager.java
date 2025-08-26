package com.example.cafe.menu;

import java.util.List;

public interface IMenuManager {

    void addMenuItem(IMenuItem item);

    void editMenuItem(String name, IMenuItem newItem);

    void removeMenuItem(String name);

    List<IMenuItem> getTheMenu();

    IMenuItem getMenuItemByName(String name);
}
