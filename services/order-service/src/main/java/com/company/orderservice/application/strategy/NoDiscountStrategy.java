package com.company.orderservice.application.strategy;

import com.company.orderservice.domain.DiscountType;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.NONE;
    }

    @Override
    public boolean supports(DiscountType type) {
        return type == null || type == DiscountType.NONE;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal baseTotal, int totalQuantity) {
        return BigDecimal.ZERO;
    }
}
