package com.mertcogulu.eventmanagement.event.service;

import com.mertcogulu.eventmanagement.event.dto.EventResponse;
import com.mertcogulu.eventmanagement.event.entity.Event;
import com.mertcogulu.eventmanagement.exception.InvalidEventDateException;
import com.mertcogulu.eventmanagement.event.repository.EventRepository;
import com.mertcogulu.eventmanagement.registration.repository.RegistrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.mertcogulu.eventmanagement.user.repository.UserRepository;
import com.mertcogulu.eventmanagement.event.dto.EventRequest;
import com.mertcogulu.eventmanagement.user.entity.User;
import com.mertcogulu.eventmanagement.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(
            EventRepository eventRepository,
            UserRepository userRepository,
            RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    public EventResponse createEvent(EventRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        String currentUserEmail = authentication.getName();

        if (!isAdmin) {
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

            if (!currentUser.getId().equals(request.getOrganizerId())) {
                throw new org.springframework.security.access.AccessDeniedException("You can only create events for yourself");
            }
        }

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
    @Transactional
    public Optional<EventResponse> updateEvent(Long id, EventRequest request) {

        return eventRepository.findWithLockById(id)
                .map(existingEvent -> {

                    Authentication authentication =
                            SecurityContextHolder.getContext().getAuthentication();

                    boolean isAdmin = authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority.getAuthority().equals("ROLE_ADMIN"));

                    String currentUserEmail = authentication.getName();

                    if (!isAdmin &&
                            !existingEvent.getOrganizer().getEmail().equals(currentUserEmail)) {

                        throw new org.springframework.security.access.AccessDeniedException(
                                "You can only update your own events"
                        );
                    }

                    User organizer = userRepository.findById(request.getOrganizerId())
                            .orElseThrow(() -> new UserNotFoundException(
                                    "User not found with id " + request.getOrganizerId()
                            ));

                    if (!isAdmin &&
                            !existingEvent.getOrganizer().getId().equals(organizer.getId())) {

                        throw new org.springframework.security.access.AccessDeniedException(
                                "Only ADMIN can change the event organizer"
                        );
                    }

                    long registeredCount = registrationRepository.countByEventId(id);

                    if (request.getCapacity() < registeredCount) {
                        throw new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.CONFLICT, "Event capacity cannot be less than the number of registered participants"
                        );
                    }

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

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found with id " + id
                ));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        String currentUserEmail = authentication.getName();

        if (!isAdmin &&
                !event.getOrganizer().getEmail().equals(currentUserEmail)) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only delete your own events");
        }
        eventRepository.delete(event);
    }
}
