package com.lifenautjoe.bol.controllers.users.requests;


import javax.validation.constraints.Size;

public class LoginRequestBody {

    @Size(min = 2, max = 32)
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
