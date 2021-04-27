package com.serguni.messenger.dbms.models;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "blocked_users")
@IdClass(BlockedUser.BlockedUserPK.class)
public class BlockedUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public static class BlockedUserPK implements Serializable {
        @Serial
        private static final long serialVersionUID = 1;

        protected long userId;
        protected long blockedUserId;

        public BlockedUserPK() {
        }

        public BlockedUserPK(long userId, long blockedUserId) {
            this.userId = userId;
            this.blockedUserId = blockedUserId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockedUserPK that = (BlockedUserPK) o;
            return userId == that.userId && blockedUserId == that.blockedUserId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, blockedUserId);
        }
    }


    @Id
    @Column(name = "usr_id")
    private long userId;
    @Id
    @Column(name = "blk_usr_id")
    private long blockedUserId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blk_usr_id", nullable = false, insertable = false, updatable = false)
    private User blockedUser;
    @Column(name = "block_time", nullable = false)
    private Date blockTime;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getBlockedUserId() {
        return blockedUserId;
    }

    public void setBlockedUserId(long blockedUserId) {
        this.blockedUserId = blockedUserId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getBlockedUser() {
        return blockedUser;
    }

    public void setBlockedUser(User blockedUser) {
        this.blockedUser = blockedUser;
    }

    public Date getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Date blockTime) {
        this.blockTime = blockTime;
    }

    @Override
    public String toString() {
        return "BlockedUser{" +
                "userId=" + userId +
                ", blockedUserId=" + blockedUserId +
                ", user=" + user +
                ", blockedUser=" + blockedUser +
                ", blockTime=" + blockTime +
                '}';
    }
}
