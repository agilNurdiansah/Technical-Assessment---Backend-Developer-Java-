package com.company.orderservice.application.strategy;

import com.company.orderservice.domain.DiscountType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class BulkPurchaseDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal BULK_DISCOUNT_PERCENT = new BigDecimal("0.15");

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.BULK_PURCHASE;
    }

    @Override
    public boolean supports(DiscountType type) {
        return type == DiscountType.BULK_PURCHASE;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal baseTotal, int totalQuantity) {
        if (totalQuantity >= 3) {
            return baseTotal.multiply(BULK_DISCOUNT_PERCENT).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
