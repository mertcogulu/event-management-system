package com.mertcogulu.eventmanagement.service;

import com.mertcogulu.eventmanagement.entity.Event;
import com.mertcogulu.eventmanagement.exception.InvalidEventDateException;
import com.mertcogulu.eventmanagement.repository.EventRepository;
import org.springframework.stereotype.Service;
import com.mertcogulu.eventmanagement.repository.UserRepository;
import com.mertcogulu.eventmanagement.dto.EventRequest;
import com.mertcogulu.eventmanagement.entity.User;
import com.mertcogulu.eventmanagement.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(
            EventRepository eventRepository,
            UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Event createEvent(EventRequest request) {

        User organizer = userRepository.findById(request.getOrganizerId())
                        .orElseThrow(() -> new UserNotFoundException(
                                "User not found with id " + request.getOrganizerId()
                        ));

        Event event = new Event();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setCapacity(request.getCapacity());
        event.setOrganizer(organizer);

        validateEventDates(event);
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Optional<Event> updateEvent(Long id, EventRequest request) {

        User organizer = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id " + request.getOrganizerId()
                ));

        return eventRepository.findById(id)
                .map(existingEvent -> {
                    existingEvent.setTitle(request.getTitle());
                    existingEvent.setDescription(request.getDescription());
                    existingEvent.setLocation(request.getLocation());
                    existingEvent.setStartDate(request.getStartDate());
                    existingEvent.setEndDate(request.getEndDate());
                    existingEvent.setCapacity(request.getCapacity());
                    existingEvent.setOrganizer(organizer);

                    validateEventDates(existingEvent);

                    return eventRepository.save(existingEvent);
                });
    }

    private void validateEventDates(Event event) {
        if (!event.getEndDate().isAfter(event.getStartDate())) {
            throw new InvalidEventDateException("End date must be after start date");
        }
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
