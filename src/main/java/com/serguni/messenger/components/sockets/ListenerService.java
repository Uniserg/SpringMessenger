package com.serguni.messenger.components.sockets;

import com.serguni.messenger.dbms.models.*;
import com.serguni.messenger.dto.SocketMessage;
import com.serguni.messenger.dto.SocketMessage.MessageType;
import com.serguni.messenger.dto.TransferToDto;
import com.serguni.messenger.dto.models.ConfigurationDto;
import com.serguni.messenger.dto.models.MessageDto;
import com.serguni.messenger.dto.models.UserInfoDto;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ListenerService extends Thread {

    private final Session session;
    private final Socket clientSocket;
    public final ObjectInputStream in;
    public final ObjectOutputStream out;

    public ListenerService(Session session,
                           Socket socket,
                           ObjectInputStream in,
                           ObjectOutputStream out) throws IOException {
        this.in = in;
        this.out = out;
        this.session = session;
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        for (WatchedChat watchedChat : session.getUser().getWatchedChats()) {
            System.out.println(watchedChat.getWatchedMessages());
        }
        System.out.println("СЛУШАТЕЛЬ ЗАПУЩЕН!");
        try {
            while (true) {
                SocketMessage message = (SocketMessage) in.readObject();


                switch (message.getType()) {
                    case SEARCH_USER -> findUsers((String) message.getBody());

                    case LOGOUT -> {
                        System.out.println("ДО УДАЛЕНИЯ ИЗ USER_SESSION - LOGOUT - ListenerService - 46");
                        System.out.println(Server.USERS_SESSIONS);
                        System.out.println("ПОСЛЕ УДАЛЕНИЯ ИЗ USER_SESSION - LOGOUT - ListenerService - 48");
                        System.out.println(Server.USERS_SESSIONS);
                        Server.deleteOtherSession(session, session.getId());

                        if (!Server.USERS_SESSIONS.isOnlineByUserId(session.getUser().getId())) {
                            Date lastOnline = new Date();
                            session.getUser().setLastOnline(lastOnline);
                            session.getUser().setSessions(null);
                            Server.userRepository.save(session.getUser());
                            Server.sendLastOnlineToTrackingUsers(session.getUser().getId(), lastOnline);
                        }

                        System.out.println(Server.USERS_SESSIONS + "ПОСЛЕ ВЫХОДА ИЗ СЕССИИ LISTENER SERVICE - 52");
                        return;
                    }

                    case EDIT_NAME -> {
                        String[] name = (String[]) message.getBody();
                        editName(name[0], name[1]);
                    }

                    case EDIT_ABOUT_ME ->  {
                        editAboutMe((String) message.getBody());

                    }

                    case EDIT_AVATAR -> editAvatar((byte[]) message.getBody());


                    case EDIT_CONFIGURATION -> editConfiguration((ConfigurationDto) message.getBody());

                    case DELETE_ALL_OTHER_SESSIONS -> Server.deleteAllOtherSessions(session, this);

                    case DELETE_OTHER_SESSION -> Server.deleteOtherSession(session, (long)message.getBody());

                    case CHAT_MESSAGE -> {

                        Object[] objects = (Object[]) message.getBody();
                        MessageDto messageDto = (MessageDto) objects[0];

                        long otherUserId = (long) objects[1];
                        long userId = session.getUser().getId();

                        Chat chat;

                        if (messageDto.getChatId() == -1) {
                            PrivateChat existPrivateChat = Server.privateChatRepository.findFirstByUser1IdAndUser2IdOrUser2IdAndUser1Id(
                                    userId,
                                    otherUserId,
                                    userId,
                                    otherUserId
                            );

                            if (existPrivateChat == null) {
                                chat = Server.createNewPrivateChat(session, otherUserId);

                            } else {
                                chat = existPrivateChat.getChat();
                            }
                        } else {
                            chat = Server.chatRepository.findById(messageDto.getChatId()).orElse(null);
                        }

                        // создали сообщение!!!
                        Message messageToAdd = new Message();
                        messageToAdd.setChat(chat);
                        messageToAdd.setText(messageDto.getText());
                        messageToAdd.setSendTime(new Date());
                        messageToAdd.setUser(session.getUser());
                        Server.messageRepository.save(messageToAdd);

                        for (WatchedChat watchedChat : chat.getWatchedChats()) {
                            Set<Message> messages = watchedChat.getWatchedMessages();
                            if (messages == null)
                                messages = new HashSet<>();

                            messages.add(messageToAdd);
                            Server.watchedChatRepository.save(watchedChat);

                            if (Server.USERS_SESSIONS.isOnlineByUserId(watchedChat.getUser().getId())) {
                                for (ListenerService listenerService : Server.USERS_SESSIONS.getSessionsByUserId(watchedChat.getUser().getId()).values()) {

                                    long senTo = (watchedChat.getUser().getId() == userId) ? otherUserId : userId;

                                    SocketMessage socketMessage = new SocketMessage(MessageType.CHAT_MESSAGE,
                                            new Object[] {senTo, TransferToDto.getMessageDto(messageToAdd)});
                                    listenerService.out.writeObject(socketMessage);
                                }
                            }
                        }
                        System.out.println(chat.getWatchedChats() + "ВСЕ ОТСЛЕЖИВАЮЩИЕ ЧАТЫ");
                        //создали сообщение!!!

                        System.out.println("ПОЛЬЗОВАТЕЛЬ ПРИСЛАЛ СООБЩЕНИЕ ДРУГОМУ ПОЛЬЗОВАТЕЛЮ");
                        System.out.println(messageDto.getText());

                        // РАССЫЛАЕМ ВСЕМ.
                    }
                }
            }

        }
        catch (IOException e) {
            Server.exit(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void findUsers(String userNick) throws IOException {
        new Thread(() -> {
            Server.trackingRepository.deleteAllTrackedUsersByTrackingSessionsId(session.getId());
            Server.trackingRepository.flush();

            Set<Configuration> configurations = Server.configurationRepository
                    .findByInvisibleEqualsAndUserNicknameStartingWith(false, userNick);

            Set<UserInfoDto> userInfoDtos = new HashSet<>();

            for (Configuration configuration : configurations) {
                User user = configuration.getUser();

                Server.trackingRepository.save(new Tracking(user.getId(), session.getUser().getId(), session.getId()));

                UserInfoDto userInfoDto = TransferToDto.getUserInfoDto(configuration.getUser());

                if (Server.USERS_SESSIONS.isOnlineByUserId(user.getId())) {
                    userInfoDto.setLastOnline(new Date(0));
                }

                System.out.println(userInfoDto.getLastOnline());

                userInfoDtos.add(userInfoDto);
            }

            SocketMessage message = new SocketMessage(MessageType.SEARCH_USER, userInfoDtos);

            try {
                out.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void editConfiguration(ConfigurationDto configurationDto) {
        Thread thread = new Thread(() -> {
            Configuration configuration = new Configuration(configurationDto);
            User user = Server.userRepository.findById(session.getUser().getId()).orElse(null);

            configuration.setUser(user);
            Server.configurationRepository.save(configuration);
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void editAboutMe(String aboutMe) {
        Thread thread = new Thread(() -> {

            User user = Server.userRepository.findById(session.getUser().getId()).orElse(null);
            assert user != null;
            user.setAboutMe(aboutMe);

            Server.sendAboutMe(user.getId(), aboutMe);

            Server.userRepository.save(user);
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void editName(String lastName, String firstName) {
        Thread thread = new Thread(() -> {

            User user = Server.userRepository.findById(session.getUser().getId()).orElse(null);
            assert user != null;

            user.setLastName(lastName);
            user.setFirstName(firstName);

            Server.userRepository.save(user);
            Server.sendEditName(session.getUser().getId(), lastName, firstName);
        });

        thread.start();
    }

    private void editAvatar(byte[] avatar) {
        Thread thread = new Thread(() -> {
            User user = Server.userRepository.findById(session.getUser().getId()).orElse(null);
            assert user != null;
            user.setAvatar(avatar);
            System.out.println("ОТПРАВИЛИ");
            System.out.println("МЫ ТУТ - ЭТО РАБОТАЕТ");
            Server.userRepository.save(user);

            Server.sendAvatar(user.getId(), avatar);
        });


        thread.setDaemon(true);
        thread.start();
    }

    private void createNewPrivateChat(long otherUser) {
        Server.createNewPrivateChat(session.getUser(), otherUser);
    }

    private void logoutUser() {
        System.out.println("УДАЛЕНИЕ СЕССИИ");
        System.out.println(session);
        Server.sessionRepository.delete(session);
//        sessionRepository.deleteAllInBatch();
        Server.USERS_SESSIONS.removeSessionWithUser(session);
        System.out.println("СЕССИЯ УДАЛЕНА");
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }
}
