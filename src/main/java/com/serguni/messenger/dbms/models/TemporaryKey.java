package com.serguni.messenger.dbms.models;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "temporary_keys")
public class TemporaryKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    @Id
    @SequenceGenerator(
            name = "keys_generator",
            sequenceName = "keys_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "keys_generator")
    private long id;
    @Column(nullable = false)
    private String key;
    @Column(name = "create_datetime", nullable = false)
    private Date createTime;
    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", nullable = false)
    private User user;

    public TemporaryKey(String key, User user) {
        this.key = key;
        this.createTime = new Date();
        this.user = user;
    }

    public TemporaryKey() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "TemporaryKey{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", createTime=" + createTime +
                ", user=" + user +
                '}';
    }
}
