package com.company.orderservice.application.strategy;

import com.company.orderservice.domain.DiscountType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class PercentageDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal TEN_PERCENT = new BigDecimal("0.10");

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.PERCENTAGE_TEN;
    }

    @Override
    public boolean supports(DiscountType type) {
        return type == DiscountType.PERCENTAGE_TEN;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal baseTotal, int totalQuantity) {
        return baseTotal.multiply(TEN_PERCENT).setScale(2, RoundingMode.HALF_UP);
    }
}
