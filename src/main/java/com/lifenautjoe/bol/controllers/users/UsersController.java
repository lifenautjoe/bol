package com.lifenautjoe.bol.controllers.users;

import com.lifenautjoe.bol.controllers.ApiResponse;
import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.users.UsersManagerService;
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
    public ResponseEntity<Object> login(@RequestBody LoginRequestBody body, HttpSession httpSession) {

        String userName = body.getUserName();

        ResponseEntity response;

        if (usersManagerService.sessionHasUser(httpSession)) {
            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Already logged in"));
        } else if (usersManagerService.userNameAlreadyExists(userName)) {
            response = ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("User name already taken"));
        } else {
            User user = usersManagerService.createUserForSession(body.getUserName(), httpSession);
            response = ResponseEntity.ok().body(new LoginResponseBody(user.getName()));
        }

        return response;
    }

    @RequestMapping(path = "logout", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Object> logout(HttpSession httpSession) {

        ResponseEntity response;

        if (usersManagerService.sessionHasUser(httpSession)) {
            usersManagerService.removeUserForSession(httpSession);
            response = ResponseEntity.ok().body(new ApiResponse("User successfully logged out"));
        } else {
            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("User not logged in"));
        }

        return response;
    }
}
