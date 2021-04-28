package com.serguni.messenger.dto;

import com.serguni.messenger.dbms.models.*;
import com.serguni.messenger.dto.models.*;

import java.util.HashSet;
import java.util.Set;

public class TransferToDto {

    public static UserInfoDto UserInfoDto(User user) {
        UserInfoDto userInfoDto = new UserInfoDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAboutMe(),
                user.getAvatar());

        return userInfoDto;
    }

    public static UserDto getUserDto(User user) {
        UserDto userDto = new UserDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAboutMe(),
                user.getAvatar());

        Set<SessionDto> sessions = null;
        Set<UserInfoDto> friends = null;
        Set<WatchedChatDto> watchedChatDtos = null;
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
                friends.add(TransferToDto.UserInfoDto(friend));
            }
        }

        if (user.getWatchedChats() != null) {
            watchedChatDtos = new HashSet<>();
            for (WatchedChat watchedChat : user.getWatchedChats()) {
                watchedChatDtos.add(TransferToDto.getWatchedChatDto(watchedChat));
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
        userDto.setWatchedChats(watchedChatDtos);
        userDto.setIncomingFriendRequests(incomingFriendsRequests);
        userDto.setOutgoingFriendRequests(outgoingFriendsRequests);

        return userDto;
    }

    public static BlockedUserDto getBlockedUserDto(BlockedUser blockedUser) {
        BlockedUserDto blockedUserDto = new BlockedUserDto(
                blockedUser.getUserId(),
                blockedUser.getBlockedUserId(),
                TransferToDto.UserInfoDto(blockedUser.getBlockedUser()),
                blockedUser.getBlockTime());

        return blockedUserDto;
    }

    public static FriendRequestDto getFriendRequestDto(FriendRequest friendRequest) {
        FriendRequestDto friendRequestDto = new FriendRequestDto(
                friendRequest.getFromUserId(),
                friendRequest.getToUserId(),
                friendRequest.getSendTime(),
                friendRequest.getAcceptTime(),
                TransferToDto.UserInfoDto(friendRequest.getFromUser()),
                TransferToDto.UserInfoDto(friendRequest.getToUser())
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
        WatchedChatDto watchedChatDto = new WatchedChatDto(
                watchedChat.getChatId(),
                watchedChat.getUserId(),
                watchedChat.getName(),
                watchedChat.getSyncTime(),
                watchedChat.isAdmin(),
                watchedChat.isBlocked(),
                TransferToDto.getChatDto(watchedChat.getChat()));

        return watchedChatDto;
    }

    public static ChatDto getChatDto(Chat chat) {
        Set<MessageDto> messageDtos = new HashSet<>();
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
        MessageDto messageDto = new MessageDto(
                message.getId(),
                message.getSendTime(),
                message.getText(),
                message.getReadTime(),
                TransferToDto.UserInfoDto(message.getUser()));
        return messageDto;
    }
}
