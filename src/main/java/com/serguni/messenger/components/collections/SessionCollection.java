package com.serguni.messenger.components.collections;

import com.serguni.messenger.components.sockets.ListenerService;
import com.serguni.messenger.dbms.models.Session;
import java.util.*;


public class SessionCollection {
    private final Map<Long, Map<Long, ListenerService>> usersSessions;
    public SessionCollection() {
        usersSessions = new HashMap<>();
    }

    public void addSession(Session session, ListenerService listenerService) {

        Map<Long, ListenerService> sessionsByUserId = usersSessions.get(session.getUser().getId());


        if (sessionsByUserId == null) {

            sessionsByUserId = new HashMap<>();
            sessionsByUserId.put(session.getId(), listenerService);

            usersSessions.put(session.getUser().getId(), sessionsByUserId);
        } else {
            sessionsByUserId.put(session.getId(), listenerService);
        }

    }

    public void removeSessionWithUser(Session session) {
        Map<Long, ListenerService> sessionsByUserId = usersSessions.get(session.getUser().getId());
        sessionsByUserId.remove(session.getId()).closeSocket();
        if (sessionsByUserId.isEmpty())
            usersSessions.remove(session.getUser().getId());
    }

    public Map<Long, ListenerService> getSessionsByUserId(long userId) {
        return usersSessions.get(userId);
    }

    public ListenerService getSessionByUserIdAndId(long userId, long id) {
        return usersSessions.get(userId).get(id);
    }

    public boolean isOnlineByUserId(long userId) {
        return usersSessions.containsKey(userId);
    }

    public boolean containsSession(Session session) {
        Map<Long, ListenerService> sessionByUserId = usersSessions.get(session.getUser().getId());

        if (sessionByUserId == null)
            return false;

        return sessionByUserId.containsKey(session.getId());
    }

    public int size() {
        return usersSessions.size();
    }

    @Override
    public String toString() {
        return "SessionCollection{" +
                "usersSessions=" + usersSessions.toString() +
                '}';
    }
}
