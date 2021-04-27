package com.serguni.messenger.dbms.models;


import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@IdClass(ContentPK.class)
public abstract class ContentPK implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Id
    @Column(name = "msg_id")
    protected long msgId;
    @Id
    @SequenceGenerator(
            name = "contents_generator",
            sequenceName = "contents_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "contents_generator")
    protected long id;


    public ContentPK() {
    }

    public ContentPK(long msgId, long id) {
        this.msgId = msgId;
        this.id = id;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentPK contentPK = (ContentPK) o;
        return msgId == contentPK.msgId && id == contentPK.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(msgId, id);
    }

    @Override
    public String toString() {
        return "ContentPK{" +
                "msgId=" + msgId +
                ", id=" + id +
                '}';
    }
}
