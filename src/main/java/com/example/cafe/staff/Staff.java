package com.example.cafe.staff;

import com.example.cafe.notification.INotificationSystem;
import com.example.cafe.order.IOrder;
import com.example.cafe.order.IOrderProcessor;
import com.example.cafe.student.IStudent;
import com.example.cafe.student.IStudentOrder;


public class Staff implements IStaff {

    private final IOrderProcessor orderProcessor;

    private final INotificationSystem notificationSystem;

    private boolean loggedIn = false;


    public Staff(IOrderProcessor orderProcessor, INotificationSystem notificationSystem) {
        this.orderProcessor = orderProcessor;
        this.notificationSystem = notificationSystem;
    }

    public boolean register(String name, String id, String password) {

        return false;
    }

    public boolean login(String username, String password) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            loggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedIn = false;
    }

    public Staff getLoggedInUser() {
        return loggedIn ? this : null;
    }

    public java.util.List<IOrder> getPendingOrders() {
        return orderProcessor.getPendingOrders();
    }
    public java.util.List<IOrder> getPreparingOrders() {
        return orderProcessor.getPreparingOrders();
    }

    public void markOrderPreparing(int orderId) {
        orderProcessor.markOrderPreparing(orderId);
        IStudent student = null;
        for (com.example.cafe.student.IStudentOrder so : (orderProcessor.getStudentOrderList())
                .getStudentOrders()) {
            if (so.getOrder().getOrderId() == orderId) {
                student = so.getStudent();
                break;
            }
        }
        if (student != null) {
            if (notificationSystem instanceof com.example.cafe.notification.NotificationSystem ns) {
                ns.notifyStudent("Order " + orderId + " for student " + student.getStudentId() + " is now Preparing.", student.getStudentId());
            }
        }
    }

    public void markOrderReady(int orderId) {
        orderProcessor.markOrderReady(orderId);
        IStudent student = null;
        for (IStudentOrder so : orderProcessor.getStudentOrderList().getStudentOrders()) {
            if (so.getOrder().getOrderId() == orderId) {
                student = so.getStudent();
                break;
            }
        }
        if (student != null) {
            if (notificationSystem instanceof com.example.cafe.notification.NotificationSystem ns) {
                ns.notifyStudent("Order " + orderId + " for student " + student.getStudentId() + " is Ready for Pickup!", student.getStudentId());
            }
        }
    }


    public IOrder getOrderById(int orderId) {
        return orderProcessor.getOrderById(orderId);
    }

}
