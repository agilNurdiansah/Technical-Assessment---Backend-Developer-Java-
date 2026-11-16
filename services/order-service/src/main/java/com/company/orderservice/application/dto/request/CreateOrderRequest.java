package com.company.orderservice.application.dto.request;

import com.company.orderservice.domain.DiscountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateOrderRequest {
    @NotNull
    private Long userId;

    private DiscountType discountType = DiscountType.NONE;

    @Valid
    @Size(min = 1)
    private List<CreateOrderItemRequest> items;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequest> items) {
        this.items = items;
    }
}
