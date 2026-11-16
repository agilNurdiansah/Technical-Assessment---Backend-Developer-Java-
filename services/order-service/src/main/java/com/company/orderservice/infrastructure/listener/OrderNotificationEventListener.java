package com.company.orderservice.infrastructure.listener;

import com.company.orderservice.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Spring IoC Event Listener simulating asynchronous notification dispatch.
 */
@Component
public class OrderNotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationEventListener.class);

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[IoC EVENT - NOTIFICATION] Dispatching confirmation notification to customer {} for order #{}",
            event.getUserId(), event.getOrderId());
    }
}
