package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public interface ILoyaltyRedemption {

    void incrementRedemption(int n);

    void setRedeemStrategy(IredeemPoints strategy);

    boolean redeemPoints(IStudent student);
}