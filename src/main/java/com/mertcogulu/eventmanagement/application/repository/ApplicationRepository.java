package com.mertcogulu.eventmanagement.application.repository;

import com.mertcogulu.eventmanagement.application.entity.Application;
import com.mertcogulu.eventmanagement.application.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    List<Application> findByEventId(Long eventId);

    List<Application> findByEventIdAndStatus(
            Long eventId,
            ApplicationStatus status);

    List<Application> findByUserId(Long userId);

    long countByEventIdAndStatus(
            Long eventId,
            ApplicationStatus status);
}