package com.example.cafe.order;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import com.example.cafe.menu.IMenuItem;

public class Order implements IOrder {
    // تستخدم فقط في deserialize حتى لا يتضاعف السعر
    public void addItemToOrderWithoutAffectingTotalPrice(IMenuItem item) {
        IOrderMenuItem orderMenuItem = new OrderMenuItem(this, item);
        orderMenuItemList.addMenuItemToOrderList(orderMenuItem);
    }

    private static boolean isDeserializing = false;
    private static int nextOrderId = 1;

    // Call this at startup to set nextOrderId from file
    public static void initializeOrderIdFromFile(String filePath) {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming each order line contains the orderId as the first field (e.g., CSV)
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        if (id > maxId)
                            maxId = id;
                    } catch (NumberFormatException e) {
                        // skip invalid lines
                    }
                }
            }
        } catch (IOException e) {
            // File may not exist yet, that's fine
        }
        nextOrderId = maxId + 1;
    }

    private final int orderId;
    private double totalPrice;
    private OrderState status;
    private final OrderMenuItemList orderMenuItemList;
    private boolean paid = false;
    private final LocalDate orderDate;
    private String studentId; // new field

    public Order() {
        this.orderId = nextOrderId++;
        this.status = OrderState.PENDING;
        this.orderMenuItemList = new OrderMenuItemList();
        this.orderDate = LocalDate.now();
        this.studentId = null;
    }

    // New constructor to set studentId
    public Order(String studentId) {
        this.orderId = nextOrderId++;
        this.status = OrderState.PENDING;
        this.orderMenuItemList = new OrderMenuItemList();
        this.orderDate = LocalDate.now();
        this.studentId = studentId;
    }

    @Override
    public boolean isPaid() {
        return paid;
    }

    @Override
    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Order(int orderId, double totalPrice, OrderState status, LocalDate orderDate, String studentId) {
        isDeserializing = true;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderMenuItemList = new OrderMenuItemList();
        this.orderDate = orderDate;
        this.studentId = studentId;
        isDeserializing = false;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public void addItemToOrder(IMenuItem item) {
        IOrderMenuItem orderMenuItem = new OrderMenuItem(this, item);
        orderMenuItemList.addMenuItemToOrderList(orderMenuItem);
        totalPrice += item.getPrice();
    }

    @Override
    public int getOrderId() {
        return orderId;
    }

    @Override
    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public OrderState getStatus() {
        return status;
    }

    @Override
    public void setStatus(OrderState status) {
        this.status = status;
    }

    @Override
    public OrderMenuItemList getOrderMenuItemList() {
        return orderMenuItemList;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }
}
