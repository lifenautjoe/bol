package com.lifenautjoe.bol.domain.exceptions;

public class RequiredUsersNotSet extends Exception {
    public RequiredUsersNotSet() {

    }

    public RequiredUsersNotSet(String message) {
        super(message);
    }

    public RequiredUsersNotSet(Throwable cause) {
        super(cause);
    }

    public RequiredUsersNotSet(String message, Throwable cause) {
        super(message, cause);
    }
}
