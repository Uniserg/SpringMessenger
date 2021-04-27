package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
}
