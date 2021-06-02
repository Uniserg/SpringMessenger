package com.serguni.messenger.components.collections;

import com.serguni.messenger.components.sockets.ListenerService;
import com.serguni.messenger.dbms.models.Session;
import com.serguni.messenger.dbms.models.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class SessionCollectionTest {

    private static final SessionCollection sessionCollection = new SessionCollection();

    @Test
    void addSession() throws IOException {
        User user = new User();
        user.setId(1);

        Session session = new Session();
        session.setUser(user);
        session.setId(1);
        sessionCollection.addSession(
                session,
                new ListenerService(session, new Socket(), null, null));

        Session session1 = new Session();
        session1.setUser(user);
        session1.setId(2);
        sessionCollection.addSession(
                session1,
                new ListenerService(session, new Socket(), null, null));

        assertEquals(1, sessionCollection.size());

    }

    @Test
    void removeSessionWithUser() {
        User user = new User();
        user.setId(1);

        Session session = new Session();
        session.setId(1);
        session.setUser(user);
        sessionCollection.removeSessionWithUser(session);
        assertEquals(1, sessionCollection.size());

        Session session1 = new Session();
        session1.setId(2);
        session1.setUser(user);
        sessionCollection.removeSessionWithUser(session1);
        assertEquals(0, sessionCollection.size());
    }

    @Test
    void getSessionsByUserId() throws IOException {
        addSession();
        assertEquals(2, sessionCollection.getSessionsByUserId(1).size());
    }

    @Test
    void isOnlineByUserId() {
        assertTrue(sessionCollection.isOnlineByUserId(1));
    }
}