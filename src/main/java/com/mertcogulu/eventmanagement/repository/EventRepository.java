package com.mertcogulu.eventmanagement.repository;

import com.mertcogulu.eventmanagement.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
