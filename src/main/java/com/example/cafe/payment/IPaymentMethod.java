package com.example.cafe.payment;

import com.example.cafe.student.IStudent;

public interface IPaymentMethod {
    boolean pay(double amount, IStudent student);
}