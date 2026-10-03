package com.mertcogulu.eventmanagement.repository;

import com.mertcogulu.eventmanagement.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    List<Application> findByEventId(Long eventId);
    
    List<Application> findByUserId(Long userId);
}