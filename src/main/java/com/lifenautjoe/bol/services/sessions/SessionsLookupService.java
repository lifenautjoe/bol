package com.lifenautjoe.bol.services.sessions;

import com.lifenautjoe.bol.services.sessions.exceptions.SessionDoesNotExistException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

// Yes, this is a bad idea in the real world. This is an experiment.

@Service
public class SessionsLookupService {
    private Map<String, HttpSession> sessions;

    public SessionsLookupService() {
        this.sessions = new HashMap<>();
    }

    public void addSession(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    public HttpSession getSessionWithId(String sessionId) {
        if (!hasSessionWithId(sessionId)) {
            throw new SessionDoesNotExistException();
        }
        return sessions.get(sessionId);
    }

    public boolean hasSessionWithId(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public void removeSession(HttpSession session) {
        String sessionId = session.getId();
        removeSessionById(sessionId);
    }

    private void removeSessionById(String sessionId) {
        sessions.remove(sessionId);
    }

}
