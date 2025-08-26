package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public class FreeDonates implements IredeemPoints {
    @Override
    public boolean redeemPoints(IStudent student, ILoyaltyProgram loyaltyProgram) {
        if (loyaltyProgram.getLoyaltyPoints(student) >= 30) {
            loyaltyProgram.deductPoints(student, 30);
            loyaltyProgram.incrementRedemption(30);
            return true;
        }
        return false;
    }
}
