package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public class FreeWaffel implements IredeemPoints {
    @Override
    public boolean redeemPoints(IStudent student, ILoyaltyProgram loyaltyProgram) {
        if (loyaltyProgram.getLoyaltyPoints(student) >= 20) {
            loyaltyProgram.deductPoints(student, 20);
            loyaltyProgram.incrementRedemption(20);
            return true;
        }
        return false;
    }
}
