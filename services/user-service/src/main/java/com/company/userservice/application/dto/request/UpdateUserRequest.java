package com.company.userservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.company.userservice.domain.UserStatus;

public class UpdateUserRequest {
    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    private UserStatus status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
