
package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public class FreeCoffee implements IredeemPoints {
    @Override
    public boolean redeemPoints(IStudent student, ILoyaltyProgram loyaltyProgram) {
        if (loyaltyProgram.getLoyaltyPoints(student) >= 10) {
            loyaltyProgram.deductPoints(student, 10);
            loyaltyProgram.incrementRedemption(10);
            return true;
        }
        return false;
    }
}