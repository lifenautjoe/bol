package com.lifenautjoe.bol.controllers.users;

import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.UsersManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(path = "users")
public class UsersController {

    private UsersManagerService usersManagerService;

    @Autowired
    public UsersController(UsersManagerService usersManagerService) {
        this.usersManagerService = usersManagerService;
    }

    @RequestMapping(path = "login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginRequestBody body, HttpSession httpSession) {

        String userName = body.getUserName();

        User user = null;
        if (usersManagerService.sessionHasUser(httpSession)) {
            user = usersManagerService.getUserFromSession(httpSession);
        } else {
            if (usersManagerService.userNameAlreadyExists(userName)) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("User name already taken");
            }
            user = usersManagerService.createUserForSession(body.getUserName(), httpSession);
        }

        return ResponseEntity.ok().body(user);
    }

    @RequestMapping(path = "logout", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void logout(HttpSession httpSession) {
        if (usersManagerService.sessionHasUser(httpSession)) {
            usersManagerService.removeUserForSession(httpSession);
        }
    }
}
