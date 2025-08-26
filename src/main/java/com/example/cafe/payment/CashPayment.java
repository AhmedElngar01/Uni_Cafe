package com.example.cafe.payment;

import com.example.cafe.student.IStudent;

public class CashPayment implements IPaymentMethod {
    @Override
    public boolean pay(double amount, IStudent student) {
        System.out.println("Thank you! Your cash payment of " + amount + " EGP has been received. Enjoy your meal!");
        return true;
    }
}
