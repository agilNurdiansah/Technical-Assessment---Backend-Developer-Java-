package com.company.orderservice.infrastructure.client;

import com.company.common.response.ApiResponse;
import com.company.orderservice.infrastructure.client.dto.UserDto;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Microservices HTTP Client implementation using Spring 6 RestClient.
 * Handles inter-service communication with user-service.
 */
@Component
public class UserRestClientAdapter implements UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserRestClientAdapter.class);
    private final RestClient userRestClient;

    public UserRestClientAdapter(RestClient userRestClient) {
        this.userRestClient = userRestClient;
    }

    @Override
    public Optional<UserDto> getUserById(Long userId) {
        try {
            log.info("[MICROSERVICE CALL] Fetching user info from user-service for userId: {}", userId);
            ApiResponse<UserDto> response = userRestClient.get()
                .uri("/api/v1/users/{id}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    log.warn("[MICROSERVICE] User {} not found in user-service (status: {})", userId, resp.getStatusCode());
                })
                .body(new ParameterizedTypeReference<ApiResponse<UserDto>>() {});

            if (response != null && response.getData() != null) {
                return Optional.of(response.getData());
            }
        } catch (Exception e) {
            log.error("[MICROSERVICE ERROR] Failed to communicate with user-service for userId {}: {}", userId, e.getMessage());
        }
        return Optional.empty();
    }
}
