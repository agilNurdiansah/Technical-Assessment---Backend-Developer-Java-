package com.company.orderservice.application.port.in;

import com.company.common.response.PageResponse;
import com.company.orderservice.application.dto.report.OrderReportRow;
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

    List<OrderReportRow> getOrderSummaryByUser();
}
