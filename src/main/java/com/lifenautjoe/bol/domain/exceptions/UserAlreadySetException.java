package com.lifenautjoe.bol.domain.exceptions;

public class UserAlreadySetException extends Exception {
    public UserAlreadySetException() {

    }

    public UserAlreadySetException(String message) {
        super(message);
    }

    public UserAlreadySetException(Throwable cause) {
        super(cause);
    }

    public UserAlreadySetException(String message, Throwable cause) {
        super(message, cause);
    }
}
