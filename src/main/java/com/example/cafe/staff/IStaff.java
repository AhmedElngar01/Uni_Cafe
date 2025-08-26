package com.example.cafe.staff;

import java.util.List;

import com.example.cafe.order.IOrder;
import com.example.cafe.userManager.IUserManager;

public interface IStaff extends IUserManager<IStaff> {

    String ADMIN_USERNAME = "admin";

    String ADMIN_PASSWORD = "admin";

    IOrder getOrderById(int orderId);

    void markOrderPreparing(int orderId);

    void markOrderReady(int orderId);

    List<IOrder> getPendingOrders();

    List<IOrder> getPreparingOrders();
}
