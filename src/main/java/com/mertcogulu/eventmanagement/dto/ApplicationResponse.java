package com.mertcogulu.eventmanagement.dto;

import com.mertcogulu.eventmanagement.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;

    private Long userId;
    private String userFirstName;
    private String userLastName;

    private Long eventId;
    private String eventTitle;

    private ApplicationStatus status;
}
