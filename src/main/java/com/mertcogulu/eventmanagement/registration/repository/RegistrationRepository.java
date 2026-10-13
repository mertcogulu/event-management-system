package com.mertcogulu.eventmanagement.registration.repository;

import com.mertcogulu.eventmanagement.registration.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    long countByEventId(Long eventId);

    Optional<Registration> findByIdAndUserId(Long registrationId, Long userId);
}
