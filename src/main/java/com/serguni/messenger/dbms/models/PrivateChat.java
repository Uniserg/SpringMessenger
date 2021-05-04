package com.serguni.messenger.dbms.models;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "private_chats")
public class PrivateChat implements Serializable {

    @Embeddable
    public static class PrivateChatPK implements Serializable {
        @Serial
        private static final long serialVersionUID = 1;
        @Column(name = "chat_id")
        protected long chatId;
        @Column(name = "user1_id")
        protected long user1Id;
        @Column(name = "user2_id")
        protected long user2Id;

        public PrivateChatPK() {
        }

        public PrivateChatPK(long chatId, long user1Id, long user2Id) {
            this.chatId = chatId;
            this.user1Id = user1Id;
            this.user2Id = user2Id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrivateChatPK that = (PrivateChatPK) o;
            return chatId == that.chatId && user1Id == that.user1Id && user2Id == that.user2Id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(chatId, user1Id, user2Id);
        }
    }

    public PrivateChat(PrivateChatPK privateChatPK) {
        this.privateChatPK = privateChatPK;
    }

    @EmbeddedId
    private PrivateChatPK privateChatPK;

    @ManyToOne
    @JoinColumn(name = "user1_id", insertable = false, updatable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", insertable = false, updatable = false)
    private User user2;

    @OneToOne
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    private Chat chat;

    public PrivateChat(User user1, User user2, Chat chat) {
        this.user1 = user1;
        this.user2 = user2;
        this.chat = chat;
    }

    public PrivateChat() {
    }

    public PrivateChatPK getPrivateChatPK() {
        return privateChatPK;
    }

    public void setPrivateChatPK(PrivateChatPK privateChatPK) {
        this.privateChatPK = privateChatPK;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
