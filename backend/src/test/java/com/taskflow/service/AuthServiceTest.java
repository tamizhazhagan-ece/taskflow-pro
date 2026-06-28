package com.taskflow.service;

import com.taskflow.dto.request.RegisterRequest;
import com.taskflow.entity.Role;
import com.taskflow.entity.User;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerCreatesMemberWithEncodedPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@taskflow.com");
        request.setPassword("password123");

        var response = authService.register(request);

        assertNotNull(response.getToken());
        assertEquals("Test User", response.getUser().getName());
        assertEquals(Role.DEVELOPER, response.getUser().getRole());

        User saved = userRepository.findByEmail("test@taskflow.com").orElseThrow();
        assertTrue(passwordEncoder.matches("password123", saved.getPassword()));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        User existing = new User();
        existing.setName("Existing");
        existing.setEmail("dup@taskflow.com");
        existing.setPassword(passwordEncoder.encode("password123"));
        existing.setRole(Role.DEVELOPER);
        userRepository.save(existing);

        RegisterRequest request = new RegisterRequest();
        request.setName("Another");
        request.setEmail("dup@taskflow.com");
        request.setPassword("password123");

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }
}
