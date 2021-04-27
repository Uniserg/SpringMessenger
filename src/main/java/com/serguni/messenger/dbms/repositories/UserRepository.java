package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    List<User> findByNicknameStartingWith(String user);
}
