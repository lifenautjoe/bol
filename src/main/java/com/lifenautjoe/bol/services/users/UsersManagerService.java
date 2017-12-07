package com.lifenautjoe.bol.services.users;

import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.users.exceptions.SessionHasNoUserException;
import com.lifenautjoe.bol.services.users.exceptions.UserAlreadyExistsException;
import com.lifenautjoe.bol.services.users.exceptions.UserDoesNotExistException;
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
        String sessionUserName = getUserNameFromSession(httpSession);
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
        if (userNameAlreadyExists(userName)) {
            throw new UserAlreadyExistsException();
        }
        User user = new User(userName);
        users.put(userName, user);
        session.setAttribute(SESSION_USERNAME_KEY, userName);
        return user;
    }

    public void removeUserForSession(HttpSession session) {
        User user = getUserFromSession(session);
        users.remove(user.getName());
        session.removeAttribute(SESSION_USERNAME_KEY);
    }

    public boolean sessionHasUser(HttpSession session) {
        boolean hasUser = false;
        String sessionUserName = getUserNameFromSession(session);
        if (sessionUserName != null) {
            boolean userExists = users.containsKey(sessionUserName);
            if (userExists) {
                hasUser = true;
            } else {
                removeUsernameFromSession(session);
            }
        }
        return hasUser;
    }

    public boolean userNameAlreadyExists(String userName) {
        return users.containsKey(userName);
    }

    private String getUserNameFromSession(HttpSession httpSession) {
        return (String) httpSession.getAttribute(SESSION_USERNAME_KEY);
    }

    private void removeUsernameFromSession(HttpSession httpSession) {
        httpSession.removeAttribute(SESSION_USERNAME_KEY);
    }

    private void addUsernameToSession(HttpSession httpSession, String userName) {
        httpSession.setAttribute(SESSION_USERNAME_KEY, userName);
    }

}
