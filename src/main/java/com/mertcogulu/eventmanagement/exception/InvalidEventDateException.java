package com.mertcogulu.eventmanagement.exception;

public class InvalidEventDateException extends RuntimeException {

    public InvalidEventDateException(String message) {
        super(message);
    }
}