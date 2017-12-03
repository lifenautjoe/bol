package com.lifenautjoe.bol.controllers.users;

import com.lifenautjoe.bol.services.UsersManagerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "users")
public class UsersController {

    private UsersManagerService usersManagerService;

    @RequestMapping(path = "login", method = RequestMethod.POST)
    public void login(@RequestBody LoginRequestBody body) {

    }

    @RequestMapping(path = "logout", method = RequestMethod.POST)
    public void logout() {

    }
}
