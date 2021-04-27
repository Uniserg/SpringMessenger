package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.WatchedChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchedChatRepository extends JpaRepository<WatchedChat, Long> {
}
