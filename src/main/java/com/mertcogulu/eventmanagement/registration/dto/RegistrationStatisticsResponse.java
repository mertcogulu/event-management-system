package com.mertcogulu.eventmanagement.registration.dto;

public record RegistrationStatisticsResponse(
        Long eventId,
        Integer capacity,
        Long registeredCount,
        Long availableSpots,
        Double occupancyRate
) {
}
