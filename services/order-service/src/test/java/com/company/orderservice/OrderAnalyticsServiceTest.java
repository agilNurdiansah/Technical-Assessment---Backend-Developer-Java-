package com.company.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.company.orderservice.application.dto.analytics.OrderAnalyticsResponse;
import com.company.orderservice.application.port.out.OrderRepository;
import com.company.orderservice.application.service.OrderAnalyticsService;
import com.company.orderservice.application.service.OrderAnalyticsServiceImpl;
import com.company.orderservice.domain.Order;
import com.company.orderservice.domain.OrderItem;
import com.company.orderservice.domain.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderAnalyticsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderAnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new OrderAnalyticsServiceImpl(orderRepository);
    }

    @Test
    @DisplayName("Java Stream API: Should calculate analytics with groupingBy, partitioningBy, summarizing and reduce")
    void shouldCalculateStreamAnalytics() {
        Order o1 = new Order();
        o1.setId(1L);
        o1.setStatus(OrderStatus.NEW);
        o1.setTotalAmount(new BigDecimal("100.00"));

        OrderItem item1 = new OrderItem();
        item1.setProductName("Laptop");
        item1.setQuantity(1);
        item1.setPrice(new BigDecimal("100.00"));
        o1.addItem(item1);

        Order o2 = new Order();
        o2.setId(2L);
        o2.setStatus(OrderStatus.PAID);
        o2.setTotalAmount(new BigDecimal("40.00"));

        OrderItem item2 = new OrderItem();
        item2.setProductName("Mouse");
        item2.setQuantity(2);
        item2.setPrice(new BigDecimal("20.00"));
        o2.addItem(item2);

        Order o3 = new Order();
        o3.setId(3L);
        o3.setStatus(OrderStatus.PAID);
        o3.setTotalAmount(new BigDecimal("60.00"));

        OrderItem item3 = new OrderItem();
        item3.setProductName("Laptop");
        item3.setQuantity(1);
        item3.setPrice(new BigDecimal("60.00"));
        o3.addItem(item3);

        when(orderRepository.findAll()).thenReturn(List.of(o1, o2, o3));

        OrderAnalyticsResponse response = analyticsService.calculateAnalytics();

        assertNotNull(response);
        assertEquals(3, response.getTotalOrdersCount());
        // Stream reduce test: 100 + 40 + 60 = 200.00
        assertEquals(new BigDecimal("200.00"), response.getTotalRevenue());

        // Stream groupingBy test:
        assertEquals(1L, response.getOrdersCountByStatus().get(OrderStatus.NEW));
        assertEquals(2L, response.getOrdersCountByStatus().get(OrderStatus.PAID));
        assertEquals(new BigDecimal("100.00"), response.getRevenueByStatus().get(OrderStatus.PAID)); // 40 + 60

        // Stream partitioningBy test:
        // High Value (>= $50): o1 ($100), o3 ($60) -> count 2
        // Standard (< $50): o2 ($40) -> count 1
        assertEquals(2L, response.getPartitionedByValue().get("HIGH_VALUE"));
        assertEquals(1L, response.getPartitionedByValue().get("STANDARD"));

        // Stream summarizingDouble test:
        assertEquals(200.0, response.getAmountStatistics().getSum());
        assertEquals(40.0, response.getAmountStatistics().getMin());
        assertEquals(100.0, response.getAmountStatistics().getMax());

        // Stream flatMap leaderboard: Laptop total = 2, Mouse total = 2
        assertEquals(2, response.getTopSellingProducts().size());
    }
}
