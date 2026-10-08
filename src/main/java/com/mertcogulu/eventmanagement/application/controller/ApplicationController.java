package com.mertcogulu.eventmanagement.application.controller;

import com.mertcogulu.eventmanagement.application.dto.ApplicationRequest;
import com.mertcogulu.eventmanagement.application.dto.ApplicationResponse;
import com.mertcogulu.eventmanagement.application.dto.ApplicationStatusRequest;
import com.mertcogulu.eventmanagement.application.entity.ApplicationStatus;
import com.mertcogulu.eventmanagement.application.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ApplicationResponse createApplication(
            @Valid
            @RequestBody ApplicationRequest request) {

        return applicationService.createApplication(request);
    }

    @GetMapping
    public List<ApplicationResponse> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return applicationService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/event/{eventId}")
    public List<ApplicationResponse> getApplicationsByEventId(
            @PathVariable Long eventId) {

        return applicationService.getApplicationsByEventId(eventId);
    }

    @GetMapping("/event/{eventId}/status/{status}")
    public List<ApplicationResponse> getApplicationsByEventIdAndStatus(
            @PathVariable Long eventId,
            @PathVariable ApplicationStatus status) {

        return applicationService.getApplicationsByEventIdAndStatus(eventId, status);

    }

    @GetMapping("/user/{userId}")
    public List<ApplicationResponse> getApplicationsByUserId(
            @PathVariable Long userId) {

        return applicationService.getApplicationsByUserId(userId);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @Valid
            @RequestBody ApplicationStatusRequest request) {
        return applicationService.updateApplicationStatus(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}
