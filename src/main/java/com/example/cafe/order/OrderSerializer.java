package com.example.cafe.order;

import com.example.cafe.dataSerializer.IDataSerializer;

public class OrderSerializer implements IDataSerializer<IOrder> {
    @Override
    public String serialize(IOrder obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof Order o) {
            // Serialize order items as names separated by ;
            StringBuilder itemsBuilder = new StringBuilder();
            java.util.Map<String, Integer> itemCount = new java.util.HashMap<>();
            for (com.example.cafe.order.IOrderMenuItem omi : o.getOrderMenuItemList().getOrderMenuItems()) {
                String name = omi.getMenuItem().getName();
                itemCount.put(name, itemCount.getOrDefault(name, 0) + 1);
            }
            for (String name : itemCount.keySet()) {
                itemsBuilder.append(name).append(":").append(itemCount.get(name)).append(";");
            }
            // Add studentId and items to serialization
            return o.getOrderId() + "," + o.getTotalPrice() + "," + o.getStatus() + "," + o.getOrderDate() + ","
                    + o.isPaid() + "," + (o.getStudentId() != null ? o.getStudentId() : "") + ","
                    + itemsBuilder.toString();
        }
        // fallback for other IOrder implementations
        try {
            return obj.getOrderId() + "," + obj.getTotalPrice() + "," + obj.getStatus() + ",," + obj.isPaid();
        } catch (Exception e) {
            return String.valueOf(obj.getOrderId());
        }
    }

    @Override
    public IOrder deserialize(String str) {
        String[] parts = str.split(",");
        if (parts.length < 7)
            return null;
        int orderId = Integer.parseInt(parts[0]);
        double totalPrice = Double.parseDouble(parts[1]);
        OrderState status = OrderState.valueOf(parts[2]);
        java.time.LocalDate orderDate = java.time.LocalDate.parse(parts[3]);
        boolean isPaid = Boolean.parseBoolean(parts[4]);
        String studentId = parts[5];
        String itemsStr = parts[6];
        Order order = new Order(orderId, totalPrice, status, orderDate, studentId);
        order.setPaid(isPaid);
        // Rebuild order items from itemsStr
        if (itemsStr != null && !itemsStr.isEmpty()) {
            String[] itemsArr = itemsStr.split(";");
            com.example.cafe.menu.MenuManager menuManager = new com.example.cafe.menu.MenuManager();
            for (String itemPair : itemsArr) {
                if (itemPair.isEmpty())
                    continue;
                String[] nameCount = itemPair.split(":");
                if (nameCount.length == 2) {
                    String name = nameCount[0];
                    int count = Integer.parseInt(nameCount[1]);
                    com.example.cafe.menu.IMenuItem menuItem = menuManager.getMenuItemByName(name);
                    if (menuItem != null) {
                        for (int i = 0; i < count; i++) {
                            if (order instanceof com.example.cafe.order.Order o) {
                                o.addItemToOrderWithoutAffectingTotalPrice(menuItem);
                            }
                        }
                    }
                }
            }
        }
        return order;
    }

    @Override
    public String getId(IOrder obj) {
        return String.valueOf(obj.getOrderId());
    }
}
