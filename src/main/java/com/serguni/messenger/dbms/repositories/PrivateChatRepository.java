package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.Chat;
import com.serguni.messenger.dbms.models.PrivateChat;
import com.serguni.messenger.dbms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateChatRepository extends JpaRepository<PrivateChat, PrivateChat.PrivateChatPK> {
//    PrivateChat findPrivateChatByUser1IdAndUser2Id(long user1, long user2);
    PrivateChat findFirstByUser1IdAndUser2IdOrUser2IdAndUser1Id(long user1Id,
                                                                long user2Id,
                                                                long user2IdR,
                                                                long user1IdR);
}
