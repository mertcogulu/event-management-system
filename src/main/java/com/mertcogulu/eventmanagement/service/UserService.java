package com.mertcogulu.eventmanagement.service;

import com.mertcogulu.eventmanagement.dto.UserResponse;
import com.mertcogulu.eventmanagement.entity.User;
import com.mertcogulu.eventmanagement.repository.UserRepository;
import com.mertcogulu.eventmanagement.exception.DuplicateEmailException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(
                    "Email already in use"
            );
        }
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

    public Optional<UserResponse> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {

                    if (userRepository.existsByEmailAndIdNot(updatedUser.getEmail(), id)) {
                        throw new DuplicateEmailException(
                                "Email already in use"
                        );
                    }
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setRole(updatedUser.getRole());

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
