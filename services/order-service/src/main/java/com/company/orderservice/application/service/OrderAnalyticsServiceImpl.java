package com.company.orderservice.application.service;

import com.company.orderservice.application.dto.analytics.OrderAnalyticsResponse;
import com.company.orderservice.application.port.out.OrderRepository;
import com.company.orderservice.domain.Order;
import com.company.orderservice.domain.OrderItem;
import com.company.orderservice.domain.OrderStatus;
import java.math.BigDecimal;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service showcasing advanced and idiomatic Java Stream API (Middle Level Requirement).
 * Demonstrates:
 * - groupingBy
 * - partitioningBy
 * - summarizingDouble
 * - stream reduction with reduce()
 * - flatMap across nested item collections
 * - pipeline sorting and limiting
 */
@Service
public class OrderAnalyticsServiceImpl implements OrderAnalyticsService {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("50.00");
    private final OrderRepository orderRepository;

    public OrderAnalyticsServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderAnalyticsResponse calculateAnalytics() {
        List<Order> orders = orderRepository.findAll();

        OrderAnalyticsResponse response = new OrderAnalyticsResponse();
        response.setTotalOrdersCount(orders.size());

        if (orders.isEmpty()) {
            response.setTotalRevenue(BigDecimal.ZERO);
            response.setOrdersCountByStatus(Map.of());
            response.setRevenueByStatus(Map.of());
            response.setPartitionedByValue(Map.of("HIGH_VALUE", 0L, "STANDARD", 0L));
            response.setAmountStatistics(new OrderAnalyticsResponse.OrderSummaryStatistics(new DoubleSummaryStatistics()));
            response.setTopSellingProducts(List.of());
            return response;
        }

        // 1. Stream Reduction: Total revenue across all orders
        BigDecimal totalRevenue = orders.stream()
            .map(Order::getTotalAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalRevenue(totalRevenue);

        // 2. Stream groupingBy: Order count by OrderStatus
        Map<OrderStatus, Long> countByStatus = orders.stream()
            .collect(Collectors.groupingBy(
                Order::getStatus,
                Collectors.counting()
            ));
        response.setOrdersCountByStatus(countByStatus);

        // 3. Stream groupingBy with reducing: Revenue by OrderStatus
        Map<OrderStatus, BigDecimal> revenueByStatus = orders.stream()
            .collect(Collectors.groupingBy(
                Order::getStatus,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    Order::getTotalAmount,
                    BigDecimal::add
                )
            ));
        response.setRevenueByStatus(revenueByStatus);

        // 4. Stream partitioningBy: High Value Orders (>= $50) vs Standard Orders
        Map<Boolean, Long> partitioned = orders.stream()
            .collect(Collectors.partitioningBy(
                order -> order.getTotalAmount() != null && order.getTotalAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0,
                Collectors.counting()
            ));
        Map<String, Long> partitionResult = new LinkedHashMap<>();
        partitionResult.put("HIGH_VALUE", partitioned.getOrDefault(true, 0L));
        partitionResult.put("STANDARD", partitioned.getOrDefault(false, 0L));
        response.setPartitionedByValue(partitionResult);

        // 5. Stream summarizingDouble: statistical distribution of order amounts
        DoubleSummaryStatistics stats = orders.stream()
            .map(Order::getTotalAmount)
            .filter(amount -> amount != null)
            .collect(Collectors.summarizingDouble(BigDecimal::doubleValue));
        response.setAmountStatistics(new OrderAnalyticsResponse.OrderSummaryStatistics(stats));

        // 6. Stream flatMap & groupingBy & summingInt: Product sales leaderboard
        List<OrderAnalyticsResponse.ProductSalesMetric> topProducts = orders.stream()
            .filter(order -> order.getItems() != null)
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(
                OrderItem::getProductName,
                Collectors.summingInt(OrderItem::getQuantity)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .map(entry -> new OrderAnalyticsResponse.ProductSalesMetric(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        response.setTopSellingProducts(topProducts);

        return response;
    }
}
