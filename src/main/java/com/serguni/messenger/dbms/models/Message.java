package com.serguni.messenger.dbms.models;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "messages")
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    @Id
    @SequenceGenerator(
            name = "messages_generator",
            sequenceName = "messages_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "messages_generator")
    private long id;
    @Column(name = "send_time", nullable = false)
    private Date sendTime;
    private String text;
    @Column(name = "read_time")
    private Date readTime;
    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;
    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", nullable = false)
    private User user;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "redirected_messages",
            joinColumns = @JoinColumn(name = "msg_id"),
            inverseJoinColumns = @JoinColumn(name = "rdt_msg_id")
    )
    private Set<Message> redirectedMessages;
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private Set<Content> contents;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
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

    public Set<Message> getRedirectedMessages() {
        return redirectedMessages;
    }

    public void setRedirectedMessages(Set<Message> messages) {
        this.redirectedMessages = messages;
    }

    public Set<Content> getContents() {
        return contents;
    }

    public void setContents(Set<Content> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sendTime=" + sendTime +
                ", text='" + text + '\'' +
                ", readTime=" + readTime +
                ", chat=" + chat +
                ", user=" + user +
                ", messages=" + redirectedMessages +
                ", contents=" + contents +
                '}';
    }
}
