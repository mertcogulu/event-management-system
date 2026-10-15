package com.mertcogulu.eventmanagement.registration.controller;

import com.mertcogulu.eventmanagement.registration.dto.RegistrationResponse;
import com.mertcogulu.eventmanagement.registration.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/events/{eventId}")
    public ResponseEntity<RegistrationResponse> registerForEvent(
            @PathVariable Long eventId) {

        RegistrationResponse response = registrationService.registerForEvent(eventId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long registrationId) {

        registrationService.cancelRegistration(registrationId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<RegistrationResponse>> getMyRegistrations() {

        List<RegistrationResponse> registrations = registrationService.getMyRegistrations();

        return ResponseEntity.ok(registrations);
    }
}
