package com.lifenautjoe.bol.controllers.users;

public class LoginResponseBody {
    private String loggedInUser;

    public LoginResponseBody(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }
}
