package com.serguni.messenger.dbms.models;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "friend_requests")
@IdClass(FriendRequest.FriendRequestFK.class)
public class FriendRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public static class FriendRequestFK implements Serializable {
        @Serial
        private static final long serialVersionUID = 1;

        protected long fromUserId;
        protected long toUserId;

        public FriendRequestFK() {
        }

        public FriendRequestFK(long fromUserId, long toUserId) {
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FriendRequestFK that = (FriendRequestFK) o;
            return fromUserId == that.fromUserId && toUserId == that.toUserId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fromUserId, toUserId);
        }
    }

    @Id
    @Column(name = "from_usr_id")
    private long fromUserId;
    @Id
    @Column(name = "to_usr_id")
    private long toUserId;
    @Column(name = "send_time", nullable = false)
    private Date sendTime;
    @Column(name = "accept_time")
    private Date acceptTime;
    @ManyToOne
    @JoinColumn(name = "from_usr_id", updatable = false, insertable = false)
    private User fromUser;
    @ManyToOne
    @JoinColumn(name = "to_usr_id", updatable = false, insertable = false)
    private User toUser;

    public long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", sendTime=" + sendTime +
                ", acceptTime=" + acceptTime +
                ", fromUser=" + fromUser +
                ", toUser=" + toUser +
                '}';
    }
}
