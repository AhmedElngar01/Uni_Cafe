package com.example.cafe.order;

public interface IOrderProcessor extends IOrderPlacement, IOrderQuery {

    com.example.cafe.student.StudentOrderList getStudentOrderList();
    public void markOrderPreparing(int orderId);
    public void markOrderReady(int orderId);

}