package com.mertcogulu.eventmanagement.registration.dto;

import java.time.LocalDateTime;

public record RegistrationResponse(
        Long id,
        Long eventId,
        Long userId,
        LocalDateTime registeredAt
) {
}