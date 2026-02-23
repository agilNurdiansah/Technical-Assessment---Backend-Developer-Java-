package com.company.userservice.application.service;

import com.company.common.response.PageResponse;
import com.company.userservice.application.dto.report.UserReportRow;
import com.company.userservice.application.dto.request.CreateUserRequest;
import com.company.userservice.application.dto.request.UpdateUserRequest;
import com.company.userservice.application.dto.response.UserResponse;
import com.company.userservice.application.exception.BadRequestException;
import com.company.userservice.application.exception.NotFoundException;
import com.company.userservice.application.mapper.UserMapper;
import com.company.userservice.application.port.in.UserService;
import com.company.userservice.application.port.out.UserReportRepository;
import com.company.userservice.application.port.out.UserRepository;
import com.company.userservice.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserReportRepository userReportRepository;

    public UserServiceImpl(UserRepository userRepository, UserReportRepository userReportRepository) {
        this.userRepository = userRepository;
        this.userReportRepository = userReportRepository;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }
        User user = UserMapper.toEntity(request);
        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAll(int page, int size) {
        Page<User> pageData = userRepository.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
        List<UserResponse> items = pageData.getContent().stream()
            .map(UserMapper::toResponse)
            .collect(Collectors.toList());
        return new PageResponse<>(items, page, size, pageData.getTotalElements());
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        UserMapper.updateEntity(user, request);
        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<UserReportRow> getActiveUserReport() {
        return userReportRepository.findActiveUsers();
    }
}
