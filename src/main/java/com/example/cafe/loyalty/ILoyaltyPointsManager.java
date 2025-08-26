package com.example.cafe.loyalty;

import com.example.cafe.student.IStudent;

public interface ILoyaltyPointsManager {

    void awardPoints(IStudent student, double orderTotal);

    void addPoints(IStudent student, int points);

    void deductPoints(IStudent student, int points);

}