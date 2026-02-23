package com.company.userservice.presentation.controller;

import com.company.common.response.ApiResponse;
import com.company.common.response.PageResponse;
import com.company.userservice.application.dto.report.UserReportRow;
import com.company.userservice.application.dto.request.CreateUserRequest;
import com.company.userservice.application.dto.request.UpdateUserRequest;
import com.company.userservice.application.dto.response.UserResponse;
import com.company.userservice.application.port.in.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User created", response));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getById(id));
    }

    @GetMapping
    public PageResponse<UserResponse> getAll(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return userService.getAll(page, size);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.ok("User updated", userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok("User deleted", null);
    }

    @GetMapping("/reports/active")
    public ApiResponse<List<UserReportRow>> getActiveUsersReport() {
        return ApiResponse.ok("Active users", userService.getActiveUserReport());
    }
}
