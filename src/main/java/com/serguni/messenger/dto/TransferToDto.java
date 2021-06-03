package com.serguni.messenger.dto;

import com.serguni.messenger.components.sockets.Server;
import com.serguni.messenger.dbms.models.*;
import com.serguni.messenger.dto.models.*;

import java.util.*;

public class TransferToDto {

    public static GroupDto getGroupDto(Group group) {
        GroupDto groupDto = new GroupDto(
                group.getId(),
                group.getIcon(),
                group.getName(),
                group.getPassword()
        );
        return groupDto;
    }

    public static UserInfoDto getUserInfoDto(User user) {
        byte[] avatar = Server.userRepository.getAvatarById(user.getId());
        Date lastOnline = Server.USERS_SESSIONS.isOnlineByUserId(user.getId()) ? new Date(0) : user.getLastOnline();
        UserInfoDto userInfoDto = new UserInfoDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAboutMe(),
                avatar,
                lastOnline
        );
        return userInfoDto;
    }

    public static UserDto getUserDto(User user) {

        System.out.println("TRANSFER TO DTO - 36 - ПРОВЕРКА ЧАТОВ");
        System.out.println(user.getWatchedChats().size());

        byte[] avatar = Server.userRepository.getAvatarById(user.getId());
        UserDto userDto = new UserDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAboutMe(),
                avatar,
                user.getLastOnline());

        Set<SessionDto> sessions = null;
        Set<UserInfoDto> friends = null;
        Map<UserInfoDto, WatchedChatDto> watchedPrivateChats = null;
        Map<GroupDto, WatchedChatDto> watchedGroupChats = null;
        Set<BlockedUserDto> blockedUserDtos = null;
        Set<FriendRequestDto> incomingFriendsRequests = null;
        Set<FriendRequestDto> outgoingFriendsRequests = null;


        if (user.getSessions() != null) {
            sessions = new HashSet<>();
            for (Session session : user.getSessions()) {
                sessions.add(TransferToDto.getSessionDto(session));
            }
        }

        if (user.getFriends() != null) {
            friends = new HashSet<>();
            for (User friend : user.getFriends()) {
                friends.add(TransferToDto.getUserInfoDto(friend));
            }
        }

        if (user.getWatchedChats() != null) {
            watchedPrivateChats = new HashMap<>();
            watchedGroupChats = new HashMap<>();

            for (WatchedChat watchedChat : user.getWatchedChats()) {
                Chat chat = watchedChat.getChat();

                if (chat.getPrivateChat() != null) {
                    PrivateChat privateChat = chat.getPrivateChat();
                    User otherUser = (user.getId() != privateChat.getUser1().getId())
                            ? privateChat.getUser1()
                            : privateChat.getUser2();

                    if (Server.USERS_SESSIONS.isOnlineByUserId(otherUser.getId())) {
                        otherUser.setLastOnline(new Date(0));
                    }

                    watchedPrivateChats.put(TransferToDto.getUserInfoDto(otherUser),
                            TransferToDto.getWatchedChatDto(watchedChat));
                } else {
                    watchedGroupChats.put(TransferToDto.getGroupDto(chat.getGroupChat()),
                            TransferToDto.getWatchedChatDto(watchedChat));
                }
            }
        }

        if (user.getBlockedUsers() != null) {
            blockedUserDtos = new HashSet<>();
            for (BlockedUser blockedUser : user.getBlockedUsers()) {
                blockedUserDtos.add(TransferToDto.getBlockedUserDto(blockedUser));
            }
        }

        if (user.getIncomingFriendRequests() != null) {
            incomingFriendsRequests = new HashSet<>();
            for (FriendRequest friendRequest : user.getIncomingFriendRequests()) {
                incomingFriendsRequests.add(TransferToDto.getFriendRequestDto(friendRequest));
            }
        }

        if (user.getOutgoingFriendRequests() != null) {
            outgoingFriendsRequests = new HashSet<>();
            for (FriendRequest friendRequest : user.getIncomingFriendRequests()) {
                outgoingFriendsRequests.add(TransferToDto.getFriendRequestDto(friendRequest));
            }
        }

        userDto.setConfiguration(TransferToDto.getConfigurationDto(user.getConfiguration()));
        userDto.setSessions(sessions);
        userDto.setFriends(friends);
        userDto.setBlockedUsers(blockedUserDtos);
        userDto.setWatchedPrivateChats(watchedPrivateChats);
        userDto.setWatchedGroupChats(watchedGroupChats);
        userDto.setIncomingFriendRequests(incomingFriendsRequests);
        userDto.setOutgoingFriendRequests(outgoingFriendsRequests);

        return userDto;
    }

    public static BlockedUserDto getBlockedUserDto(BlockedUser blockedUser) {
        BlockedUserDto blockedUserDto = new BlockedUserDto(
                blockedUser.getUserId(),
                blockedUser.getBlockedUserId(),
                TransferToDto.getUserInfoDto(blockedUser.getBlockedUser()),
                blockedUser.getBlockTime());

        return blockedUserDto;
    }

    public static FriendRequestDto getFriendRequestDto(FriendRequest friendRequest) {
        FriendRequestDto friendRequestDto = new FriendRequestDto(
                friendRequest.getFromUserId(),
                friendRequest.getToUserId(),
                friendRequest.getSendTime(),
                friendRequest.getAcceptTime(),
                TransferToDto.getUserInfoDto(friendRequest.getFromUser()),
                TransferToDto.getUserInfoDto(friendRequest.getToUser())
        );

        return friendRequestDto;
    }

    public static ConfigurationDto getConfigurationDto(Configuration configuration) {
        ConfigurationDto configurationDto = new ConfigurationDto(
                configuration.getUserId(),
                configuration.isMsgFromFriendsOnly(),
                configuration.isShowLastOnline(),
                configuration.isInvisible()
        );

        return configurationDto;
    }

    public static SessionDto getSessionDto(Session session) {
        SessionDto sessionDto = new SessionDto(
                session.getId(),
                session.getSignInTime(),
                session.getLastOnline(),
                session.getIp(),
                session.getDevice(),
                session.getOs(),
                session.getLocation(),
                session.isActive(),
                session.getCookie()
        );

        return sessionDto;
    }

    public static WatchedChatDto getWatchedChatDto(WatchedChat watchedChat) {
        SortedSet<UserInfoDto> userDtos = new TreeSet<>();

        for (WatchedChat chatOfUser : watchedChat.getChat().getWatchedChats()) {
            User user = chatOfUser.getUser();
            if (Server.USERS_SESSIONS.isOnlineByUserId(user.getId())) {
                user.setLastOnline(new Date(0));
            }
            userDtos.add(getUserInfoDto(user));
        }

        SortedSet<MessageDto> messageDtos = new TreeSet<>();
        for (Message message : watchedChat.getWatchedMessages()) {
            messageDtos.add(TransferToDto.getMessageDto(message));
        }

        WatchedChatDto watchedChatDto = new WatchedChatDto(
                watchedChat.getChat().getId(),
                watchedChat.getUser().getId(),
                watchedChat.getName(),
                watchedChat.getSyncTime(),
                watchedChat.isAdmin(),
                watchedChat.isBlocked(),
                messageDtos,
                userDtos);

        return watchedChatDto;
    }

    public static ChatDto getChatDto(Chat chat) {
        SortedSet<MessageDto> messageDtos = new TreeSet<>();
        for (Message message : chat.getMessages()) {
            messageDtos.add(TransferToDto.getMessageDto(message));
        }
        Set<WatchedChatDto> watchedChatDtos = new HashSet<>();
        for (WatchedChat watchedChat : chat.getWatchedChats()) {
            watchedChatDtos.add(TransferToDto.getWatchedChatDto(watchedChat));
        }

        ChatDto chatDto = new ChatDto(chat.getId(), chat.getSecretKey());

        return chatDto;
    }

    public static MessageDto getMessageDto(Message message) {

        System.out.println("ФОРМИРУЕМ MESSAGE DTO -> TransferToDto -> 215");
        System.out.println(message.getUser() + " USER сообщения");

        MessageDto messageDto = new MessageDto(
                message.getId(),
                message.getSendTime(),
                message.getText(),
                message.getReadTime(),
                message.getUser().getNickname()
        );
        return messageDto;
    }
}
