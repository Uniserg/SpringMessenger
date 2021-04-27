package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

public interface TrackingRepository extends JpaRepository<Tracking, Tracking.TrackingPK> {
    @Query(value = "SELECT tracked_usr_id FROM tracking WHERE tracking_session_id = :tracking_session_id", nativeQuery = true)
    Set<Long> getTrackedUserIdsByTrackingSessionId(@Param("tracking_session_id") long trackingSessionId);

    @Query(value = "SELECT tracking_usr_id, tracking_session_id FROM tracking WHERE tracked_usr_id = :tracked_usr_id", nativeQuery = true)
    Set<long[]> getTrackingSessionsIdsByTrackedUserId(@Param("tracked_usr_id") long trackedUserId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM tracking WHERE tracking_session_id = :tracking_session_id", nativeQuery = true)
    void deleteAllTrackedUsersByTrackingSessionsId(@Param("tracking_session_id") long trackingSessionId);
}
