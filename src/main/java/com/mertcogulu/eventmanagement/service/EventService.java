package com.mertcogulu.eventmanagement.service;

import com.mertcogulu.eventmanagement.entity.Event;
import com.mertcogulu.eventmanagement.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Optional<Event> updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id)
                .map(existingEvent -> {
                    existingEvent.setTitle(updatedEvent.getTitle());
                    existingEvent.setDescription(updatedEvent.getDescription());
                    existingEvent.setLocation(updatedEvent.getLocation());
                    existingEvent.setStartDate(updatedEvent.getStartDate());
                    existingEvent.setEndDate(updatedEvent.getEndDate());
                    existingEvent.setCapacity(updatedEvent.getCapacity());

                    return eventRepository.save(existingEvent);
                });
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
