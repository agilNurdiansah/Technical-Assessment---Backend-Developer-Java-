package com.company.userservice.application.port.in;

import com.company.common.response.PageResponse;
import com.company.userservice.application.dto.report.UserReportRow;
import com.company.userservice.application.dto.request.CreateUserRequest;
import com.company.userservice.application.dto.request.UpdateUserRequest;
import com.company.userservice.application.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse create(CreateUserRequest request);

    UserResponse getById(Long id);

    PageResponse<UserResponse> getAll(int page, int size);

    UserResponse update(Long id, UpdateUserRequest request);

    void delete(Long id);

    List<UserReportRow> getActiveUserReport();
}
