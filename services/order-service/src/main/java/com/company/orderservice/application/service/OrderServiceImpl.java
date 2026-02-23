package com.company.orderservice.application.service;

import com.company.common.response.PageResponse;
import com.company.orderservice.application.dto.report.OrderReportRow;
import com.company.orderservice.application.dto.request.CreateOrderRequest;
import com.company.orderservice.application.dto.request.UpdateOrderStatusRequest;
import com.company.orderservice.application.dto.response.OrderResponse;
import com.company.orderservice.application.exception.BadRequestException;
import com.company.orderservice.application.exception.NotFoundException;
import com.company.orderservice.application.mapper.OrderMapper;
import com.company.orderservice.application.port.in.OrderService;
import com.company.orderservice.application.port.out.OrderReportRepository;
import com.company.orderservice.application.port.out.OrderRepository;
import com.company.orderservice.domain.Order;
import com.company.orderservice.domain.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderReportRepository orderReportRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderReportRepository orderReportRepository) {
        this.orderRepository = orderRepository;
        this.orderReportRepository = orderReportRepository;
    }

    @Override
    public OrderResponse create(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order items are required");
        }

        Order order = OrderMapper.toEntity(request);
        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.getItems()) {
            OrderItem item = OrderMapper.toItemEntity(itemRequest);
            order.addItem(item);
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return OrderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));
        return OrderMapper.toResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> getAll(int page, int size) {
        Page<Order> pageData = orderRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        List<OrderResponse> items = pageData.getContent().stream()
            .map(OrderMapper::toResponse)
            .collect(Collectors.toList());
        return new PageResponse<>(items, page, size, pageData.getTotalElements());
    }

    @Override
    public List<OrderResponse> getByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
            .map(OrderMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(request.getStatus());
        Order saved = orderRepository.save(order);
        return OrderMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderReportRow> getOrderSummaryByUser() {
        return orderReportRepository.findOrderSummaryByUser();
    }
}
