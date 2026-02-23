package com.company.userservice.infrastructure.bootstrap;

import com.company.userservice.domain.User;
import com.company.userservice.domain.UserStatus;
import com.company.userservice.infrastructure.persistence.jpa.UserJpaRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final UserJpaRepository userRepository;

    public DatabaseSeeder(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User alice = new User();
        alice.setName("Alice Johnson");
        alice.setEmail("alice@example.com");
        alice.setStatus(UserStatus.ACTIVE);

        User bob = new User();
        bob.setName("Bob Smith");
        bob.setEmail("bob@example.com");
        bob.setStatus(UserStatus.INACTIVE);

        userRepository.saveAll(List.of(alice, bob));
    }
}
