package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public interface IredeemPoints {

    boolean redeemPoints(IStudent student, ILoyaltyProgram loyaltyProgram);

}