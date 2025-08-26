package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public class Discount implements IredeemPoints {
    private final int discountAmount;
    private final int requiredPoints;

    public Discount(int discountAmount, int requiredPoints) {
        this.discountAmount = discountAmount;
        this.requiredPoints = requiredPoints;
    }

    @Override
    public boolean redeemPoints(IStudent student, ILoyaltyProgram loyaltyProgram) {
        int currentPoints = loyaltyProgram.getLoyaltyPoints(student);
        if (currentPoints >= requiredPoints) {
            loyaltyProgram.deductPoints(student, requiredPoints);
            loyaltyProgram.incrementRedemption(requiredPoints);
            return true;
        }
        return false;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }
}