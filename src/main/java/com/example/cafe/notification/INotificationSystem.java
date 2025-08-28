package com.example.cafe.notification;

import java.util.List;

public interface INotificationSystem {

    void notifyStudent(String message, String studentId);

    java.util.List<String> getNotifications(String studentId);

}