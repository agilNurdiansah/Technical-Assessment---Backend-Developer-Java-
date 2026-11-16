package com.company.orderservice.application.strategy;

import com.company.orderservice.domain.DiscountType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Factory demonstrating Spring IoC (Inversion of Control) and Strategy Pattern.
 * Spring automatically collects and injects all DiscountStrategy beans into the constructor.
 */
@Component
public class DiscountStrategyFactory {

    private final Map<DiscountType, DiscountStrategy> strategyMap;
    private final DiscountStrategy defaultStrategy;

    public DiscountStrategyFactory(List<DiscountStrategy> strategies) {
        this.strategyMap = strategies.stream()
            .collect(Collectors.toMap(
                DiscountStrategy::getDiscountType,
                strategy -> strategy,
                (existing, replacement) -> existing
            ));
        this.defaultStrategy = strategyMap.getOrDefault(DiscountType.NONE, new NoDiscountStrategy());
    }

    public DiscountStrategy getStrategy(DiscountType type) {
        if (type == null) {
            return defaultStrategy;
        }
        return Optional.ofNullable(strategyMap.get(type))
            .orElse(defaultStrategy);
    }
}
