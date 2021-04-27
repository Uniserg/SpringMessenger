package com.serguni.messenger.dto;

import com.serguni.messenger.dbms.models.Session;

public class SessionContext {
    long userId;
    String key;
    Session session;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
