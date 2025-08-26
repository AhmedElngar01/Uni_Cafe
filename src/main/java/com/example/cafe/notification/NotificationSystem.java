package com.example.cafe.notification;

import java.util.List;

public class NotificationSystem implements INotificationSystem {
    private final NotificationList notificationList;

    public NotificationSystem() {
        this.notificationList = new NotificationList();
    }

    @Override
    public void notifyStudent(String message) {
        notificationList.addToNotificationList(message);
    }

    @Override
    public List<String> getNotifications() {
        return notificationList.getNotificationsCopy();
    }

    @Override
    public void clearNotifications() {
        notificationList.notifications.clear();
    }
}