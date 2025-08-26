package com.example.cafe.order;

import com.example.cafe.student.IStudent;

public interface IOrderPlacement {

    void placeOrder(IOrder order, IStudent student);

}