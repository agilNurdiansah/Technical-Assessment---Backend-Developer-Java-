package com.company.orderservice.application.dto.report;

import java.math.BigDecimal;

/**
 * DTO for Intermediate Native SQL Query using Multi-table JOIN with Aggregations and HAVING clause.
 */
public class ProductPerformanceReportRow {
    private String productName;
    private long ordersCount;
    private long totalQuantitySold;
    private BigDecimal totalRevenue;
    private BigDecimal averagePrice;

    public ProductPerformanceReportRow() {
    }

    public ProductPerformanceReportRow(String productName, long ordersCount, long totalQuantitySold, BigDecimal totalRevenue, BigDecimal averagePrice) {
        this.productName = productName;
        this.ordersCount = ordersCount;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
        this.averagePrice = averagePrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(long ordersCount) {
        this.ordersCount = ordersCount;
    }

    public long getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(long totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }
}
