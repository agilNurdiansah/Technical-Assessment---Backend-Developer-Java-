package com.company.orderservice.infrastructure.listener;

import com.company.orderservice.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Spring IoC Event Listener for audit trail tracking.
 */
@Component
public class OrderAuditEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderAuditEventListener.class);

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[IoC EVENT - AUDIT] Order #{} created for userId: {} with amount: ${}",
            event.getOrderId(), event.getUserId(), event.getTotalAmount());
    }
}
