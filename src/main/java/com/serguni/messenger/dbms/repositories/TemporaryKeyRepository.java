package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.TemporaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemporaryKeyRepository extends JpaRepository<TemporaryKey, Long> {
//    TemporaryKey findFirstByUserOrderByCreateTimeDesc(User user);
    TemporaryKey findFirstByUserIdOrderByCreateTimeDesc(long userId);
    List<TemporaryKey> findByUserId(long userId);
}
