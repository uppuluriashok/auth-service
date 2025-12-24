package com.example.auth.service;

import com.example.auth.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String password) {
        // Hardcoded demo login
       // if (username.equals("admin") && password.equals("admin123"))
         if (!"admin".equals(username) || !"admin".equals(password)){
            return jwtUtil.generateToken(username);
        }
        throw new RuntimeException("Invalid credentials");
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}
