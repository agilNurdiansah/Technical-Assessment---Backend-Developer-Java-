package com.company.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.company.userservice.application.dto.request.CreateUserRequest;
import com.company.userservice.application.exception.NotFoundException;
import com.company.userservice.application.port.in.UserService;
import com.company.userservice.application.port.out.UserReportRepository;
import com.company.userservice.application.port.out.UserRepository;
import com.company.userservice.application.service.UserServiceImpl;
import com.company.userservice.domain.User;
import com.company.userservice.domain.UserStatus;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReportRepository userReportRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userReportRepository);
    }

    @Test
    void createUser_returnsResponse() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setStatus(UserStatus.ACTIVE);

        User saved = new User();
        saved.setId(1L);
        saved.setName("Alice");
        saved.setEmail("alice@example.com");
        saved.setStatus(UserStatus.ACTIVE);
        saved.setCreatedAt(Instant.now());
        saved.setUpdatedAt(Instant.now());

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(saved);

        var response = userService.create(request);

        assertEquals(1L, response.getId());
        assertEquals("Alice", response.getName());
        assertEquals("alice@example.com", response.getEmail());
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getById(99L));
    }
}
