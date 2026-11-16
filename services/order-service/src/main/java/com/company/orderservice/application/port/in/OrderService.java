package com.company.orderservice.application.port.in;

import com.company.common.response.PageResponse;
import com.company.orderservice.application.dto.analytics.OrderAnalyticsResponse;
import com.company.orderservice.application.dto.report.OrderCumulativeReportRow;
import com.company.orderservice.application.dto.report.OrderReportRow;
import com.company.orderservice.application.dto.report.ProductPerformanceReportRow;
import com.company.orderservice.application.dto.report.UserRankingReportRow;
import com.company.orderservice.application.dto.request.CreateOrderRequest;
import com.company.orderservice.application.dto.request.UpdateOrderStatusRequest;
import com.company.orderservice.application.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request);

    OrderResponse getById(Long id);

    PageResponse<OrderResponse> getAll(int page, int size);

    List<OrderResponse> getByUserId(Long userId);

    OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request);

    void delete(Long id);

    // Basic native query
    List<OrderReportRow> getOrderSummaryByUser();

    // Intermediate Native SQL Queries (CTE, Window Functions, JOINs with HAVING)
    List<UserRankingReportRow> getUserSpendingRankings();

    List<OrderCumulativeReportRow> getCumulativeSpentReport();

    List<ProductPerformanceReportRow> getProductPerformanceReport(int minQuantity);

    // Java Stream API Analytics
    OrderAnalyticsResponse getAnalytics();
}
