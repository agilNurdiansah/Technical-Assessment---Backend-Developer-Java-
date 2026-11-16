package com.company.orderservice.application.dto.report;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for Intermediate Native SQL Query using Window Functions:
 * ROW_NUMBER() and SUM() OVER (PARTITION BY ... ORDER BY ... ROWS BETWEEN ...).
 */
public class OrderCumulativeReportRow {
    private Long orderId;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private long orderSequence;
    private BigDecimal cumulativeSpent;

    public OrderCumulativeReportRow() {
    }

    public OrderCumulativeReportRow(Long orderId, Long userId, String status, BigDecimal totalAmount, Instant createdAt, long orderSequence, BigDecimal cumulativeSpent) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.orderSequence = orderSequence;
        this.cumulativeSpent = cumulativeSpent;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public long getOrderSequence() {
        return orderSequence;
    }

    public void setOrderSequence(long orderSequence) {
        this.orderSequence = orderSequence;
    }

    public BigDecimal getCumulativeSpent() {
        return cumulativeSpent;
    }

    public void setCumulativeSpent(BigDecimal cumulativeSpent) {
        this.cumulativeSpent = cumulativeSpent;
    }
}
