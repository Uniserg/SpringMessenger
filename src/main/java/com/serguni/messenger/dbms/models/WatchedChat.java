package com.serguni.messenger.dbms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
//@IdClass(WatchedChat.WatchedChatPK.class)
@Table(name = "watched_chats")
public class WatchedChat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    @Embeddable
    public static class WatchedChatPK implements Serializable {
        @Serial
        private static final long serialVersionUID = 1;

        @Column(name = "chat_id")
        protected long chatId;
        @Column(name = "usr_id")
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
//
//    @Id
//    @Column(name = "chat_id")
//    private long chatId;
//
//    @Id
//    @Column(name = "usr_id")
//    private long userId;


    public WatchedChat() {
    }

    public WatchedChat(WatchedChatPK watchedChatPK, String name, Date syncTime, boolean isAdmin, boolean isBlocked) {
        this.watchedChatPK = watchedChatPK;
        this.name = name;
        this.syncTime = syncTime;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
    }

    @EmbeddedId
    private WatchedChatPK watchedChatPK;

    @Column(nullable = false)
    private String name;

    @Column(name = "sync_time", nullable = false)
    private Date syncTime;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "is_blk", nullable = false)
    private boolean isBlocked;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "watched_chats_messages",
            joinColumns = {@JoinColumn(name = "chat_id"),
                    @JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private Set<Message> watchedMessages;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    private Chat chat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", insertable = false, updatable = false)
    private User user;

    public WatchedChat(String name, Date syncTime, boolean isAdmin, boolean isBlocked, Chat chat) {
        this.name = name;
        this.syncTime = syncTime;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        this.chat = chat;
    }

    //    public long getChatId() {
//        return chatId;
//    }
//
//    public void setChatId(long chatId) {
//        this.chatId = chatId;
//    }
//
//    public long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(long userId) {
//        this.userId = userId;
//    }


    public WatchedChatPK getWatchedChatPK() {
        return watchedChatPK;
    }

    public void setWatchedChatPK(WatchedChatPK watchedChatPK) {
        this.watchedChatPK = watchedChatPK;
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

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Message> getWatchedMessages() {
        return watchedMessages;
    }

    public void setWatchedMessages(Set<Message> watchedMessages) {
        this.watchedMessages = watchedMessages;
    }


    @Override
    public String toString() {
        return "WatchedChat{" +
                "name='" + name + '\'' +
                ", syncTime=" + syncTime +
                ", isAdmin=" + isAdmin +
                ", isBlocked=" + isBlocked +
                ", watchedMessages=" + watchedMessages +
                '}';
    }
}
