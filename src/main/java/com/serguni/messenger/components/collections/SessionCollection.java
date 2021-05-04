package com.serguni.messenger.components.collections;

import com.serguni.messenger.components.sockets.ListenerService;
import com.serguni.messenger.dbms.models.Session;
import java.util.*;


public class SessionCollection {
    private final Map<Long, Map<Long, ListenerService>> usersSessions;
//    private final Map<User, Map<Session, ListenerService>> userSessionsMap;
//    private Map<Long, UserSession> userSession;
//
//    private Map<Long, >

//    //ОТСЛЕЖИВАЕМЫЕ ПОЛЬЗОВАТЕЛИ И МНОЖЕСТВО СЕССИЙ КОТОРЫЕ ОТСЛЕЖИВАЮТ ЕГО
//    private final Map<Long, Set<Long>> trackedUsers;
//
//    //ОТСЛЕЖИВАЮЩИЕ СЕССИИ И ПОЛЬЗОВАТЕЛИ КОТОРЫЕ ОТСЛЕЖИВАЮТСЯ ЭТИМИ СЕССИЯМИ
//    private final Map<Long, Set<Long>> trackingSessions;

    public SessionCollection() {
        usersSessions = new HashMap<>();
//        trackedUsers = new HashMap<>();
//        trackingSessions = new HashMap<>();
    }

    //добавить поиск видимых чатов добавленного пользователя. После по id настоящего чата ищем другие видмые чаты и рассылаем всем пользователям этого юзера, что он онлайн
    public void addSession(Session session, ListenerService listenerService) {

        Map<Long, ListenerService> sessionsByUserId = usersSessions.get(session.getUser().getId());


        if (sessionsByUserId == null) {

            sessionsByUserId = new HashMap<>();
            sessionsByUserId.put(session.getId(), listenerService);

            usersSessions.put(session.getUser().getId(), sessionsByUserId);

            System.out.println("ПОСЛЕ ДОБАВЛЕНИЯ СЕССИИ");
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

    @Override
    public String toString() {
        return "SessionCollection{" +
                "usersSessions=" + usersSessions.toString() +
                '}';
    }

    //    public void addTrackingSessionByTrackedUserId(long trackingSessionId, long trackedUserId) {
//        trackedUsers.computeIfAbsent(trackedUserId, k -> new HashSet<>())
//                .add(trackingSessionId);
//    }
//
//    public void removeTrackingSessionByTrackedUserId(long trackingSessionId, long trackedUserId) {
//        Set<Long> trackingSessionsByTrackedUserId = trackedUsers.get(trackedUserId);
//
//        trackingSessionsByTrackedUserId.remove(trackingSessionId);
//        if (trackingSessionsByTrackedUserId.isEmpty())
//            trackingSessions.remove(trackedUserId);
//    }
//
//    public Set<Long> getTrackingSessionsByTrackedUserId(long trackedUserId) {
//        return trackedUsers.get(trackedUserId);
//    }
//
//    public void addTrackedUserByTrackingSessionId(long trackedUserId, long trackingSessionId) {
//        trackingSessions.computeIfAbsent(trackingSessionId, k -> new HashSet<>())
//                .add(trackedUserId);
//    }
//
//    public void removeTrackedUserByTrackingSessionId(long trackedUserId, long trackingSessionId) {
//        Set<Long> trackedUserByTrackingSession = trackingSessions.get(trackingSessionId);
//
//        trackedUserByTrackingSession.remove(trackedUserId);
//        if (trackedUserByTrackingSession.isEmpty())
//
//
//        trackingUsersByTrackedUserId.remove(trackingUserId);
//        if (trackingUsersByTrackedUserId.isEmpty())
//            trackedUsers.remove(trackedUserId);
//    }
//
//    public Set<Long> getTrackingUsersByTrackedUserId(long trackedUserId) {
//        return trackedUsers.get(trackedUserId);
//    }
//
//    public void clearTrackedUsers(long trackingUserId) {
//        for (long trackedUserId : trackingUsers.get(trackingUserId)) {
//            trackedUsers.get(trackedUserId).remove(trackingUserId);
//        }
//    }
}
