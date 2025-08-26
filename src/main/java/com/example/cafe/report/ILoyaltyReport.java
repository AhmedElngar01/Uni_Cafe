package com.example.cafe.report;

import java.time.LocalDate;

public interface ILoyaltyReport {
    int getTotalRedemptions();

    int getTotalRedeemedPoints();

    int getDailyRedemptions(LocalDate date);

    int getDailyRedeemedPoints(LocalDate date);

    int getWeeklyRedemptions(LocalDate anyDayOfWeek);

    int getWeeklyRedeemedPoints(LocalDate anyDayOfWeek);
}