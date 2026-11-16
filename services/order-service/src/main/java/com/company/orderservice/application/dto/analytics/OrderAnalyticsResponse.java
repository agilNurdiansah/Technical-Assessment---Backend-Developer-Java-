package com.company.orderservice.application.dto.analytics;

import com.company.orderservice.domain.OrderStatus;
import java.math.BigDecimal;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

public class OrderAnalyticsResponse {
    private long totalOrdersCount;
    private BigDecimal totalRevenue;
    private Map<OrderStatus, Long> ordersCountByStatus;
    private Map<OrderStatus, BigDecimal> revenueByStatus;
    private Map<String, Long> partitionedByValue; // "HIGH_VALUE" vs "STANDARD"
    private OrderSummaryStatistics amountStatistics;
    private List<ProductSalesMetric> topSellingProducts;

    public OrderAnalyticsResponse() {
    }

    public long getTotalOrdersCount() {
        return totalOrdersCount;
    }

    public void setTotalOrdersCount(long totalOrdersCount) {
        this.totalOrdersCount = totalOrdersCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Map<OrderStatus, Long> getOrdersCountByStatus() {
        return ordersCountByStatus;
    }

    public void setOrdersCountByStatus(Map<OrderStatus, Long> ordersCountByStatus) {
        this.ordersCountByStatus = ordersCountByStatus;
    }

    public Map<OrderStatus, BigDecimal> getRevenueByStatus() {
        return revenueByStatus;
    }

    public void setRevenueByStatus(Map<OrderStatus, BigDecimal> revenueByStatus) {
        this.revenueByStatus = revenueByStatus;
    }

    public Map<String, Long> getPartitionedByValue() {
        return partitionedByValue;
    }

    public void setPartitionedByValue(Map<String, Long> partitionedByValue) {
        this.partitionedByValue = partitionedByValue;
    }

    public OrderSummaryStatistics getAmountStatistics() {
        return amountStatistics;
    }

    public void setAmountStatistics(OrderSummaryStatistics amountStatistics) {
        this.amountStatistics = amountStatistics;
    }

    public List<ProductSalesMetric> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<ProductSalesMetric> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }

    public static class OrderSummaryStatistics {
        private long count;
        private double sum;
        private double min;
        private double average;
        private double max;

        public OrderSummaryStatistics() {
        }

        public OrderSummaryStatistics(DoubleSummaryStatistics stats) {
            this.count = stats.getCount();
            this.sum = stats.getSum();
            this.min = stats.getCount() > 0 ? stats.getMin() : 0.0;
            this.average = stats.getAverage();
            this.max = stats.getCount() > 0 ? stats.getMax() : 0.0;
        }

        public long getCount() {
            return count;
        }

        public double getSum() {
            return sum;
        }

        public double getMin() {
            return min;
        }

        public double getAverage() {
            return average;
        }

        public double getMax() {
            return max;
        }
    }

    public static class ProductSalesMetric {
        private String productName;
        private int totalQuantity;

        public ProductSalesMetric() {
        }

        public ProductSalesMetric(String productName, int totalQuantity) {
            this.productName = productName;
            this.totalQuantity = totalQuantity;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }
    }
}
