package com.example.cafe.payment;


import com.example.cafe.student.IStudent;

public class CreditCardPayment implements IPaymentMethod {
    @Override
    public boolean pay(double amount, IStudent student) {
        System.out.println("Thank you! Your credit card payment of " + amount + " EGP was completed successfully.Have a wonderful day!");
        return true;
    }
}
