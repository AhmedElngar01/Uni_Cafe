
package com.example.cafe.order;

import java.util.List;

import com.example.cafe.student.IStudent;

public interface IOrderQuery {

    List<IOrder> getPendingOrders();

    List<IOrder> getPreparingOrders();

    List<IOrder> getAllOrders();

    IOrder getOrderById(int orderId);

    List<IOrder> getOrdersByStudent(IStudent student);
}