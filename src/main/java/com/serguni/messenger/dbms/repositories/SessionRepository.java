package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findAllByUserId(long userId);

    int deleteByIdAndUserId(long id, long userId);

    @Query(value = "SELECT nextval('sessions_seq')", nativeQuery = true)
    long getNextId();

    @Query(value = "SELECT MAX(last_online) FROM sessions WHERE usr_id = :usr_id", nativeQuery = true)
    Date getLastOnlineOfUser(@Param("usr_id") long userId);

    @Transactional
    @Modifying
    void deleteAllByUserId(long userId);
}
