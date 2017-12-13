package com.lifenautjoe.bol.services.sessions.exceptions;

public class SessionDoesNotExistException extends RuntimeException {

    public SessionDoesNotExistException() {
    }

    public SessionDoesNotExistException(String message) {
        super(message);
    }

    public SessionDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public SessionDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
