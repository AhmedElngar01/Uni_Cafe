package com.example.cafe.student;

import com.example.cafe.order.IOrder;

public class StudentOrder implements IStudentOrder {
    private final IStudent student;
    private final IOrder order;

    public StudentOrder(IStudent student, IOrder order) {
        this.student = student;
        this.order = order;
    }

    @Override
    public IStudent getStudent() {
        return student;
    }

    @Override
    public IOrder getOrder() {
        return order;
    }
}
