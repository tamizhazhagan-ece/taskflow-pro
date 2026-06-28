package com.taskflow.service;

import com.taskflow.security.JwtService;
import com.taskflow.entity.User;
import org.springframework.stereotype.Component;

@Component
public class JwtServiceHelper {

    private final JwtService jwtService;

    public JwtServiceHelper(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user);
    }
}
