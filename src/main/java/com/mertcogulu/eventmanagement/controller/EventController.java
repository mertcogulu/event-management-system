package com.mertcogulu.eventmanagement.controller;

import com.mertcogulu.eventmanagement.entity.Event;
import com.mertcogulu.eventmanagement.service.EventService;
import com.mertcogulu.eventmanagement.dto.EventRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public Event createEvent(
            @Valid
            @RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{location}")
    public List<Event> getEventsByLocation(@PathVariable String location) {
        return eventService.getEventsByLocation(location);
    }

    @GetMapping("/title/{title}")
    public List<Event> getEventsByTitle(@PathVariable String title) {
        return eventService.getEventsByTitle(title);
    }
    
    @GetMapping("/after/{date}")
    public List<Event> getEventsAfterDate(@PathVariable LocalDateTime date) {
        return eventService.getEventsAfterDate(date);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @Valid
            @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
