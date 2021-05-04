package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(value = "SELECT nextval('chats_seq')", nativeQuery = true)
    long getNextId();
}
