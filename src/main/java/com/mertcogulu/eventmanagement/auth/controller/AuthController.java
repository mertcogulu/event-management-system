package com.mertcogulu.eventmanagement.auth.controller;

import com.mertcogulu.eventmanagement.auth.dto.LoginRequest;
import com.mertcogulu.eventmanagement.auth.dto.LoginResponse;
import com.mertcogulu.eventmanagement.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
