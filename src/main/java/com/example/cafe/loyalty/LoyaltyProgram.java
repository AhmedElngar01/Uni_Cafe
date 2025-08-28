package com.example.cafe.loyalty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.cafe.student.IStudent;

public class LoyaltyProgram implements ILoyaltyProgram {
    private static final String POINTS_FILE = getDataFilePath("loyalty_points.txt");

    private static String getDataFilePath(String fileName) {
        String basePath = System.getProperty("user.dir") + "/src/dataFiles/";
        return basePath + fileName;
    }

    private int totalRedemptions = 0;
    private int totalRedeemedPoints = 0;
    private IredeemPoints redeemStrategy;
    private final List<RedemptionRecord> redemptionRecords = new ArrayList<>();
    private final Map<String, Integer> studentPoints = new HashMap<>();
    private static final String REDEMPTION_FILE = getDataFilePath("redemption_records.txt");
    // Load redemption records from file
    public void loadRedemptionRecordsFromFile() {
        redemptionRecords.clear();
        try {
            java.io.File file = new java.io.File(REDEMPTION_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        java.time.LocalDate date = java.time.LocalDate.parse(parts[0]);
                        int points = Integer.parseInt(parts[1]);
                        redemptionRecords.add(new RedemptionRecord(date, points));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading redemption records: " + e.getMessage());
        }
    }

    // Save redemption records to file
    public void saveRedemptionRecordsToFile() {
        try {
            java.io.File file = new java.io.File(REDEMPTION_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file))) {
                for (RedemptionRecord record : redemptionRecords) {
                    writer.write(record.date + "," + record.points);
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving redemption records: " + e.getMessage());
        }
    }

    public void loadPointsFromFile() {
        studentPoints.clear();
        try {
            java.io.File file = new java.io.File(POINTS_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        studentPoints.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading loyalty points: " + e.getMessage());
        }
    }

    public void savePointsToFile() {
        try {
            java.io.File file = new java.io.File(POINTS_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file))) {
                for (Map.Entry<String, Integer> entry : studentPoints.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving loyalty points: " + e.getMessage());
        }
    }

    @Override
    public void addPoints(IStudent student, int points) {
        String studentId = student.getStudentId();
        int current = studentPoints.getOrDefault(studentId, 0);
        studentPoints.put(studentId, current + points);
        savePointsToFile();
    }

    @Override
    public void deductPoints(IStudent student, int points) {
        String studentId = student.getStudentId();
        int current = studentPoints.getOrDefault(studentId, 0);
        studentPoints.put(studentId, Math.max(0, current - points));
        savePointsToFile();
    }

    @Override
    public void awardPoints(IStudent student, double orderTotal) {
        int points = (int) orderTotal / 10;
        addPoints(student, points);
    }

    @Override

    public int getLoyaltyPoints(IStudent student) {
        loadPointsFromFile();
        return studentPoints.getOrDefault(student.getStudentId(), 0);
    }

    public void incrementRedemption(int points) {
    totalRedemptions++;
    totalRedeemedPoints += points;
    redemptionRecords.add(new RedemptionRecord(java.time.LocalDate.now(), points));
    saveRedemptionRecordsToFile();
    }

    public List<RedemptionRecord> getRedemptionRecords() {
    loadRedemptionRecordsFromFile();
    return redemptionRecords;
    }

    public int getTotalRedemptions() {
        return totalRedemptions;
    }

    public int getTotalRedeemedPoints() {
        return totalRedeemedPoints;
    }

    public void setRedeemStrategy(IredeemPoints strategy) {
        this.redeemStrategy = strategy;
    }

    public boolean redeemPoints(IStudent student) {
        if (redeemStrategy == null)
            return false;
        return redeemStrategy.redeemPoints(student, this);
    }

}
