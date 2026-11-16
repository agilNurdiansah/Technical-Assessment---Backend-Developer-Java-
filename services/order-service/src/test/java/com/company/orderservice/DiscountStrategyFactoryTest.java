package com.company.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.company.orderservice.application.strategy.BulkPurchaseDiscountStrategy;
import com.company.orderservice.application.strategy.DiscountStrategy;
import com.company.orderservice.application.strategy.DiscountStrategyFactory;
import com.company.orderservice.application.strategy.NoDiscountStrategy;
import com.company.orderservice.application.strategy.PercentageDiscountStrategy;
import com.company.orderservice.application.strategy.VipUserDiscountStrategy;
import com.company.orderservice.domain.DiscountType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiscountStrategyFactoryTest {

    private DiscountStrategyFactory factory;

    @BeforeEach
    void setUp() {
        // Simulating Spring IoC container injecting all strategy beans
        List<DiscountStrategy> strategies = List.of(
            new NoDiscountStrategy(),
            new PercentageDiscountStrategy(),
            new BulkPurchaseDiscountStrategy(),
            new VipUserDiscountStrategy()
        );
        factory = new DiscountStrategyFactory(strategies);
    }

    @Test
    @DisplayName("Spring IoC Strategy: Should resolve NoDiscountStrategy when NONE is selected")
    void shouldResolveNoDiscount() {
        DiscountStrategy strategy = factory.getStrategy(DiscountType.NONE);
        assertTrue(strategy instanceof NoDiscountStrategy);
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(new BigDecimal("100.00"), 1));
    }

    @Test
    @DisplayName("Spring IoC Strategy: Should resolve PercentageDiscountStrategy (10% off)")
    void shouldResolvePercentageDiscount() {
        DiscountStrategy strategy = factory.getStrategy(DiscountType.PERCENTAGE_TEN);
        assertTrue(strategy instanceof PercentageDiscountStrategy);
        assertEquals(new BigDecimal("10.00"), strategy.calculateDiscount(new BigDecimal("100.00"), 1));
    }

    @Test
    @DisplayName("Spring IoC Strategy: Should resolve BulkPurchaseDiscountStrategy for 3+ items")
    void shouldResolveBulkDiscount() {
        DiscountStrategy strategy = factory.getStrategy(DiscountType.BULK_PURCHASE);
        assertTrue(strategy instanceof BulkPurchaseDiscountStrategy);
        assertEquals(new BigDecimal("15.00"), strategy.calculateDiscount(new BigDecimal("100.00"), 3));
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(new BigDecimal("100.00"), 2));
    }

    @Test
    @DisplayName("Spring IoC Strategy: Should resolve VipUserDiscountStrategy (20% off)")
    void shouldResolveVipDiscount() {
        DiscountStrategy strategy = factory.getStrategy(DiscountType.VIP_TIER);
        assertTrue(strategy instanceof VipUserDiscountStrategy);
        assertEquals(new BigDecimal("20.00"), strategy.calculateDiscount(new BigDecimal("100.00"), 1));
    }
}
