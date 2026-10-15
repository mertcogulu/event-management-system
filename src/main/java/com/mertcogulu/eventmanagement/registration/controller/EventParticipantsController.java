package com.mertcogulu.eventmanagement.registration.controller;

import com.mertcogulu.eventmanagement.registration.dto.RegistrationResponse;
import com.mertcogulu.eventmanagement.registration.entity.Registration;
import com.mertcogulu.eventmanagement.registration.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventParticipantsController {

    private final RegistrationService registrationService;

    public EventParticipantsController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<RegistrationResponse>> getEventParticipants(@PathVariable Long eventId) {

        List<RegistrationResponse> participants =
                registrationService.getEventParticipants(eventId);

        return ResponseEntity.ok(participants);
    }
}
