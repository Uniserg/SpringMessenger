//package com.serguni.messenger.dbms.models;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "user_secret_keys")
//public class UserSecretKey {
//    @Id
//    @Column(name = "usr_id")
//    private long userId;
//    @Column(nullable = false, length = 120)
//    private String secretKey;
//
//    @OneToOne
//    @MapsId
//    @JoinColumn(name = "usr_id")
//    @JsonIgnore
//    private User user;
//
//    public UserSecretKey() {
//    }
//
//    public long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(long userId) {
//        this.userId = userId;
//    }
//
//    public String getSecretKey() {
//        return secretKey;
//    }
//
//    public void setSecretKey(String secretKey) {
//        this.secretKey = secretKey;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//}
