package com.company.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.company.orderservice.application.dto.request.CreateOrderItemRequest;
import com.company.orderservice.application.dto.request.CreateOrderRequest;
import com.company.orderservice.application.exception.BadRequestException;
import com.company.orderservice.application.exception.NotFoundException;
import com.company.orderservice.application.port.in.OrderService;
import com.company.orderservice.application.port.out.OrderReportRepository;
import com.company.orderservice.application.port.out.OrderRepository;
import com.company.orderservice.application.service.OrderAnalyticsService;
import com.company.orderservice.application.service.OrderServiceImpl;
import com.company.orderservice.application.strategy.BulkPurchaseDiscountStrategy;
import com.company.orderservice.application.strategy.DiscountStrategyFactory;
import com.company.orderservice.application.strategy.NoDiscountStrategy;
import com.company.orderservice.application.strategy.PercentageDiscountStrategy;
import com.company.orderservice.application.strategy.VipUserDiscountStrategy;
import com.company.orderservice.domain.DiscountType;
import com.company.orderservice.domain.event.OrderCreatedEvent;
import com.company.orderservice.infrastructure.client.UserClient;
import com.company.orderservice.infrastructure.client.dto.UserDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderReportRepository orderReportRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private OrderAnalyticsService orderAnalyticsService;

    private DiscountStrategyFactory discountStrategyFactory;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        discountStrategyFactory = new DiscountStrategyFactory(List.of(
            new NoDiscountStrategy(),
            new PercentageDiscountStrategy(),
            new BulkPurchaseDiscountStrategy(),
            new VipUserDiscountStrategy()
        ));

        orderService = new OrderServiceImpl(
            orderRepository,
            orderReportRepository,
            userClient,
            discountStrategyFactory,
            eventPublisher,
            orderAnalyticsService
        );
    }

    @Test
    void createOrder_calculatesTotalWithNoDiscount() {
        CreateOrderItemRequest item1 = new CreateOrderItemRequest();
        item1.setProductName("Keyboard");
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("10.00"));

        CreateOrderItemRequest item2 = new CreateOrderItemRequest();
        item2.setProductName("Mouse");
        item2.setQuantity(1);
        item2.setPrice(new BigDecimal("5.00"));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setDiscountType(DiscountType.NONE);
        request.setItems(List.of(item1, item2));

        when(userClient.getUserById(1L))
            .thenReturn(Optional.of(new UserDto(1L, "Alice", "alice@example.com", "ACTIVE")));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.create(request);

        assertEquals(new BigDecimal("25.00"), response.getTotalAmount());
        assertEquals(BigDecimal.ZERO, response.getDiscountAmount());
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_appliesPercentageDiscountStrategy() {
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(new BigDecimal("100.00"));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setDiscountType(DiscountType.PERCENTAGE_TEN);
        request.setItems(List.of(item));

        when(userClient.getUserById(1L))
            .thenReturn(Optional.of(new UserDto(1L, "Alice", "alice@example.com", "ACTIVE")));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.create(request);

        assertEquals(new BigDecimal("90.00"), response.getTotalAmount());
        assertEquals(new BigDecimal("10.00"), response.getDiscountAmount());
    }

    @Test
    void createOrder_rejectsWhenUserNotFound() {
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(new BigDecimal("100.00"));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(999L);
        request.setItems(List.of(item));

        when(userClient.getUserById(999L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> orderService.create(request));
    }

    @Test
    void createOrder_rejectsWhenUserInactive() {
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(new BigDecimal("100.00"));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(2L);
        request.setItems(List.of(item));

        when(userClient.getUserById(2L))
            .thenReturn(Optional.of(new UserDto(2L, "Bob", "bob@example.com", "INACTIVE")));

        assertThrows(BadRequestException.class, () -> orderService.create(request));
    }

    @Test
    void getById_throwsNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getById(99L));
    }
}
