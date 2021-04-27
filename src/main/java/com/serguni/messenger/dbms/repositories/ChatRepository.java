package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
