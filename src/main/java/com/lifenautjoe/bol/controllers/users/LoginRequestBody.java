package com.lifenautjoe.bol.controllers.users;


public class LoginRequestBody {
    private String userName;

    public LoginRequestBody() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
