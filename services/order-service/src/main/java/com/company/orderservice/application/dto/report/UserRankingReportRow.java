package com.company.orderservice.application.dto.report;

import java.math.BigDecimal;

/**
 * DTO for Intermediate Native SQL Query using CTE (Common Table Expression)
 * and Window Function DENSE_RANK().
 */
public class UserRankingReportRow {
    private Long userId;
    private long totalOrders;
    private BigDecimal totalSpent;
    private BigDecimal avgOrderAmount;
    private long spendingRank;

    public UserRankingReportRow() {
    }

    public UserRankingReportRow(Long userId, long totalOrders, BigDecimal totalSpent, BigDecimal avgOrderAmount, long spendingRank) {
        this.userId = userId;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.avgOrderAmount = avgOrderAmount;
        this.spendingRank = spendingRank;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getAvgOrderAmount() {
        return avgOrderAmount;
    }

    public void setAvgOrderAmount(BigDecimal avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }

    public long getSpendingRank() {
        return spendingRank;
    }

    public void setSpendingRank(long spendingRank) {
        this.spendingRank = spendingRank;
    }
}
