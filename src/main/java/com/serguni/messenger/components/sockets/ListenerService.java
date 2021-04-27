package com.serguni.messenger.components.sockets;

import com.serguni.messenger.dbms.models.Configuration;
import com.serguni.messenger.dbms.models.Session;
import com.serguni.messenger.dbms.models.Tracking;
import com.serguni.messenger.dbms.models.User;
import com.serguni.messenger.dbms.repositories.ConfigurationRepository;
import com.serguni.messenger.dbms.repositories.SessionRepository;
import com.serguni.messenger.dbms.repositories.UserRepository;
import com.serguni.messenger.dto.SocketMessage;
import com.serguni.messenger.dto.SocketMessage.MessageType;
import com.serguni.messenger.dto.TransferToDto;
import com.serguni.messenger.dto.models.ConfigurationDto;
import com.serguni.messenger.dto.models.UserInfoDto;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ListenerService extends Thread {

    private final Session session;
    private final Socket clientSocket;
    public final ObjectInputStream in;
    public final ObjectOutputStream out;
//    private final SessionRepository sessionRepository;
//    private final UserRepository userRepository;
//    private final ConfigurationRepository configurationRepository;


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
        System.out.println("СЛУШАТЕЛЬ ЗАПУЩЕН!");
        try {
            while (true) {
                SocketMessage message = (SocketMessage) in.readObject();

                switch (message.getType()) {
                    case SEARCH_USER -> findUsers((String) message.getBody());
                    case LOGOUT -> {
                        logoutUser();
                        return;
                    }
                    case EDIT_NAME -> {
                        String[] name = (String[]) message.getBody();
                        editName(name[0], name[1]);
                    }
                    case EDIT_ABOUT_ME -> editAboutMe((String) message.getBody());
                    case EDIT_AVATAR -> editAvatar((byte[]) message.getBody());

                    case EDIT_CONFIGURATION -> editConfiguration((ConfigurationDto) message.getBody());
                    case DELETE_ALL_OTHER_SESSIONS -> Server.deleteAllOtherSessions(session, this);
                }

//                if (message.getType() == SocketMessage.MessageType.SEARCH_USER) {
//                    findUsers((String) message.getBody());
//                }

            }

        }
//        catch (NullPointerException e) {
//            sessionRepository.deleteById(session.getId());
//
//            try {
//                clientSocket.close();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//
//            System.out.println("Пользователь вышел");
//            Server.CLIENT_SESSIONS.remove(session);
//
//        }
        catch (IOException e) {

            System.out.println("Пользователь отключился");
            try {
                session.setLastOnline(new Date());
                Server.sessionRepository.save(session);
            } catch (ObjectOptimisticLockingFailureException ignored) {
                //ЗДЕСЬ ВОЗНИКАЕТ ОШИБКА КОГДА МЫ УДАЛЯЕМ СЕССИЮ А ДРУГОЙ ПЫТАЕТСЯ ЕЕ СОХРАНИТЬ ПРИ СБРОСЕ
            }
            try {
                clientSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            Server.USERS_SESSIONS.removeSession(session);
            Server.trackingRepository.deleteAllTrackedUsersByTrackingSessionsId(session.getId());

            // ОТПРАВКА СООБЩЕНИЯ ЧТО ПОЛЬЗОВАТЕЛЬ ВЫШЕЛ
            if (!Server.USERS_SESSIONS.isOnlineByUserId(session.getUser().getId())) {
                Server.sendUserStatus(session.getUser(), session.getLastOnline());
            }

            System.out.println("УДАЛИЛИ ОТСЛЕЖИВАЕМЫЕ ОБЪЕКТЫ");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void findUsers(String userNick) throws IOException {
        new Thread(() -> {
            Server.trackingRepository.deleteAllTrackedUsersByTrackingSessionsId(session.getId());
            Server.trackingRepository.flush();

//            List<User> users = userRepository.findByNicknameStartingWith(userNick);
//            List<UserInfoDto> userInfoDtos = new ArrayList<>();

            Set<Configuration> configurations = Server.configurationRepository
                    .findByInvisibleEqualsAndUserNicknameStartingWith(false, userNick);

            Set<UserInfoDto> userInfoDtos = new HashSet<>();

            for (Configuration configuration : configurations) {
                User user = configuration.getUser();

                Server.trackingRepository.save(new Tracking(user.getId(), session.getUser().getId(),session.getId()));

                UserInfoDto userInfoDto = TransferToDto.UserInfoDto(configuration.getUser());

                if (Server.USERS_SESSIONS.isOnlineByUserId(user.getId())) {
                    userInfoDto.setLastOnline(new Date(0));
                } else {
                    userInfoDto.setLastOnline(Server.sessionRepository.getLastOnlineOfUser(user.getId()));
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
            configuration.setUser(session.getUser());
            Server.configurationRepository.save(configuration);
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void editAboutMe(String aboutMe) {
        Thread thread = new Thread(() -> {

            User user = session.getUser();
            user.setAboutMe(aboutMe);

            //ИЗМЕНЕНИЕ О СЕБЕ ОТПРАВЛЯЕМ СРАЗУ ВСЕМ
            Server.sendUserStatus(user, new Date(0));

            Server.userRepository.save(user);
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void editName(String lastName, String firstName) {
        Thread thread = new Thread(() -> {

            User user = session.getUser();

            user.setLastName(lastName);
            user.setFirstName(firstName);


            //ИЗМЕНЕНИЕ ИМЕНИ ОТПРАВЛЯЕМ СРАЗУ ВСЕМ
            Server.sendUserStatus(user, new Date(0));

            Server.userRepository.save(user);
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void editAvatar(byte[] avatar) {
        Thread thread = new Thread(() -> {
            User user = session.getUser();
            user.setAvatar(avatar);

            Server.sendUserStatus(user, new Date(0));
            System.out.println("ОТПРАВИЛИ");
            Server.userRepository.save(user);
        });


        thread.setDaemon(true);
        thread.start();
    }

    private void logoutUser() {
        System.out.println("УДАЛЕНИЕ СЕССИИ");
        System.out.println(session);
        Server.sessionRepository.delete(session);
//        sessionRepository.deleteAllInBatch();
        Server.USERS_SESSIONS.removeSession(session);
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

//    private void send(SocketMessage message) {
//        try {
//        } catch (IOException ignored) {
//        }
//    }

}
