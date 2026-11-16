package com.company.orderservice.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain event published through Spring ApplicationEventPublisher (Spring IoC Event mechanism).
 */
public class OrderCreatedEvent {
    private final Long orderId;
    private final Long userId;
    private final BigDecimal totalAmount;
    private final Instant createdAt;

    public OrderCreatedEvent(Long orderId, Long userId, BigDecimal totalAmount, Instant createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
            "orderId=" + orderId +
            ", userId=" + userId +
            ", totalAmount=" + totalAmount +
            ", createdAt=" + createdAt +
            '}';
    }
}
