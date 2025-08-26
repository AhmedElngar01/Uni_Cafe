package com.example.cafe.report;

import java.time.LocalDate;

public interface ISalesReport {
    double getTotalSales();

    void addToTotalSales(double amount);

    double getDailySales(LocalDate date);

    double getWeeklySales(LocalDate anyDayOfWeek);
}
