
package com.example.cafe.order;

import java.util.ArrayList;
import java.util.List;
import com.example.cafe.loyalty.ILoyaltyProgram;
import com.example.cafe.repository.FileRepository;
import com.example.cafe.repository.IRepository;
import com.example.cafe.student.IStudent;
import com.example.cafe.student.IStudentOrder;
import com.example.cafe.student.StudentOrder;
import com.example.cafe.student.StudentOrderList;

public class OrderProcessor implements IOrderProcessor {
    private final StudentOrderList studentOrderList;
    private final ILoyaltyProgram loyaltyProgram;

    public void reloadOrdersFromFile() {
        java.util.List<IOrder> allOrders = orderRepo.getAll();
        java.util.List<IStudentOrder> updatedStudentOrders = new java.util.ArrayList<>();
        for (IOrder order : allOrders) {
            // Find matching student for this order
            for (IStudentOrder so : studentOrderList.getStudentOrders()) {
                if (so.getOrder() != null && so.getOrder().getOrderId() == order.getOrderId()) {
                    // Create new StudentOrder with updated order
                    updatedStudentOrders.add(new StudentOrder(so.getStudent(), order));
                }
            }
        }
        // Replace studentOrderList with updated list
        studentOrderList.getStudentOrders().clear();
        studentOrderList.getStudentOrders().addAll(updatedStudentOrders);
    }

    public void payOrder(int orderId) {
        IOrder order = getOrderById(orderId);
        if (order != null && !order.isPaid()) {
            order.setPaid(true);
            orderRepo.save(order);
        }
    }
    private final IRepository<IOrder> orderRepo = new FileRepository<>(getDataFilePath("orders.txt"),
            new OrderSerializer());

    private static String getDataFilePath(String fileName) {
        String basePath = System.getProperty("user.dir") + "/src/dataFiles/";
        return basePath + fileName;
    }

    public OrderProcessor(ILoyaltyProgram loyaltyProgram, StudentOrderList studentOrderList) {
        this.loyaltyProgram = loyaltyProgram;
        this.studentOrderList = studentOrderList;
    }

    public ILoyaltyProgram getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public StudentOrderList getStudentOrderList() {
        return studentOrderList;
    }

    @Override
    public void placeOrder(IOrder order, IStudent student) {
        if (order instanceof Order) {
            orderRepo.save(order);
        }
        IStudentOrder studentOrder = new StudentOrder(student, order);
        studentOrderList.addStudentOrder(studentOrder);

    }

    @Override
    public List<IOrder> getPendingOrders() {
        List<IOrder> pending = new ArrayList<>();
        for (IOrder order : orderRepo.getAll()) {
            if (order.getStatus() == OrderState.PENDING) {
                pending.add(order);
            }
        }
        return pending;
    }

    public List<IOrder> getPreparingOrders() {
        List<IOrder> preparing = new ArrayList<>();
        for (IOrder order : orderRepo.getAll()) {
            if (order.getStatus() == OrderState.PREPARING) {
                preparing.add(order);
            }
        }
        return preparing;
    }

    @Override
    public List<IOrder> getAllOrders() {
        List<IOrder> orders = orderRepo.getAll();
        return new ArrayList<>(orders);
    }

    @Override
    public IOrder getOrderById(int orderId) {
        IOrder order = orderRepo.getById(String.valueOf(orderId));
        if (order != null) {
            return order;
        }
        for (IStudentOrder so : studentOrderList.getStudentOrders()) {
            IOrder studentOrder = so.getOrder();
            if (studentOrder != null && studentOrder.getOrderId() == orderId) {
                return studentOrder;
            }
        }
        return null;
    }

    @Override
    public List<IOrder> getOrdersByStudent(IStudent student) {
        List<IOrder> studentOrdersList = new ArrayList<>();
        List<IOrder> allOrders = orderRepo.getAll();
        for (IOrder order : allOrders) {
            if (order instanceof Order o) {
                if (o.getStudentId() != null && o.getStudentId().equals(student.getStudentId())) {
                    studentOrdersList.add(order);
                }
            }
        }
        return studentOrdersList;
    }

    public void markOrderPreparing(int orderId) {
        IOrder order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(OrderState.PREPARING);
            orderRepo.save(order);

        }
    }

    public void markOrderReady(int orderId) {
        IOrder order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(OrderState.READY_FOR_PICKUP);
            orderRepo.save(order);
        }
    }
}