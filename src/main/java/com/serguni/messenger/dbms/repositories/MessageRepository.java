package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
