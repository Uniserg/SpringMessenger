package com.serguni.messenger.dbms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "chats")
public class Chat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    @Id
    @SequenceGenerator(
            name = "chats_generator",
            sequenceName = "chats_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "chats_generator")
    private long id;

    @Column(nullable = false, unique = true, length = 120)
    @JsonIgnore
    private String secretKey;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    private Set<Message> messages;

    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<WatchedChat> watchedChats;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "chat")
    private PrivateChat privateChat;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "chat")
    private Group groupChat;

    public Chat(long id, String secretKey) {
        this.id = id;
        this.secretKey = secretKey;
    }

    public Chat() {
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", secretKey='" + secretKey + '\'' +
                ", messages=" + messages +
                ", watchedChats=" + watchedChats +
                '}';
    }

    public Group getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(Group groupChat) {
        this.groupChat = groupChat;
    }

    public PrivateChat getPrivateChat() {
        return privateChat;
    }

    public void setPrivateChat(PrivateChat privateChat) {
        this.privateChat = privateChat;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Set<WatchedChat> getWatchedChats() {
        return watchedChats;
    }

    public void setWatchedChats(Set<WatchedChat> watchedChats) {
        this.watchedChats = watchedChats;
    }
}
