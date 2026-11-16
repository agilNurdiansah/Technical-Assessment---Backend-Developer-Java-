package com.company.orderservice.application.service;

import com.company.common.response.PageResponse;
import com.company.orderservice.application.dto.analytics.OrderAnalyticsResponse;
import com.company.orderservice.application.dto.report.OrderCumulativeReportRow;
import com.company.orderservice.application.dto.report.OrderReportRow;
import com.company.orderservice.application.dto.report.ProductPerformanceReportRow;
import com.company.orderservice.application.dto.report.UserRankingReportRow;
import com.company.orderservice.application.dto.request.CreateOrderRequest;
import com.company.orderservice.application.dto.request.UpdateOrderStatusRequest;
import com.company.orderservice.application.dto.response.OrderResponse;
import com.company.orderservice.application.exception.BadRequestException;
import com.company.orderservice.application.exception.NotFoundException;
import com.company.orderservice.application.mapper.OrderMapper;
import com.company.orderservice.application.port.in.OrderService;
import com.company.orderservice.application.port.out.OrderReportRepository;
import com.company.orderservice.application.port.out.OrderRepository;
import com.company.orderservice.application.strategy.DiscountStrategy;
import com.company.orderservice.application.strategy.DiscountStrategyFactory;
import com.company.orderservice.domain.DiscountType;
import com.company.orderservice.domain.Order;
import com.company.orderservice.domain.OrderItem;
import com.company.orderservice.domain.event.OrderCreatedEvent;
import com.company.orderservice.infrastructure.client.UserClient;
import com.company.orderservice.infrastructure.client.dto.UserDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderReportRepository orderReportRepository;
    private final UserClient userClient;
    private final DiscountStrategyFactory discountStrategyFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderAnalyticsService orderAnalyticsService;

    public OrderServiceImpl(
        OrderRepository orderRepository,
        OrderReportRepository orderReportRepository,
        UserClient userClient,
        DiscountStrategyFactory discountStrategyFactory,
        ApplicationEventPublisher eventPublisher,
        OrderAnalyticsService orderAnalyticsService
    ) {
        this.orderRepository = orderRepository;
        this.orderReportRepository = orderReportRepository;
        this.userClient = userClient;
        this.discountStrategyFactory = discountStrategyFactory;
        this.eventPublisher = eventPublisher;
        this.orderAnalyticsService = orderAnalyticsService;
    }

    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order items are required");
        }

        // 1. Microservices Inter-Service Communication: Validate user in user-service
        Optional<UserDto> userOpt = userClient.getUserById(request.getUserId());
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User with id " + request.getUserId() + " does not exist or is unreachable");
        }
        UserDto user = userOpt.get();
        if (!user.isActive()) {
            throw new BadRequestException("User " + request.getUserId() + " is currently inactive. Order cannot be placed.");
        }

        // 2. Java Stream: Map request items to entity list
        List<OrderItem> items = request.getItems().stream()
            .map(OrderMapper::toItemEntity)
            .collect(Collectors.toList());

        // 3. Java Stream: Compute subtotal and total item quantity using Stream reduction
        BigDecimal subtotal = items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantity = items.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();

        // 4. Spring IoC: Resolve Discount Strategy dynamically from Spring Container
        DiscountType requestedType = request.getDiscountType() != null ? request.getDiscountType() : DiscountType.NONE;
        DiscountStrategy discountStrategy = discountStrategyFactory.getStrategy(requestedType);
        BigDecimal discountAmount = discountStrategy.calculateDiscount(subtotal, totalQuantity);
        BigDecimal finalTotal = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);

        log.info("[IoC STRATEGY] Applied strategy: {} for user: {}, discount: ${}, finalTotal: ${}",
            discountStrategy.getDiscountType(), request.getUserId(), discountAmount, finalTotal);

        // 5. Construct Order entity
        Order order = OrderMapper.toEntity(request);
        order.setDiscountType(discountStrategy.getDiscountType());
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(finalTotal);

        for (OrderItem item : items) {
            order.addItem(item);
        }

        Order saved = orderRepository.save(order);

        // 6. Spring IoC Event Publishing: publish domain event to Spring IoC container
        eventPublisher.publishEvent(new OrderCreatedEvent(
            saved.getId(),
            saved.getUserId(),
            saved.getTotalAmount(),
            saved.getCreatedAt()
        ));

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
    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(request.getStatus());
        Order saved = orderRepository.save(order);
        return OrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderReportRow> getOrderSummaryByUser() {
        return orderReportRepository.findOrderSummaryByUser();
    }

    @Override
    public List<UserRankingReportRow> getUserSpendingRankings() {
        return orderReportRepository.findUserSpendingRankings();
    }

    @Override
    public List<OrderCumulativeReportRow> getCumulativeSpentReport() {
        return orderReportRepository.findCumulativeSpentReport();
    }

    @Override
    public List<ProductPerformanceReportRow> getProductPerformanceReport(int minQuantity) {
        return orderReportRepository.findProductPerformance(minQuantity);
    }

    @Override
    public OrderAnalyticsResponse getAnalytics() {
        return orderAnalyticsService.calculateAnalytics();
    }
}
