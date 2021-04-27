package com.serguni.messenger.components.sockets;

import com.serguni.messenger.dbms.models.Session;
import com.serguni.messenger.dbms.models.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientSession {
    private final Map<Long, ListenerService> clientSession;
    private final Set<User> userTracking;



    public ClientSession() {
        clientSession = new HashMap<>();
        userTracking = new HashSet<>();
    }

    public void addUserTracking(User user) {
        userTracking.add(user);
    }

    public void addSession(long sessionId, ListenerService listenerService) {
        clientSession.put(sessionId, listenerService);
    }

    public Map<Long, ListenerService> getClientSession() {
        return clientSession;
    }

    public Set<User> getUserTracking() {
        return userTracking;
    }
}
