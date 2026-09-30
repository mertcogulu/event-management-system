package com.mertcogulu.eventmanagement.service;

import com.mertcogulu.eventmanagement.dto.ApplicationRequest;
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

    public Application createApplication(ApplicationRequest request) {

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

        return applicationRepository.save(application);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public Optional<Application> updateApplicationStatus(
            Long id,
            ApplicationStatusRequest request) {
        return applicationRepository.findById(id)
                .map(application -> {
                    application.setStatus(request.getStatus());
                    return applicationRepository.save(application);
                });
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}
