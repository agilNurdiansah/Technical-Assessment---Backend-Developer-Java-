package com.company.orderservice.application.service;

import com.company.orderservice.application.dto.analytics.OrderAnalyticsResponse;

public interface OrderAnalyticsService {
    OrderAnalyticsResponse calculateAnalytics();
}
