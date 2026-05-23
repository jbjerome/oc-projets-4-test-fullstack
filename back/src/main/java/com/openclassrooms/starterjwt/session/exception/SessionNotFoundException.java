package com.openclassrooms.starterjwt.session.exception;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(String identifier) {
        super("Session not found: " + identifier);
    }
}
