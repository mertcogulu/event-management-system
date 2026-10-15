package com.mertcogulu.eventmanagement.registration.service;

import com.mertcogulu.eventmanagement.event.entity.Event;
import com.mertcogulu.eventmanagement.event.repository.EventRepository;
import com.mertcogulu.eventmanagement.exception.UserNotFoundException;
import com.mertcogulu.eventmanagement.registration.dto.RegistrationResponse;
import com.mertcogulu.eventmanagement.registration.entity.Registration;
import com.mertcogulu.eventmanagement.registration.repository.RegistrationRepository;
import com.mertcogulu.eventmanagement.user.entity.User;
import com.mertcogulu.eventmanagement.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               UserRepository userRepository) {

        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RegistrationResponse registerForEvent(Long eventId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "Authenticated user not found"
                ));

        Event event = eventRepository.findWithLockById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found with id " + eventId
                ));

        boolean alreadyRegistered = registrationRepository.existsByUserIdAndEventId(currentUser.getId(), event.getId());

        if (alreadyRegistered) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You are already registered for this event"
            );
        }

        long registeredCount = registrationRepository.countByEventId(event.getId());

        if (registeredCount >= event.getCapacity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Event capacity has been reached"
            );
        }

        Registration registration = new Registration();

        registration.setUser(currentUser);
        registration.setEvent(event);
        registration.setRegisteredAt(LocalDateTime.now());

        Registration savedRegistration = registrationRepository.save(registration);

        return new RegistrationResponse(
                savedRegistration.getId(),
                savedRegistration.getEvent().getId(),
                savedRegistration.getUser().getId(),
                savedRegistration.getRegisteredAt()
        );
    }

    @Transactional
    public void cancelRegistration(Long registrationId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

        Registration registration = registrationRepository.findByIdAndUserId(registrationId, currentUser.getId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Registration not found"
        ));

        eventRepository.findWithLockById(registration.getEvent().getId())
                        .orElseThrow(() -> new ResponseStatusException(
                               HttpStatus.NOT_FOUND,
                               "Event not found"
                        ));

        registrationRepository.delete(registration);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getMyRegistrations() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

        List<Registration> registrations = registrationRepository.findByUserId(currentUser.getId());

        return registrations.stream()
                .map(registration -> new RegistrationResponse(
                        registration.getId(),
                        registration.getEvent().getId(),
                        registration.getUser().getId(),
                        registration.getRegisteredAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getEventParticipants(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found with id " + eventId));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "Authenticated user not found"));

        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isOrganizer = currentUser.getRole().name().equals("ORGANIZER");
        boolean isEventOwner = event.getOrganizer() != null
                && event.getOrganizer().getId().equals(currentUser.getId());

        if (!isAdmin && !(isOrganizer && isEventOwner)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to view participants if this event"
            );
        }

        List<Registration> registrations =
                registrationRepository.findByEventId(eventId);

        return registrations.stream()
                .map(registration -> new RegistrationResponse(
                        registration.getId(),
                        registration.getEvent().getId(),
                        registration.getUser().getId(),
                        registration.getRegisteredAt()
                ))
                .toList();
    }
}
