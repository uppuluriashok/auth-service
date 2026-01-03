//package com.example.auth.controller;
//
//import com.example.auth.dto.LoginRequest;
//import com.example.auth.service.AuthService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @Autowired
//    private AuthService authService;

package com.example.auth.controller;
import com.example.auth.service.AuthService;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")

@CrossOrigin(
        origins = {
                "http://localhost",
                "http://localhost:8100",
                "https://localhost",
                "capacitor://localhost"
        },
        allowCredentials = "true"
)

public class AuthController {
    @Autowired
   private AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Username already exists")
            );
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        userRepository.save(user);

        // OPTIONAL: Auto-login after register
        String token =jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "token", token
        ));
    }

    // (your existing login method stays here)



    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        String token = authService.login(request.getUsername(), request.getPassword());
        String username = jwtUtil.extractUsername(token);
        return Map.of("token", token,"username", username);
    }
//
//    @PostMapping("/validate")
//    public Map<String, Boolean> validate(@RequestHeader("Authorization") String authHeader) {
//
//        String token = authHeader.replace("Bearer ", "");
//
//        boolean valid = authService.validateToken(token);
//
//        return Map.of("valid", valid);
//    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        String username = jwtUtil.extractUsername(token);

        if (jwtUtil.isTokenValid(token, username)) {
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", username
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false));
    }


    @PostMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String auth) {

        String token = auth.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(Map.of(
                "username", username
        ));
    }

}
