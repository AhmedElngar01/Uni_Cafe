package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public class FreePizza implements IredeemPoints {
    @Override
    public boolean redeemPoints(IStudent student, ILoyaltyProgram loyaltyProgram) {
        if (loyaltyProgram.getLoyaltyPoints(student) >= 40) {
            loyaltyProgram.deductPoints(student, 40);
            loyaltyProgram.incrementRedemption(40);
            return true;
        }
        return false;
    }
}
