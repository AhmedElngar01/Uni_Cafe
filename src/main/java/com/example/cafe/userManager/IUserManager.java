package com.example.cafe.userManager;

public interface IUserManager<T> {

    boolean register(String name, String id, String password);

    boolean login(String id, String password);

    void logout();

    T getLoggedInUser();

}