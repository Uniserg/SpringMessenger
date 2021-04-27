package com.serguni.messenger.dbms.models;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tracking")
@IdClass(Tracking.TrackingPK.class)
public class Tracking {

    public static class TrackingPK implements Serializable {
        @Serial
        private static final long serialVersionUID = 1;

        protected long trackedUserId;
        protected long trackingSessionId;

        public TrackingPK() {
        }

        public TrackingPK(long trackedUserId, long trackingSessionId) {
            this.trackedUserId = trackedUserId;
            this.trackingSessionId = trackingSessionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TrackingPK that = (TrackingPK) o;
            return trackedUserId == that.trackedUserId && trackingSessionId == that.trackingSessionId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(trackedUserId, trackingSessionId);
        }
    }

    public Tracking() {
    }

    public Tracking(long trackedUserId, long trackingUserId, long trackingSessionId) {
        this.trackedUserId = trackedUserId;
        this.trackingSessionId = trackingSessionId;
        this.trackingUserId = trackingUserId;
    }

    @Id
    @Column(name = "tracked_usr_id")
    private long trackedUserId;
    @Id
    @Column(name = "tracking_session_id")
    private long trackingSessionId;

    @Column(name = "tracking_usr_id")
    private long trackingUserId;

    public long getTrackingUserId() {
        return trackingUserId;
    }

    public void setTrackingUserId(long trackingUserId) {
        this.trackingUserId = trackingUserId;
    }

    public long getTrackedUserId() {
        return trackedUserId;
    }

    public void setTrackedUserId(long trackedUserId) {
        this.trackedUserId = trackedUserId;
    }

    public long getTrackingSessionId() {
        return trackingSessionId;
    }

    public void setTrackingSessionId(long trackingSessionId) {
        this.trackingSessionId = trackingSessionId;
    }
}
