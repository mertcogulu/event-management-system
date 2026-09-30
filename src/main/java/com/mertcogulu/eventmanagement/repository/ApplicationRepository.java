package com.mertcogulu.eventmanagement.repository;

import com.mertcogulu.eventmanagement.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}
