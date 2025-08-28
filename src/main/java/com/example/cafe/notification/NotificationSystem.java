package com.example.cafe.notification;

import java.util.List;

public class NotificationSystem implements INotificationSystem {
    private static final String FILE_PATH = "src/dataFiles/notifications.txt";

    @Override
    public void notifyStudent(String message, String studentId) {
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(FILE_PATH),
                    (studentId + "," + message + System.lineSeparator()).getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public java.util.List<String> getNotifications(String studentId) {
        java.util.List<String> result = new java.util.ArrayList<>();
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(FILE_PATH));
            for (String line : lines) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2 && parts[0].equals(studentId)) {
                    result.add(parts[1]);
                }
            }
        } catch (Exception e) {

        }
        return result;
    }
}