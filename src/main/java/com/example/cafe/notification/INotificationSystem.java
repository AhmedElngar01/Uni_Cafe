package com.example.cafe.notification;

import java.util.List;

public interface INotificationSystem {

    void notifyStudent(String message);

    List<String> getNotifications();

    void clearNotifications();
}