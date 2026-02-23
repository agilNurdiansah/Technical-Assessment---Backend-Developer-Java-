package com.company.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.company.orderservice.application.dto.request.CreateOrderItemRequest;
import com.company.orderservice.application.dto.request.CreateOrderRequest;
import com.company.orderservice.application.exception.NotFoundException;
import com.company.orderservice.application.port.in.OrderService;
import com.company.orderservice.application.port.out.OrderReportRepository;
import com.company.orderservice.application.port.out.OrderRepository;
import com.company.orderservice.application.service.OrderServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderReportRepository orderReportRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderReportRepository);
    }

    @Test
    void createOrder_calculatesTotal() {
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
        request.setItems(List.of(item1, item2));

        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.create(request);

        assertEquals(new BigDecimal("25.00"), response.getTotalAmount());
        assertEquals(2, response.getItems().size());
    }

    @Test
    void getById_notFound_throws() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getById(99L));
    }
}
