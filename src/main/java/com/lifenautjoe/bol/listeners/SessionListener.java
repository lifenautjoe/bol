package com.lifenautjoe.bol.listeners;

import com.lifenautjoe.bol.domain.GamePlayOutcome;
import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.sessions.SessionsLookupService;
import com.lifenautjoe.bol.services.users.UsersManagerService;
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
    private SessionsLookupService sessionsLookupService;

    @Autowired
    public SessionListener(SimpMessagingTemplate template,
                           SessionsLookupService sessionsLookupService,
                           UsersManagerService usersManagerService) {
        this.template = template;
        this.usersManagerService = usersManagerService;
        this.sessionsLookupService = sessionsLookupService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("Session created!");
        HttpSession session = event.getSession();
        sessionsLookupService.addSession(session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        if (usersManagerService.sessionHasUser(session)) {
            User sessionUser = usersManagerService.getUserFromSession(session);

            if (sessionUser.hasGame()) {
                String userGameName = sessionUser.getGameName();
                GamePlayOutcome gamePlayOutcome = sessionUser.terminateGame();
                this.template.convertAndSend("/games/" + userGameName, gamePlayOutcome);
            }
        }

        sessionsLookupService.removeSession(session);

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