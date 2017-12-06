package com.lifenautjoe.bol.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class StompDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final Log logger = LogFactory.getLog(StompDisconnectListener.class);

    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();

        logger.debug("Disconnect event [sessionId: " + sessionId + "]");
    }
}