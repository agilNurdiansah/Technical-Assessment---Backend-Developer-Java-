package com.company.userservice.application.mapper;

import com.company.userservice.domain.User;
import com.company.userservice.application.dto.request.CreateUserRequest;
import com.company.userservice.application.dto.request.UpdateUserRequest;
import com.company.userservice.application.dto.response.UserResponse;

public final class UserMapper {
    private UserMapper() {
    }

    public static User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        return user;
    }

    public static void updateEntity(User user, UpdateUserRequest request) {
        user.setName(request.getName());
        user.setStatus(request.getStatus());
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
