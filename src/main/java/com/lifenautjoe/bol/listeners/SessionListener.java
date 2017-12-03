package com.lifenautjoe.bol.listeners;

import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.UsersManagerService;
import com.lifenautjoe.bol.services.exceptions.SessionHasNoUserException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class SessionListener implements HttpSessionListener, ApplicationContextAware {

    private SimpMessagingTemplate template;
    private UsersManagerService usersManagerService;

    @Autowired
    public SessionListener(SimpMessagingTemplate template, UsersManagerService usersManagerService) {
        this.template = template;
        this.usersManagerService = usersManagerService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("session created");
        event.getSession().setMaxInactiveInterval(15);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        try {
            User sessionUser = usersManagerService.getUserFromSession(session);
        } catch (SessionHasNoUserException exception) {
        }

        System.out.println("session destroyed");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof WebApplicationContext) {
            ((WebApplicationContext) applicationContext).getServletContext().addListener(this);
        } else {
            throw new RuntimeException("Must be inside a web application context");
        }
    }
}