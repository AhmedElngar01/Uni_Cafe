package com.example.cafe.payment;

import com.example.cafe.student.IStudent;

public class MobilePayment implements IPaymentMethod {
    @Override
    public boolean pay(double amount, IStudent student) {
        System.out.println("Thank you! Your mobile payment of " + amount + " EGP was processed successfully. Enjoy your meal!");
        return true;
    }
}
