
package com.example.cafe.menu;

import java.util.List;
import com.example.cafe.repository.FileRepository;
import com.example.cafe.repository.IRepository;

public class MenuManager implements IMenuManager {

    private final IRepository<IMenuItem> menuRepo = new FileRepository<>(getDataFilePath("menuitems.txt"),
            new MenuItemSerializer());

    private static String getDataFilePath(String fileName) {
        String basePath = System.getProperty("user.dir") + "/src/dataFiles/";
        return basePath + fileName;
    }

    @Override
    public void addMenuItem(IMenuItem item) {
        if (item instanceof MenuItem) {
            menuRepo.save(item);
        }
    }

    @Override
    public void editMenuItem(String name, IMenuItem newItem) {
        IMenuItem oldItem = menuRepo.getById(name);
        if (oldItem != null) {
            menuRepo.delete(oldItem.getName());//////
            menuRepo.save(newItem);
        }
    }

    @Override
    public void removeMenuItem(String name) {
        menuRepo.delete(name);
    }

    @Override
    public List<IMenuItem> getTheMenu() {
        return menuRepo.getAll();
    }

    @Override
    public IMenuItem getMenuItemByName(String name) {
        return menuRepo.getById(name);
    }
}
