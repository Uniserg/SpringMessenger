package com.serguni.messenger.components.sockets;

import com.serguni.messenger.SpringMessengerApplication;
import com.serguni.messenger.dbms.models.Session;
import com.serguni.messenger.dto.SessionCookie;
import com.serguni.messenger.dto.SocketMessage;
import com.serguni.messenger.dto.TransferToDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.serguni.messenger.dto.SocketMessage.MessageType.*;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    public static String SERVER_SOCKET_ADDRESS = "localhost";
    public static int SERVER_SOCKET_PORT = 8081;
    public static Socket socket;
    public static Session session;
    public static ObjectOutputStream out;

    @BeforeAll
    static void login() {
        try {
            SpringMessengerApplication.main(new String[0]);
            Session session = Server.sessionRepository.findById(550L).orElse(null);
            assertNotNull(session);

            Socket socket = new Socket(SERVER_SOCKET_ADDRESS, SERVER_SOCKET_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            SessionCookie sessionCookie = new SessionCookie(session.getId(), session.getCookie());
            out.writeObject(sessionCookie);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            SocketMessage socketMessage = (SocketMessage) in.readObject();
            assertEquals(LOGIN, socketMessage.getType());
            assertEquals(TransferToDto.getUserDto(session.getUser()), socketMessage.getBody());

            ServerTest.session = session;
            ServerTest.socket = socket;
            ServerTest.out = out;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteAllOtherSessions() throws IOException, InterruptedException {
        SocketMessage socketMessage = new SocketMessage(DELETE_ALL_OTHER_SESSIONS, null);
        out.writeObject(socketMessage);
        System.out.println("Пользователь у которого удаляется сессия " + session.getUser().getId());
        //ЖДЕМ завершения транзакции
        Thread.sleep(200);
        assertEquals(1, Server.sessionRepository.findAllByUserId(session.getUser().getId()).size());
    }


    @Test
    void deleteOtherSession() throws IOException {
        out.writeObject(new SocketMessage(DELETE_OTHER_SESSION, 560L));
        assertNotNull(Server.sessionRepository.findById(560L).orElse(null));

        out.writeObject(new SocketMessage(DELETE_OTHER_SESSION, 564L));
        assertNull(Server.sessionRepository.findById(564L).orElse(null));

    }
}