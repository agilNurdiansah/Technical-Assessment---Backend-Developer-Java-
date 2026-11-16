package com.company.orderservice.presentation.controller;

import com.company.common.response.ApiResponse;
import com.company.common.response.PageResponse;
import com.company.orderservice.application.dto.analytics.OrderAnalyticsResponse;
import com.company.orderservice.application.dto.report.OrderCumulativeReportRow;
import com.company.orderservice.application.dto.report.OrderReportRow;
import com.company.orderservice.application.dto.report.ProductPerformanceReportRow;
import com.company.orderservice.application.dto.report.UserRankingReportRow;
import com.company.orderservice.application.dto.request.CreateOrderRequest;
import com.company.orderservice.application.dto.request.UpdateOrderStatusRequest;
import com.company.orderservice.application.dto.response.OrderResponse;
import com.company.orderservice.application.port.in.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Order created", response));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(orderService.getById(id));
    }

    @GetMapping
    public PageResponse<OrderResponse> getAll(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return orderService.getAll(page, size);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderResponse>> getByUserId(@PathVariable Long userId) {
        return ApiResponse.ok(orderService.getByUserId(userId));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ApiResponse.ok("Order status updated", orderService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ApiResponse.ok("Order deleted", null);
    }

    // --- Junior Level Report ---
    @GetMapping("/reports/by-user")
    public ApiResponse<List<OrderReportRow>> getOrderSummaryByUser() {
        return ApiResponse.ok("Order summary by user", orderService.getOrderSummaryByUser());
    }

    // --- Middle Level Requirement 1: Intermediate Native SQL (CTE + Window Function DENSE_RANK) ---
    @GetMapping("/reports/user-rankings")
    public ApiResponse<List<UserRankingReportRow>> getUserSpendingRankings() {
        return ApiResponse.ok("User spending rankings (CTE + DENSE_RANK window function)", orderService.getUserSpendingRankings());
    }

    // --- Middle Level Requirement 2: Intermediate Native SQL (Window Functions ROW_NUMBER & Running Cumulative Sum) ---
    @GetMapping("/reports/cumulative-spent")
    public ApiResponse<List<OrderCumulativeReportRow>> getCumulativeSpentReport() {
        return ApiResponse.ok("Customer cumulative spending progression (Window functions)", orderService.getCumulativeSpentReport());
    }

    // --- Middle Level Requirement 3: Intermediate Native SQL (Multi-table JOIN + HAVING) ---
    @GetMapping("/reports/product-performance")
    public ApiResponse<List<ProductPerformanceReportRow>> getProductPerformanceReport(
        @RequestParam(defaultValue = "1") @Min(1) int minQuantity
    ) {
        return ApiResponse.ok("Product performance report (JOIN + HAVING)", orderService.getProductPerformanceReport(minQuantity));
    }

    // --- Middle Level Requirement 4: Java Stream API Analytics ---
    @GetMapping("/analytics/summary")
    public ApiResponse<OrderAnalyticsResponse> getAnalyticsSummary() {
        return ApiResponse.ok("Order analytics summary (Java Stream API: groupingBy, partitioningBy, summarize, reduce)", orderService.getAnalytics());
    }
}
