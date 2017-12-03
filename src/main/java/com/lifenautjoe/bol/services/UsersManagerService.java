package com.lifenautjoe.bol.services;

import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.exceptions.SessionHasNoUserException;
import com.lifenautjoe.bol.services.exceptions.UserAlreadyExistsException;
import com.lifenautjoe.bol.services.exceptions.UserDoesNotExistException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class UsersManagerService {
    private static final String SESSION_USERNAME_KEY = "username";
    private Map<String, User> users;

    public UsersManagerService() {
        this.users = Collections.synchronizedMap(new HashMap<String, User>());
    }

    public User getUserFromSession(HttpSession httpSession) {
        String sessionUserName = (String) httpSession.getAttribute(SESSION_USERNAME_KEY);
        if (sessionUserName == null) {
            throw new SessionHasNoUserException();
        }
        return getUserWithName(sessionUserName);
    }

    public User getUserWithName(String name) throws UserDoesNotExistException {
        User user = users.get(name);
        if (user == null) {
            throw new UserDoesNotExistException();
        }
        return user;
    }

    public User createUserForSession(String userName, HttpSession session) throws UserAlreadyExistsException {
        if (users.containsKey(userName)) {
            throw new UserAlreadyExistsException();
        }
        User user = new User(userName);
        users.put(userName, user);
        return user;
    }

}