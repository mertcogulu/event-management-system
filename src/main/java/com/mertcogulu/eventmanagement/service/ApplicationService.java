package com.mertcogulu.eventmanagement.service;

import com.mertcogulu.eventmanagement.dto.ApplicationRequest;
import com.mertcogulu.eventmanagement.dto.ApplicationResponse;
import com.mertcogulu.eventmanagement.dto.ApplicationStatusRequest;
import com.mertcogulu.eventmanagement.entity.Application;
import com.mertcogulu.eventmanagement.entity.ApplicationStatus;
import com.mertcogulu.eventmanagement.entity.Event;
import com.mertcogulu.eventmanagement.entity.User;
import com.mertcogulu.eventmanagement.exception.DuplicateApplicationException;
import com.mertcogulu.eventmanagement.exception.UserNotFoundException;
import com.mertcogulu.eventmanagement.repository.ApplicationRepository;
import com.mertcogulu.eventmanagement.repository.EventRepository;
import com.mertcogulu.eventmanagement.repository.UserRepository;
import com.mertcogulu.eventmanagement.exception.EventNotFoundException;
import com.mertcogulu.eventmanagement.exception.EventCapacityExceededException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public ApplicationResponse createApplication(ApplicationRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id " + request.getUserId()
                ));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with id " + request.getEventId()
                ));

        if (applicationRepository.existsByUserIdAndEventId(
                request.getUserId(),
                request.getEventId())) {
            throw new DuplicateApplicationException(
                    "User has already applied to this event"
            );
        }

        Application application = new Application();

        application.setUser(user);
        application.setEvent(event);
        application.setStatus(ApplicationStatus.PENDING);

        Application savedApplication = applicationRepository.save(application);
        return mapToApplicationResponse(savedApplication);
    }

    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsByEventId(Long eventId) {
        return applicationRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsByEventIdAndStatus(Long eventId, ApplicationStatus status) {
        return applicationRepository.findByEventIdAndStatus(eventId, status)
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();
    }

    public Optional<ApplicationResponse> getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .map(this::mapToApplicationResponse);
    }

    public Optional<ApplicationResponse> updateApplicationStatus(
            Long id,
            ApplicationStatusRequest request) {
        return applicationRepository.findById(id)
                .map(application -> {

                    if (request.getStatus() == ApplicationStatus.ACCEPTED
                            && application.getStatus() != ApplicationStatus.ACCEPTED) {
                        Long eventId = application.getEvent().getId();
                        long acceptedCount =
                                applicationRepository.countByEventIdAndStatus(
                                        eventId,
                                        ApplicationStatus.ACCEPTED);

                        if (acceptedCount >= application.getEvent().getCapacity()) {
                            throw new EventCapacityExceededException("Event capacity has been reached");
                        }
                    }

                    application.setStatus(request.getStatus());

                    Application savedApplication = applicationRepository.save(application);
                    return mapToApplicationResponse(savedApplication);

                });
    }

    private ApplicationResponse mapToApplicationResponse(Application application) {

        User user = application.getUser();
        Event event = application.getEvent();

        return new ApplicationResponse(
                application.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                event.getId(),
                event.getTitle(),
                application.getStatus()
        );
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}
