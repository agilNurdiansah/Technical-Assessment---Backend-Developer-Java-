package com.company.orderservice.application.strategy;

import com.company.orderservice.domain.DiscountType;
import java.math.BigDecimal;

/**
 * Strategy interface for calculating order discounts.
 * Concrete implementations are managed as Spring beans (Spring IoC).
 */
public interface DiscountStrategy {
    DiscountType getDiscountType();
    boolean supports(DiscountType type);
    BigDecimal calculateDiscount(BigDecimal baseTotal, int totalQuantity);
}
