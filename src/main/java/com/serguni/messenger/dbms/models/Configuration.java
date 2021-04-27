package com.serguni.messenger.dbms.models;

import com.serguni.messenger.dto.models.ConfigurationDto;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "configurations")
public class Configuration implements Serializable {
    @Id
    @Column(name = "usr_id")
    private long userId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "usr_id")
    private User user;
    @Column(name = "msg_from_friends_only", nullable = false, columnDefinition = "boolean default false")
    private boolean msgFromFriendsOnly;
    @Column(name = "show_last_online", nullable = false, columnDefinition = "boolean default true")
    private boolean showLastOnline;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean invisible;

    public Configuration(User user) {
        this.user = user;
    }

    public Configuration() {
    }

    public Configuration(ConfigurationDto configurationDto) {
        userId = configurationDto.getUserId();
        msgFromFriendsOnly = configurationDto.isMsgFromFriendsOnly();
        showLastOnline = configurationDto.isShowLastOnline();
        invisible = configurationDto.isInvisible();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isMsgFromFriendsOnly() {
        return msgFromFriendsOnly;
    }

    public void setMsgFromFriendsOnly(boolean msgFromFriendsOnly) {
        this.msgFromFriendsOnly = msgFromFriendsOnly;
    }

    public boolean isShowLastOnline() {
        return showLastOnline;
    }

    public void setShowLastOnline(boolean showLastOnline) {
        this.showLastOnline = showLastOnline;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "userId=" + userId +
                ", user=" + user +
                ", msgFromFriendsOnly=" + msgFromFriendsOnly +
                ", showLastOnline=" + showLastOnline +
                ", invisible=" + invisible +
                '}';
    }
}
