package com.company.orderservice.application.dto.report;

import java.math.BigDecimal;

public class OrderReportRow {
    private Long userId;
    private Long orderCount;
    private BigDecimal totalAmount;

    public OrderReportRow() {
    }

    public OrderReportRow(Long userId, Long orderCount, BigDecimal totalAmount) {
        this.userId = userId;
        this.orderCount = orderCount;
        this.totalAmount = totalAmount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
