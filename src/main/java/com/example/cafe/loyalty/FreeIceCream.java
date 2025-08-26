package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public class FreeIceCream implements IredeemPoints {
    @Override
    public boolean redeemPoints(IStudent student, ILoyaltyProgram loyaltyProgram) {
        if (loyaltyProgram.getLoyaltyPoints(student) >= 15) {
            loyaltyProgram.deductPoints(student, 15);
            loyaltyProgram.incrementRedemption(15);
            return true;
        }
        return false;
    }
}
