package com.example.cafe.loyalty;

import java.time.LocalDate;

public class RedemptionRecord {
    public final LocalDate date;
    public final int points;

    public RedemptionRecord(LocalDate date, int points) {
        this.date = date;
        this.points = points;
    }
}

