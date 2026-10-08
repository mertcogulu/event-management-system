package com.mertcogulu.eventmanagement.user.service;

import com.mertcogulu.eventmanagement.user.dto.UserCreateRequest;
import com.mertcogulu.eventmanagement.user.dto.UserResponse;
import com.mertcogulu.eventmanagement.user.dto.UserUpdateRequest;
import com.mertcogulu.eventmanagement.user.entity.User;
import com.mertcogulu.eventmanagement.user.repository.UserRepository;
import com.mertcogulu.eventmanagement.exception.DuplicateEmailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(
                    "Email already in use"
            );
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }

    public Optional<UserResponse> updateUser(Long id, UserUpdateRequest request) {
        return userRepository.findById(id)
                .map(existingUser -> {

                    if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                        throw new DuplicateEmailException(
                                "Email already in use"
                        );
                    }
                    existingUser.setFirstName(request.getFirstName());
                    existingUser.setLastName(request.getLastName());
                    existingUser.setEmail(request.getEmail());
                    existingUser.setRole(request.getRole());

                    User savedUser = userRepository.save(existingUser);
                    return mapToUserResponse(savedUser);
                });
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
