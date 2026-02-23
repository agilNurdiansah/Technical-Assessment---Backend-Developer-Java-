package com.company.orderservice.infrastructure.bootstrap;

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

        Order first = new Order();
        first.setUserId(1L);
        first.setStatus(OrderStatus.NEW);
        List<OrderItem> firstItems = List.of(
            buildItem("Wireless Mouse", 2, new BigDecimal("19.99")),
            buildItem("USB-C Cable", 1, new BigDecimal("9.50"))
        );
        applyItems(first, firstItems);

        Order second = new Order();
        second.setUserId(2L);
        second.setStatus(OrderStatus.PAID);
        List<OrderItem> secondItems = List.of(
            buildItem("Notebook", 3, new BigDecimal("4.25")),
            buildItem("Pen Set", 1, new BigDecimal("7.80"))
        );
        applyItems(second, secondItems);

        orderRepository.saveAll(List.of(first, second));
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
        order.setTotalAmount(total);
    }
}
