package com.example.cafe.recommendation;

import com.example.cafe.menu.MenuItem;

import java.util.*;

public class RecommendationSystem {
    private Map<String, Double> qValues = new HashMap<>();
    private Random random = new Random();
    private double alpha = 0.1;   // learning rate
    private double gamma = 0.9;   // discount factor
    private double epsilon = 0.2; // exploration rate

    public RecommendationSystem(List<MenuItem> items) {
        for (MenuItem item : items) {
            qValues.put(item.getName(), 0.0);
        }
    }
    public void update(String chosenItem, String recommendedItem, double reward) {
        double oldValue = qValues.getOrDefault(recommendedItem, 0.0);
        double newValue = oldValue + alpha * (reward + gamma * getMaxQ() - oldValue);
        qValues.put(recommendedItem, newValue);
    }

    public MenuItem recommend(List<MenuItem> items) {
        if (random.nextDouble() < epsilon) {
            return items.get(random.nextInt(items.size())); // exploration
        }
        return items.stream()
                .max(Comparator.comparingDouble(i -> qValues.getOrDefault(i.getName(), 0.0)))
                .orElse(items.get(0));
    }

    private double getMaxQ() {
        return qValues.values().stream().max(Double::compare).orElse(0.0);
    }
}
