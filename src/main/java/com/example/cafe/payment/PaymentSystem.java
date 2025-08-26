package com.example.cafe.payment;

import com.example.cafe.student.IStudent;

public class PaymentSystem {
    public boolean processPayment(IPaymentMethod paymentMethod, double amount, IStudent student) {
        return paymentMethod.pay(amount, student);
    }
}