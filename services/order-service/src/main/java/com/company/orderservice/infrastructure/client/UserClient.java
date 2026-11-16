package com.company.orderservice.infrastructure.client;

import com.company.orderservice.infrastructure.client.dto.UserDto;
import java.util.Optional;

/**
 * Microservices client port for inter-service communication with user-service.
 */
public interface UserClient {
    Optional<UserDto> getUserById(Long userId);
}
