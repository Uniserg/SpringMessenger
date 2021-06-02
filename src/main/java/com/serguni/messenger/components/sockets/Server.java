package com.serguni.messenger.components.sockets;

import com.serguni.messenger.components.collections.SessionCollection;
import com.serguni.messenger.components.security.KeyGenerator;
import com.serguni.messenger.dbms.models.*;
import com.serguni.messenger.dbms.repositories.*;
import com.serguni.messenger.dto.SessionCookie;
import com.serguni.messenger.dto.SocketMessage;
import com.serguni.messenger.dto.SocketMessage.MessageType;
import com.serguni.messenger.dto.TransferToDto;
import com.serguni.messenger.dto.models.SessionDto;
import com.serguni.messenger.dto.models.WatchedChatDto;
import com.serguni.messenger.utils.CryptoUtil;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
    public static ChatRepository chatRepository;
    public static PrivateChatRepository privateChatRepository;
    public static WatchedChatRepository watchedChatRepository;
    public static MessageRepository messageRepository;

    public static final int PORT = 8081;
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

    public Server(SessionRepository sessionRepository,
                  UserRepository userRepository,
                  ConfigurationRepository configurationRepository,
                  TrackingRepository trackingRepository,
                  ChatRepository chatRepository,
                  PrivateChatRepository privateChatRepository,
                  WatchedChatRepository watchedChatRepository,
                  MessageRepository messageRepository) {
        Server.sessionRepository = sessionRepository;
        Server.userRepository = userRepository;
        Server.configurationRepository = configurationRepository;
        Server.trackingRepository = trackingRepository;
        Server.chatRepository = chatRepository;
        Server.privateChatRepository = privateChatRepository;
        Server.watchedChatRepository = watchedChatRepository;
        Server.messageRepository = messageRepository;

        this.start();
    }

    public static void deleteAllOtherSessions(Session session, ListenerService currentListenerService) throws IOException {
//        USERS_SESSIONS.removeSession(session);
        USERS_SESSIONS.getSessionsByUserId(session.getUser().getId()).remove(session.getId());

        Map<Long, ListenerService> otherSessions = USERS_SESSIONS.getSessionsByUserId(session.getUser().getId());
        System.out.println("Server -> 61 -> null other sessions");

        for (long otherSessionId : otherSessions.keySet()) {
            otherSessions.remove(otherSessionId).closeSocket();
            trackingRepository.deleteAllTrackedUsersByTrackingSessionsId(otherSessionId);
        }

        USERS_SESSIONS.addSession(session, currentListenerService);
        sessionRepository.deleteAllByUserId(session.getUser().getId());
        sessionRepository.save(session);
    }

    public static void exit(ListenerService listenerService) {
        Session session = listenerService.getSession();
        //ПРОВЕРКА ЧТО ПОЛЬЗОВАТЕЛЬ УЖЕ НЕ В СЕТИ (ЭТО ВОЗМОЖНО, ЕСЛИ ПРОИЗОШЛО УДАЛЕНИЕ СЕССИИ ИЗ ДРУГОЙ)
        if (!USERS_SESSIONS.containsSession(session))
            return;

        System.out.println("Пользователь отключился");

        try {
            Date lastOnline = new Date();
            session.setLastOnline(lastOnline);
            User user = Server.userRepository.findById(session.getUser().getId()).orElse(null);
            user.setLastOnline(lastOnline);

            Server.userRepository.save(user);
            Server.sessionRepository.save(session);
        } catch (ObjectOptimisticLockingFailureException ignored) {
            //ЗДЕСЬ ВОЗНИКАЕТ ОШИБКА КОГДА МЫ УДАЛЯЕМ СЕССИЮ А ДРУГОЙ ПЫТАЕТСЯ ЕЕ СОХРАНИТЬ ПРИ СБРОСЕ
        }

        listenerService.closeSocket();

        USERS_SESSIONS.removeSessionWithUser(session);
        trackingRepository.deleteAllTrackedUsersByTrackingSessionsId(session.getId());
//        User user = Server.userRepository.findById(session.getUser().getId()).orElse(null);
//        assert user != null;
        // ОТПРАВКА СООБЩЕНИЯ ЧТО ПОЛЬЗОВАТЕЛЬ ВЫШЕЛ
        if (!Server.USERS_SESSIONS.isOnlineByUserId(session.getUser().getId())) {
//            User user = Server.userRepository.findById(session.getUser().getId()).orElse(null);
            // ВОЗМОЖНА ОШИБКА
            Server.sendLastOnlineToTrackingUsers(session.getUser().getId(), session.getLastOnline());
        }
        Server.sendLastOnlineToOtherUserSessions(session.getUser().getId(), session.getId(), new Date());

        System.out.println("УДАЛИЛИ ОТСЛЕЖИВАЕМЫЕ ОБЪЕКТЫ");
    }

    public static void deleteOtherSession(Session session, long sessionOnDeleteId) throws IOException {
        Map<Long, ListenerService> userSessions = USERS_SESSIONS.getSessionsByUserId(session.getUser().getId());
//        if (userSessions.containsKey(sessionOnDeleteId))
//            userSessions.remove(sessionOnDeleteId).closeSocket();

//        USERS_SESSIONS.removeSessionWithUser(session);
        userSessions.remove(sessionOnDeleteId).closeSocket();

        sessionRepository.deleteById(sessionOnDeleteId);
//        session.getUser().getSessions().remove(session);

        trackingRepository.deleteAllTrackedUsersByTrackingSessionsId(sessionOnDeleteId);

        ListenerService currentListenerService = userSessions.remove(session.getId());

        for (ListenerService listenerService : userSessions.values()) {
            listenerService.out.writeObject(new SocketMessage(MessageType.DELETE_OTHER_SESSION, sessionOnDeleteId));
        }
        // ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ ВЫШЕЛ (НЕ LOGOUT)
        if (currentListenerService != null)
            userSessions.put(session.getId(), currentListenerService);
    }

    public static void sendNewSession(Session session) {
        SessionDto sessionDto = TransferToDto.getSessionDto(session);
        System.out.println("ID пользователя сессии " + session.getUser().getId());
        System.out.println(USERS_SESSIONS);
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

    private static void sendToOtherUserSessions(long userId, SocketMessage socketMessage) {
        Map<Long, ListenerService> userSessions = USERS_SESSIONS.getSessionsByUserId(userId);

        if (userSessions == null)
            return;

        for (ListenerService listenerService : USERS_SESSIONS.getSessionsByUserId(userId).values()) {
            try {
                listenerService.out.writeObject(socketMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendToTrackingUser(long userId, SocketMessage socketMessage) {
        Set<long[]> trackingUsers = trackingRepository.getTrackingSessionsIdsByTrackedUserId(userId);

        if (trackingUsers == null)
            return;

        for (long[] trackingUserSession : trackingUsers) {
            try {
                USERS_SESSIONS.getSessionByUserIdAndId(trackingUserSession[0], trackingUserSession[1]).out.writeObject(socketMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private static void sendLastOnlineToUserSessions() {
//    }




    public static void sendEditName(long userId, String lastName, String firstName) {
        SocketMessage socketMessage = new SocketMessage(MessageType.EDIT_NAME, new Object[] {userId, lastName, firstName});
        sendToOtherUserSessions(userId, socketMessage);
        sendToTrackingUser(userId, socketMessage);
    }

    public static void sendAboutMe(long userId, String aboutMe) {
        SocketMessage socketMessage = new SocketMessage(MessageType.EDIT_ABOUT_ME, new Object[] {userId, aboutMe});
        sendToOtherUserSessions(userId, socketMessage);
        sendToTrackingUser(userId, socketMessage);
    }

    public static void sendAvatar(long userId, byte[] avatar) {
        SocketMessage socketMessage = new SocketMessage(MessageType.EDIT_AVATAR, new Object[] {userId, avatar});
        sendToOtherUserSessions(userId, socketMessage);
        sendToTrackingUser(userId, socketMessage);
    }

    public static void sendLastOnlineToTrackingUsers(long userId, Date lastOnline) {
        SocketMessage socketMessage = new SocketMessage(MessageType.UPDATE_LAST_ONLINE_TO_TRACKING_USERS, new Object[] {userId, lastOnline});
        sendToTrackingUser(userId, socketMessage);
        sendToOtherUserSessions(userId, socketMessage);
//        sendToOtherUserSessions(userId, );
    }

    public static void sendLastOnlineToOtherUserSessions(long userId, long sessionId, Date lastOnline) {
        System.out.println("Server -> 199 -> проверка отправки");
        SocketMessage socketMessage = new SocketMessage(MessageType.UPDATE_LAST_ONLINE_TO_OTHER_USER_SESSIONS,
                new Object[]{sessionId, lastOnline});
        sendToOtherUserSessions(userId, socketMessage);
    }

    public static void createNewPrivateChat(User user, long otherUserId) {
        User otherUser = userRepository.findById(otherUserId).orElse(null);

        if (otherUser == null)
            return;

        KeyGenerator keyGenerator = new KeyGenerator();
        String secretKey = CryptoUtil.createHash(keyGenerator.getSecretKey());
        long chatId = chatRepository.getNextId();

        Chat chat = new Chat(chatId, secretKey);
        PrivateChat privateChat = new PrivateChat(user, otherUser, chat);
        chat.setPrivateChat(privateChat);

        chatRepository.save(chat);
        privateChatRepository.save(privateChat);

        String watchedChatName = otherUser.getLastName() + " " + otherUser.getFirstName();
        if (watchedChatName.equals(" "))
            watchedChatName = otherUser.getNickname();

        WatchedChat watchedChat = new WatchedChat(
                new WatchedChat.WatchedChatPK(chat.getId(), user.getId()),
                watchedChatName,
                new Date(),
                false,
                false
        );

        //ТОТ КТО СОЗДАЛ
        watchedChat.setUser(user);
        watchedChatRepository.save(watchedChat);

        SocketMessage socketMessage = new SocketMessage(MessageType.CREATE_NEW_CHAT, watchedChat);
        sendToOtherUserSessions(user.getId(), socketMessage);


        // ДРУГОЙ ПОЛЬЗОВАТЕЛЬ
        watchedChatName = user.getLastName() + " " + user.getFirstName();
        if (watchedChatName.equals(" "))
            watchedChatName = otherUser.getNickname();

        watchedChat.setName(watchedChatName);
        watchedChat.setUser(otherUser);

        watchedChatRepository.save(watchedChat);

        socketMessage = new SocketMessage(MessageType.CREATE_NEW_CHAT, watchedChat);
        sendToOtherUserSessions(otherUserId, socketMessage);

    }

    public static Chat createNewPrivateChat(Session session, long otherUserId) throws IOException {
        User otherUser = userRepository.findById(otherUserId).orElse(null);

        Chat chat = new Chat();
        chat.setSecretKey(CryptoUtil.createHash(new KeyGenerator().getSecretKey()));
        chat = chatRepository.save(chat);

        PrivateChat privateChat = new PrivateChat();
        privateChat.setPrivateChatPK(
                new PrivateChat.PrivateChatPK(
                        chat.getId(),
                        session.getUser().getId(),
                        otherUser.getId()));

        Server.privateChatRepository.save(privateChat);

        Date syncDate = new Date();

        WatchedChat watchedChatUser = new WatchedChat(
                new WatchedChat.WatchedChatPK(chat.getId(), session.getUser().getId()),
                otherUser.getNickname(),
                syncDate,
                false,
                false
        );
        watchedChatUser.setUser(session.getUser());

        WatchedChat watchedChatOtherUser = new WatchedChat(
                new WatchedChat.WatchedChatPK(chat.getId(), otherUser.getId()),
                session.getUser().getNickname(),
                syncDate,
                false,
                false
        );
        watchedChatUser.setUser(otherUser);

        chat.setWatchedChats(new HashSet<>());
        chat.getWatchedChats().add(watchedChatOtherUser);
        chat.getWatchedChats().add(watchedChatUser);

        watchedChatRepository.save(watchedChatUser);
        watchedChatRepository.save(watchedChatOtherUser);

        watchedChatUser = watchedChatRepository.findById(watchedChatUser.getWatchedChatPK()).orElse(null);
        watchedChatOtherUser = watchedChatRepository.findById(watchedChatOtherUser.getWatchedChatPK()).orElse(null);

        WatchedChatDto watchedChatUserDto = TransferToDto.getWatchedChatDto(watchedChatUser);
        WatchedChatDto watchedChatOtherUserDto = TransferToDto.getWatchedChatDto(watchedChatOtherUser);

        SocketMessage messageForUser = new SocketMessage(MessageType.CREATE_NEW_CHAT,
                new Object[] {TransferToDto.getUserInfoDto(otherUser), watchedChatUserDto});
        SocketMessage messageForOtherUser = new SocketMessage(MessageType.CREATE_NEW_CHAT,
                new Object[] {TransferToDto.getUserInfoDto(session.getUser()), watchedChatOtherUserDto});

        for (ListenerService listenerService : USERS_SESSIONS.getSessionsByUserId(session.getUser().getId()).values()) {
            listenerService.out.writeObject(messageForUser);
            System.out.println("ОТПРАВИЛИ ВСЕМ СЕССИЯМ ДЛЯ ЭТОГО ПОЛЬЗОВАТЕЛЯ");
        }

        if (otherUserId != session.getUser().getId() && USERS_SESSIONS.isOnlineByUserId(otherUserId)) {
            for (ListenerService listenerService : USERS_SESSIONS.getSessionsByUserId(otherUserId).values()) {
                listenerService.out.writeObject(messageForOtherUser);
                System.out.println("ОТПРАВИЛИ ВСЕМ СЕССИЯМ ДЛЯ ДРУГОГО ПОЛЬЗОВАТЕЛЯ");
            }
        }

        System.out.println("МЫ СОЗДАЛИ ЧАТЫ НАДО БЫ ИХ ОТПРАВИТЬ ПОЛЬЗОВАТЕЛЯМ!!! (WATCHED CHATS) Server - 322");

        return chatRepository.findById(chat.getId()).orElse(null);
    }

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
                        ObjectOutputStream out = new ObjectOutputStream(socketListen.getOutputStream());

                        if (session == null ||
                                USERS_SESSIONS.containsSession(session) ||
                                !session.getCookie().equals(sessionCookie.getCookie())) {

                            System.out.println("Несуществующая сессия!");
                            out.writeObject(new SocketMessage(MessageType.LOGIN, null));
                            out.flush();

                            return;
                        }
                        session.setLastOnline(new Date(0));

                        User user = userRepository.findById(session.getUser().getId()).orElse(null);
                        assert user != null;
                        long userId = user.getId();
                        long sessionId = session.getId();

                        Map<Long, ListenerService> userSessions = USERS_SESSIONS.getSessionsByUserId(userId);

                        if  (userSessions != null) {
                            for (ListenerService listenerService : USERS_SESSIONS.getSessionsByUserId(userId).values()) {
                                Session otherUserSession = listenerService.getSession();
                                //МАГИЯ ХЭШЕЙ
                                // ДОБАВЛЕНИЕ АКТУАЛЬНЫХ ДАННЫХ О СЕССИЯХ
                                user.getSessions().remove(otherUserSession);
                                user.getSessions().add(otherUserSession);
                            }
                        }

                        for (WatchedChat watchedChat : user.getWatchedChats()) {
                            Set<WatchedChat> watchedChats = watchedChat.getChat().getWatchedChats();
                            watchedChats.remove(watchedChat);

                            for (WatchedChat otherWatchedChats : watchedChats) {
                                long otherUserId = otherWatchedChats.getUser().getId();

                                trackingRepository.save(new Tracking(
                                        otherUserId,
                                        userId,
                                        sessionId
                                ));

//                                if (USERS_SESSIONS.isOnlineByUserId(otherUserId)) {
//                                    for (long sessionOtherUserId : USERS_SESSIONS.getSessionsByUserId(otherUserId).keySet()) {
//                                        trackingRepository.save(new Tracking(
//                                                userId,
//                                                otherUserId,
//                                                sessionOtherUserId));
//                                    }
//                                }
                            }
                        }

                        //ЗДЕСЬ МОЖНО ВЗЯТЬ ЮЗЕРА, ПОДГРУЗИТЬ ДАННЫЕ ЧАТОВ И Т.Д И ОТПРАВИТЬ
                        out.writeObject(new SocketMessage(MessageType.LOGIN, TransferToDto.getUserDto(user)));

                        //ОТПРАВЛЯЕМ ВСЕМ ЧТО ПОЛЬЗОВАТЕЛЬ ONLINE
                        if (!Server.USERS_SESSIONS.isOnlineByUserId(userId)) {
                            System.out.println("ОТПРАВЛЯЕМ ONLINE");
                            sendLastOnlineToTrackingUsers(userId, new Date(0));
                        }

                        sendLastOnlineToOtherUserSessions(userId, sessionId, new Date(0));

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
