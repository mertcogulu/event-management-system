package com.mertcogulu.eventmanagement.event.repository;

import com.mertcogulu.eventmanagement.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByLocationIgnoreCase(String location);

    List<Event> findByTitleContainingIgnoreCase(String title);

    List<Event> findByStartDateAfter(LocalDateTime date);
}
