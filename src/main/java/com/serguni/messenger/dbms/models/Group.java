package com.serguni.messenger.dbms.models;

import javax.persistence.*;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    private long id;
    @Column(nullable = false)
    private String name;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Chat chat;
    private String password;
    private byte[] icon;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
}
