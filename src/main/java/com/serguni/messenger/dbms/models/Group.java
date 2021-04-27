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
}
