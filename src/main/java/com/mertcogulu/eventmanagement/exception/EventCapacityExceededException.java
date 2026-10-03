package com.mertcogulu.eventmanagement.exception;

public class EventCapacityExceededException extends RuntimeException {

    public EventCapacityExceededException(String message) {
        super(message);
    }
}
