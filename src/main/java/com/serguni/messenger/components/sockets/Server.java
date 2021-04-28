package com.serguni.messenger.components.sockets;

import com.serguni.messenger.components.collections.SessionCollection;
import com.serguni.messenger.dbms.models.Session;
import com.serguni.messenger.dbms.models.User;
import com.serguni.messenger.dbms.repositories.ConfigurationRepository;
import com.serguni.messenger.dbms.repositories.SessionRepository;
import com.serguni.messenger.dbms.repositories.TrackingRepository;
import com.serguni.messenger.dbms.repositories.UserRepository;
import com.serguni.messenger.dto.SessionCookie;
import com.serguni.messenger.dto.SocketMessage;
import com.serguni.messenger.dto.SocketMessage.MessageType;
import com.serguni.messenger.dto.TransferToDto;
import com.serguni.messenger.dto.models.SessionDto;
import com.serguni.messenger.dto.models.UserInfoDto;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

@Component
public class Server extends Thread {

    public static SessionRepository sessionRepository;
    public static UserRepository userRepository;
    public static ConfigurationRepository configurationRepository;
    public static TrackingRepository trackingRepository;

    public static final int PORT = 8081;
//    public static final SessionCollection CLIENT_SESSIONS = new SessionCollection();

//    public static final Map<Long, Map<Long, ListenerService>> USERS_SESSIONS = new HashMap<>();
    public static final SessionCollection USERS_SESSIONS = new SessionCollection();

    private static ServerSocket server;

    static {
        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Не удается подключиться к порту");
            e.printStackTrace();
        }
    }

    public Server(SessionRepository sessionRepository, UserRepository userRepository, ConfigurationRepository configurationRepository, TrackingRepository trackingRepository) {
        Server.sessionRepository = sessionRepository;
        Server.userRepository = userRepository;
        Server.configurationRepository = configurationRepository;
        Server.trackingRepository = trackingRepository;

        this.start();
    }

    public static void deleteAllOtherSessions(Session session, ListenerService currentListenerService) throws IOException {
//        USERS_SESSIONS.removeSession(session);
        USERS_SESSIONS.getSessionsByUserId(session.getUser().getId()).remove(session.getId());

        Map<Long, ListenerService> otherSessions = USERS_SESSIONS.getSessionsByUserId(session.getUser().getId());
        System.out.println("Server -> 61 -> null other sessions");

        for (long otherSessionId : otherSessions.keySet()) {
            otherSessions.get(otherSessionId).closeSocket();
        }
        otherSessions.clear();

        USERS_SESSIONS.addSession(session, currentListenerService);
        sessionRepository.deleteAllByUserId(session.getUser().getId());
        sessionRepository.save(session);
    }

    public static void deleteOtherSession(Session session, long sessionOnDeleteId) throws IOException {
        System.out.println("ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ " + session.getUser().getId() );
        System.out.println("ВСЕ СЕССИИ" + USERS_SESSIONS);
        Map<Long, ListenerService> userSessions = USERS_SESSIONS.getSessionsByUserId(session.getUser().getId());

        if (userSessions.containsKey(sessionOnDeleteId))
            userSessions.remove(sessionOnDeleteId).closeSocket();

        sessionRepository.deleteById(sessionOnDeleteId);

        ListenerService currentListenerService = userSessions.remove(session.getId());

        for (ListenerService listenerService : userSessions.values()) {
            listenerService.out.writeObject(new SocketMessage(MessageType.DELETE_OTHER_SESSION, sessionOnDeleteId));
        }

        userSessions.put(session.getId(), currentListenerService);
    }

    public static void sendNewSession(Session session) {
        SessionDto sessionDto = TransferToDto.getSessionDto(session);
        System.out.println(USERS_SESSIONS);
        System.out.println("ID пользователя сессии " + session.getUser().getId());
        Map<Long, ListenerService> listenerServices = USERS_SESSIONS.getSessionsByUserId(session.getUser().getId());

        if (listenerServices != null) {
            for (ListenerService listenerService : listenerServices.values()) {
                try {
                    listenerService.out.writeObject(new SocketMessage(MessageType.ADD_NEW_SESSION, sessionDto));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendUserStatus(User user, Date lastOnline) {

        UserInfoDto userInfoDto = TransferToDto.UserInfoDto(user);
        userInfoDto.setLastOnline(lastOnline);
        SocketMessage socketMessage = new SocketMessage(MessageType.UPDATE_TRACKING_USER, userInfoDto);

        Map<Long, ListenerService> sessionsOfCurrentUser = USERS_SESSIONS.getSessionsByUserId(user.getId());

        System.out.println("Server -> 95 -> СМОТРИМ СЕССИ ПОЛЬЗОВАТЕЛЯ ПЕРЕД ОТПРАВКОЙ ИЗМ. ПОЛЬЗОВАТЕЛЯ");
        System.out.println(sessionsOfCurrentUser);

        if (sessionsOfCurrentUser != null) {
            for (ListenerService listenerService : sessionsOfCurrentUser.values()) {
                try {
                    listenerService.out.writeObject(socketMessage);
                } catch (IOException info) {
                    info.printStackTrace();
                }
            }
        }

//        Set<Long> trackingUsersByTrackedUserId = CLIENT_SESSIONS.getTrackingUsersByTrackedUserId(user.getId());
//        Set<Long> trackingUsersByTrackedUserId = trackingRepository.getTrackingSessionsIdsByTrackedUserId(user.getId());
        Set<long[]> trackingUsersByTrackedUserId = trackingRepository.getTrackingSessionsIdsByTrackedUserId(user.getId());
        if (trackingUsersByTrackedUserId == null)
            return;

        System.out.println(trackingUsersByTrackedUserId);
        System.out.println(USERS_SESSIONS);
        for (long[] longs : trackingUsersByTrackedUserId) {
            System.out.println(longs[0] + " " + longs[1]);
            ListenerService listenerService = USERS_SESSIONS.getSessionByUserIdAndId(longs[0], longs[1]);
            try {
                listenerService.out.writeObject(socketMessage);
            } catch (IOException info) {
                info.printStackTrace();
            }
        }

//        for (Long trackingUser : trackingUsersByTrackedUserId.keySet()) {
//            for (long trackingSessionId : trackingUsersByTrackedUserId.get(trackingUser)) {
//                ListenerService listenerService = USERS_SESSIONS.getSessionByUserIdAndId(trackingUser, trackingSessionId);
//
//                try {
//                    listenerService.out.writeObject(socketMessage);
//                } catch (IOException info) {
//                    info.printStackTrace();
//                }
//            }
//        }

//        for (Set<Long> trackingUser : trackingUsersByTrackedUserId.values()) {
//            System.out.println("TRACKING USER ID = " + Arrays.toString(trackingUser));
//            System.out.println("USERS_SESSIONS =" + USERS_SESSIONS);
//            for (ListenerService listenerService: USERS_SESSIONS.getSessionsByUserId(trackingUser[0])) {
//                try {
//                    listenerService.out.writeObject(socketMessage);
//                } catch (IOException info) {
//                    info.printStackTrace();
//                }
//            }
//        }
    }

//    public static User getUserById(long userId) {
//        return userRepository.findById(userId).orElse(null);
//    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socketListen = server.accept();

                new Thread(() -> {
                    try {
                        ObjectInputStream objectIs = new ObjectInputStream(socketListen.getInputStream());
                        SessionCookie sessionCookie = (SessionCookie) objectIs.readObject();

                        if (sessionCookie == null) {
                            System.out.println("Неверный запрос от клиента");
                            socketListen.close();
                            return;
                        }

                        Session session = sessionRepository.findById(sessionCookie.getSessionId()).orElse(null);

//                        OutputStream os = socketListen.getOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(socketListen.getOutputStream());
//                        System.out.println(USERS_SESSIONS);
//                        System.out.println(USERS_SESSIONS.containsSession(session) + " " + session.getId());
                        if (session == null ||
                                USERS_SESSIONS.containsSession(session) ||
                                !session.getCookie().equals(sessionCookie.getCookie())) {

                            System.out.println("Несуществующая сессия!");
                            out.writeObject(new SocketMessage(MessageType.LOGIN, null));
                            out.flush();
//                            os.write(0);
//                            os.flush();
                            return;
                        }

                        User user = userRepository.findById(session.getUser().getId()).orElse(null);

                        //ЗДЕСЬ МОЖНО ВЗЯТЬ ЮЗЕРА, ПОДГРУЗИТЬ ДАННЫЕ ЧАТОВ И Т.Д И ОТПРАВИТЬ
                        out.writeObject(new SocketMessage(MessageType.LOGIN, TransferToDto.getUserDto(user)));

                        //ОТПРАВЛЯЕМ ВСЕМ ЧТО ПОЛЬЗОВАТЕЛЬ ONLINE
                        if (!Server.USERS_SESSIONS.isOnlineByUserId(session.getUser().getId())) {
                            System.out.println("ОТПРАВЛЯЕМ ONLINE");
                            Server.sendUserStatus(user, new Date(0));
                        }

                        ListenerService listenerService = new ListenerService(session, socketListen, objectIs, out);
                        USERS_SESSIONS.addSession(session, listenerService);

                        System.out.println("АУУУ");
                        listenerService.start();
                        System.out.println("ПОСЛЕ ЗАПУСКА ПРОСЛУШИВАТЕЛЯ");
                        System.out.println(USERS_SESSIONS);

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }).start();

            } catch (IOException e) {
                System.out.println("Ошибка, не удается проверить подлинность сессии!");
                e.printStackTrace();
            }

        }
    }
}
