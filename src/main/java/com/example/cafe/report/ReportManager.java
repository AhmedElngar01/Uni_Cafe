
package com.example.cafe.report;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import com.example.cafe.loyalty.ILoyaltyProgram;
import com.example.cafe.loyalty.LoyaltyProgram;
import com.example.cafe.order.IOrder;
import com.example.cafe.order.IOrderProcessor;

public class ReportManager implements IReportManager {
    private double totalSales = 0;

    private final IOrderProcessor orderProcessor;

    private final ILoyaltyProgram loyaltyProgram;

    public ReportManager(IOrderProcessor orderProcessor, ILoyaltyProgram loyaltyProgram) {
        this.orderProcessor = orderProcessor;
        this.loyaltyProgram = loyaltyProgram;
    }

    @Override
    public void addToTotalSales(double amount) {
        totalSales += amount;
    }

    @Override
    public int getTotalRedemptions() {
        if (loyaltyProgram instanceof LoyaltyProgram lp) {
            return lp.getTotalRedemptions();
        }
        return 0;
    }

    @Override
    public int getTotalRedeemedPoints() {
        if (loyaltyProgram instanceof LoyaltyProgram lp) {
            return lp.getTotalRedeemedPoints();
        }
        return 0;
    }

    @Override
    public double getTotalSales() {
        return totalSales;
    }

    @Override
    public double getDailySales(LocalDate date) {
        double sum = 0;
        List<IOrder> orders = orderProcessor.getAllOrders();
        for (IOrder order : orders) {
            if (order instanceof com.example.cafe.order.Order o) {
                if (o.getOrderDate().equals(date) && o.isPaid()) {
                    sum += o.getTotalPrice();
                }
            }
        }
        return sum;
    }

    @Override
    public double getWeeklySales(LocalDate anyDayOfWeek) {
        double sum = 0;
        List<IOrder> orders = orderProcessor.getAllOrders();
        LocalDate startOfWeek = anyDayOfWeek.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        for (IOrder order : orders) {
            if (order instanceof com.example.cafe.order.Order o) {
                LocalDate d = o.getOrderDate();
                if ((d.isEqual(startOfWeek) || d.isAfter(startOfWeek))
                        && (d.isEqual(endOfWeek) || d.isBefore(endOfWeek))
                        && o.isPaid()) {
                    sum += o.getTotalPrice();
                }
            }
        }
        return sum;
    }

    @Override
    public int getDailyRedemptions(LocalDate date) {
        if (loyaltyProgram instanceof LoyaltyProgram lp) {
            int count = 0;
            for (com.example.cafe.loyalty.RedemptionRecord r : lp.getRedemptionRecords()) {
                if (r.date.equals(date))
                    count++;
            }
            return count;
        }
        return 0;
    }

    @Override
    public int getDailyRedeemedPoints(LocalDate date) {
        if (loyaltyProgram instanceof LoyaltyProgram lp) {
            int points = 0;
            for (com.example.cafe.loyalty.RedemptionRecord r : lp.getRedemptionRecords()) {
                if (r.date.equals(date))
                    points += r.points;
            }
            return points;
        }
        return 0;
    }

    @Override
    public int getWeeklyRedemptions(LocalDate anyDayOfWeek) {
        if (loyaltyProgram instanceof LoyaltyProgram lp) {
            int count = 0;
            LocalDate startOfWeek = anyDayOfWeek.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            for (com.example.cafe.loyalty.RedemptionRecord r : lp.getRedemptionRecords()) {
                if ((r.date.isEqual(startOfWeek) || r.date.isAfter(startOfWeek))
                        && (r.date.isEqual(endOfWeek) || r.date.isBefore(endOfWeek))) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    @Override
    public int getWeeklyRedeemedPoints(LocalDate anyDayOfWeek) {
        if (loyaltyProgram instanceof LoyaltyProgram lp) {
            int points = 0;
            LocalDate startOfWeek = anyDayOfWeek.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            for (com.example.cafe.loyalty.RedemptionRecord r : lp.getRedemptionRecords()) {
                if ((r.date.isEqual(startOfWeek) || r.date.isAfter(startOfWeek))
                        && (r.date.isEqual(endOfWeek) || r.date.isBefore(endOfWeek))) {
                    points += r.points;
                }
            }
            return points;
        }
        return 0;
    }

}