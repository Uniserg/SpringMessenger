package com.serguni.messenger.dbms.models;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sessions")
public class Session implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    @Id
    @SequenceGenerator(
            name = "sessions_generator",
            sequenceName = "sessions_seq",
            initialValue = 0,
            allocationSize = 1
    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "sessions_generator")
    private Long id;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sign_in_time", nullable = false)
//    @CreatedDate
    private Date signInTime;

    @Column(name = "last_online", nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
//    @CreatedDate
    private Date lastOnline;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String device;

    @Column(nullable = false)
    private String os;
    private String location;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "is_active")
    private boolean isActive;

    @Column(unique = true)
    private String cookie;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Date signInTime) {
        this.signInTime = signInTime;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return isActive == session.isActive && id.equals(session.id) && signInTime.equals(session.signInTime) && lastOnline.equals(session.lastOnline) && ip.equals(session.ip) && device.equals(session.device) && os.equals(session.os) && Objects.equals(location, session.location) && user.equals(session.user) && cookie.equals(session.cookie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, signInTime, lastOnline, ip, device, os, location, user, isActive, cookie);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", signInTime=" + signInTime +
                ", lastOnline=" + lastOnline +
                ", ip='" + ip + '\'' +
                ", device='" + device + '\'' +
                ", os='" + os + '\'' +
                ", location='" + location + '\'' +
                ", user=" + user +
                ", isActive=" + isActive +
                '}';
    }
}