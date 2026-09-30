package com.mertcogulu.eventmanagement.dto;

import com.mertcogulu.eventmanagement.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationStatusRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;
}
