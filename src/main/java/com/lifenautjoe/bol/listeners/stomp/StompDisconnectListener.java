package com.lifenautjoe.bol.listeners.stomp;

import com.lifenautjoe.bol.domain.GamePlayOutcome;
import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.sessions.SessionsLookupService;
import com.lifenautjoe.bol.services.users.UsersManagerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.servlet.http.HttpSession;

@Component
public class StompDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final Log logger = LogFactory.getLog(StompDisconnectListener.class);
    private SessionsLookupService sessionsLookupService;
    private UsersManagerService usersManagerService;
    private SimpMessagingTemplate template;


    @Autowired
    public StompDisconnectListener(SessionsLookupService sessionsLookupService,
                                   UsersManagerService usersManagerService,
                                   SimpMessagingTemplate template) {
        this.sessionsLookupService = sessionsLookupService;
        this.usersManagerService = usersManagerService;
        this.template = template;
    }

    public void onApplicationEvent(SessionDisconnectEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        String sessionId = SimpMessageHeaderAccessor.getSessionAttributes(headers).get("HTTPSESSIONID").toString();

        if (sessionsLookupService.hasSessionWithId(sessionId)) {
            HttpSession session = sessionsLookupService.getSessionWithId(sessionId);
            if (usersManagerService.sessionHasUser(session)) {
                User sessionUser = usersManagerService.getUserFromSession(session);

                if (sessionUser.hasGame()) {
                    String userGameName = sessionUser.getGameName();
                    GamePlayOutcome gamePlayOutcome = sessionUser.terminateGame();
                    this.template.convertAndSend("/games/" + userGameName, gamePlayOutcome);
                }
            }
        }

        logger.debug("Disconnect event [sessionId: " + sessionId + "]");
    }
}