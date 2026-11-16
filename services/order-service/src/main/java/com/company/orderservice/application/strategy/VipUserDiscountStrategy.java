package com.company.orderservice.application.strategy;

import com.company.orderservice.domain.DiscountType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class VipUserDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal VIP_DISCOUNT_PERCENT = new BigDecimal("0.20");

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.VIP_TIER;
    }

    @Override
    public boolean supports(DiscountType type) {
        return type == DiscountType.VIP_TIER;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal baseTotal, int totalQuantity) {
        return baseTotal.multiply(VIP_DISCOUNT_PERCENT).setScale(2, RoundingMode.HALF_UP);
    }
}
