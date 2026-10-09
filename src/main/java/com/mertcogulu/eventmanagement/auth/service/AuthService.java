package com.mertcogulu.eventmanagement.auth.service;

import com.mertcogulu.eventmanagement.auth.dto.LoginRequest;
import com.mertcogulu.eventmanagement.exception.InvalidCredentialsException;
import com.mertcogulu.eventmanagement.user.entity.User;
import com.mertcogulu.eventmanagement.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            throw new InvalidCredentialsException("Invalid email or password");
        }
        return true;
    }
}
