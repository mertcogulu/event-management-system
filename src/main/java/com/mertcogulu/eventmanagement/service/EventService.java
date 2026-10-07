package com.mertcogulu.eventmanagement.service;

import com.mertcogulu.eventmanagement.dto.EventResponse;
import com.mertcogulu.eventmanagement.entity.Event;
import com.mertcogulu.eventmanagement.exception.InvalidEventDateException;
import com.mertcogulu.eventmanagement.repository.EventRepository;
import org.springframework.stereotype.Service;
import com.mertcogulu.eventmanagement.repository.UserRepository;
import com.mertcogulu.eventmanagement.dto.EventRequest;
import com.mertcogulu.eventmanagement.entity.User;
import com.mertcogulu.eventmanagement.exception.UserNotFoundException;

import java.time.LocalDateTime;
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

    public EventResponse createEvent(EventRequest request) {

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

        Event savedEvent = eventRepository.save(event);
        return mapToEventResponse(savedEvent);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToEventResponse)
                .toList();
    }

    public List<EventResponse> getEventsByLocation(String location) {
        return eventRepository.findByLocationIgnoreCase(location)
                .stream()
                .map(this::mapToEventResponse)
                .toList();
    }

    public List<EventResponse> getEventsByTitle(String title) {
        return eventRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::mapToEventResponse)
                .toList();
    }

    public List<EventResponse> getEventsAfterDate(LocalDateTime date) {
        return eventRepository.findByStartDateAfter(date)
                .stream()
                .map(this::mapToEventResponse)
                .toList();
    }

    public Optional<EventResponse> getEventById(Long id) {
        return eventRepository.findById(id)
                .map(this::mapToEventResponse);
    }

    public Optional<EventResponse> updateEvent(Long id, EventRequest request) {

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

                    Event savedEvent = eventRepository.save(existingEvent);
                    return mapToEventResponse(savedEvent);
                });
    }

    private void validateEventDates(Event event) {
        if (!event.getEndDate().isAfter(event.getStartDate())) {
            throw new InvalidEventDateException("End date must be after start date");
        }
    }

    private EventResponse mapToEventResponse(Event event) {

        User organizer = event.getOrganizer();

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartDate(),
                event.getEndDate(),
                event.getCapacity(),
                organizer != null ? organizer.getId() : null,
                organizer != null ? organizer.getFirstName() : null,
                organizer != null ? organizer.getLastName() : null
        );
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
