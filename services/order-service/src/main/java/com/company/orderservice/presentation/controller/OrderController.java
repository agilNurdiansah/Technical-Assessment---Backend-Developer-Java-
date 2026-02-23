package com.company.orderservice.presentation.controller;

import com.company.common.response.ApiResponse;
import com.company.common.response.PageResponse;
import com.company.orderservice.application.dto.report.OrderReportRow;
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

    @GetMapping("/reports/by-user")
    public ApiResponse<List<OrderReportRow>> getOrderSummaryByUser() {
        return ApiResponse.ok("Order summary by user", orderService.getOrderSummaryByUser());
    }
}
