package com.mertcogulu.eventmanagement.auth.service;

import com.mertcogulu.eventmanagement.auth.dto.LoginRequest;
import com.mertcogulu.eventmanagement.auth.jwt.JwtService;
import com.mertcogulu.eventmanagement.exception.InvalidCredentialsException;
import com.mertcogulu.eventmanagement.user.entity.User;
import com.mertcogulu.eventmanagement.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            throw new InvalidCredentialsException("Invalid email or password");
        }
        return jwtService.generateToken(user.getEmail());
    }
}
