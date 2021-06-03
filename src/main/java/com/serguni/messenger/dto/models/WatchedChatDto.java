package com.serguni.messenger.dto.models;

import com.serguni.messenger.dbms.models.Message;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.SortedSet;

public class WatchedChatDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public static class WatchedChatPK implements Serializable {
        protected long chatId;
        protected long userId;

        public WatchedChatPK() {
        }

        public WatchedChatPK(long chatId, long userId) {
            this.chatId = chatId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WatchedChatPK that = (WatchedChatPK) o;
            return chatId == that.chatId && userId == that.userId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(chatId, userId);
        }
    }

    private long chatId;
    private long userId;
    private String name;
    private Date syncTime;
    private boolean isAdmin;
    private boolean isBlocked;
    private SortedSet<MessageDto> messages;
    private SortedSet<UserInfoDto> users;

    public WatchedChatDto(long chatId,
                          long userId,
                          String name,
                          Date syncTime,
                          boolean isAdmin,
                          boolean isBlocked,
                          SortedSet<MessageDto> watchedMessages,
                          SortedSet<UserInfoDto> users) {
        this.chatId = chatId;
        this.userId = userId;
        this.name = name;
        this.syncTime = syncTime;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        this.messages = watchedMessages;
        this.users = users;
    }

    //    public WatchedChatDto(WatchedChat watchedChat) {
//        chatId = watchedChat.getChatId();
//        userId = watchedChat.getUserId();
//        name = watchedChat.getName();
//        syncTime = watchedChat.getSyncTime();
//        isAdmin = watchedChat.isAdmin();
//        isBlocked = watchedChat.isBlocked();
//        chat = new ChatDto(watchedChat.getChat());
//    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public String toString() {
        return "WatchedChatDto{" +
                "chatId=" + chatId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", syncTime=" + syncTime +
                ", isAdmin=" + isAdmin +
                ", isBlocked=" + isBlocked +
                ", messages=" + messages +
                ", users=" + users +
                '}';
    }
}
