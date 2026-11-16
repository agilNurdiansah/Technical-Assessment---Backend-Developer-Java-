package com.company.orderservice.application.mapper;

import com.company.orderservice.domain.Order;
import com.company.orderservice.domain.OrderItem;
import com.company.orderservice.domain.OrderStatus;
import com.company.orderservice.application.dto.request.CreateOrderItemRequest;
import com.company.orderservice.application.dto.request.CreateOrderRequest;
import com.company.orderservice.application.dto.response.OrderItemResponse;
import com.company.orderservice.application.dto.response.OrderResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public final class OrderMapper {
    private OrderMapper() {
    }

    public static Order toEntity(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.NEW);
        order.setDiscountType(request.getDiscountType() != null ? request.getDiscountType() : com.company.orderservice.domain.DiscountType.NONE);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(BigDecimal.ZERO);
        return order;
    }

    public static OrderItem toItemEntity(CreateOrderItemRequest request) {
        OrderItem item = new OrderItem();
        item.setProductName(request.getProductName());
        item.setQuantity(request.getQuantity());
        item.setPrice(request.getPrice());
        return item;
    }

    public static OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountType(order.getDiscountType());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponse> items = order.getItems().stream()
            .map(item -> {
                OrderItemResponse itemResponse = new OrderItemResponse();
                itemResponse.setProductName(item.getProductName());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setPrice(item.getPrice());
                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                itemResponse.setSubtotal(subtotal);
                return itemResponse;
            })
            .collect(Collectors.toList());

        response.setItems(items);
        return response;
    }
}
