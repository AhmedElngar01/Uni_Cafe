package com.example.cafe.loyalty;

public interface ILoyaltyProgram extends ILoyaltyPointsManager, ILoyaltyRedemption {

    int getLoyaltyPoints(com.example.cafe.student.IStudent student);

}