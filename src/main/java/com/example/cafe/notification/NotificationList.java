package com.example.cafe.notification;
import java.util.ArrayList;
import java.util.List;

public class NotificationList {
    List<String> notifications;

    public NotificationList() {
        this.notifications = new ArrayList<>();
    }

    public void addToNotificationList(String notification) {
        this.notifications.add(notification);
    }

    public List<String> getNotificationsCopy() {
        return new ArrayList<>(notifications);
    }
}