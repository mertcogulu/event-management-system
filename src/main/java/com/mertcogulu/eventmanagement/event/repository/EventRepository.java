package com.mertcogulu.eventmanagement.event.repository;

import com.mertcogulu.eventmanagement.event.entity.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByLocationIgnoreCase(String location);

    List<Event> findByTitleContainingIgnoreCase(String title);

    List<Event> findByStartDateAfter(LocalDateTime date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findWithLockById(Long id);
}
