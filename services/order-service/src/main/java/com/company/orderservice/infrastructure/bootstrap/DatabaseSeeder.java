package com.company.orderservice.infrastructure.bootstrap;

import com.company.orderservice.domain.DiscountType;
import com.company.orderservice.domain.Order;
import com.company.orderservice.domain.OrderItem;
import com.company.orderservice.domain.OrderStatus;
import com.company.orderservice.infrastructure.persistence.jpa.OrderJpaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final OrderJpaRepository orderRepository;

    public DatabaseSeeder(OrderJpaRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (orderRepository.count() > 0) {
            return;
        }

        // Order 1: User 1 - NEW
        Order first = new Order();
        first.setUserId(1L);
        first.setStatus(OrderStatus.NEW);
        first.setDiscountType(DiscountType.NONE);
        first.setDiscountAmount(BigDecimal.ZERO);
        applyItems(first, List.of(
            buildItem("Wireless Mouse", 2, new BigDecimal("25.00")),
            buildItem("USB-C Hub", 1, new BigDecimal("45.00"))
        ));

        // Order 2: User 1 - PAID
        Order second = new Order();
        second.setUserId(1L);
        second.setStatus(OrderStatus.PAID);
        second.setDiscountType(DiscountType.PERCENTAGE_TEN);
        second.setDiscountAmount(new BigDecimal("30.00"));
        applyItems(second, List.of(
            buildItem("Mechanical Keyboard", 1, new BigDecimal("120.00")),
            buildItem("4K Monitor", 1, new BigDecimal("180.00"))
        ));

        // Order 3: User 2 - PAID
        Order third = new Order();
        third.setUserId(2L);
        third.setStatus(OrderStatus.PAID);
        third.setDiscountType(DiscountType.BULK_PURCHASE);
        third.setDiscountAmount(new BigDecimal("15.00"));
        applyItems(third, List.of(
            buildItem("Desk Mat", 3, new BigDecimal("15.00")),
            buildItem("Wireless Mouse", 1, new BigDecimal("25.00"))
        ));

        // Order 4: User 2 - COMPLETED
        Order fourth = new Order();
        fourth.setUserId(2L);
        fourth.setStatus(OrderStatus.COMPLETED);
        fourth.setDiscountType(DiscountType.NONE);
        fourth.setDiscountAmount(BigDecimal.ZERO);
        applyItems(fourth, List.of(
            buildItem("Ergonomic Chair", 1, new BigDecimal("250.00")),
            buildItem("Monitor Stand", 2, new BigDecimal("35.00"))
        ));

        orderRepository.saveAll(List.of(first, second, third, fourth));
    }

    private static OrderItem buildItem(String name, int quantity, BigDecimal price) {
        OrderItem item = new OrderItem();
        item.setProductName(name);
        item.setQuantity(quantity);
        item.setPrice(price);
        return item;
    }

    private static void applyItems(Order order, List<OrderItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            order.addItem(item);
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        order.setTotalAmount(total.subtract(discount).max(BigDecimal.ZERO));
    }
}
