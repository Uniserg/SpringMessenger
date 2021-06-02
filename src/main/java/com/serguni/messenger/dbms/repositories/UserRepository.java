package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    List<User> findByNicknameStartingWith(String user);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET avatar = :avatar WHERE id = :id", nativeQuery = true)
    void updateAvatarById(@Param("id") long id, @Param("avatar") byte[] avatar);

    @Query(value = "SELECT avatar FROM users WHERE id = :id", nativeQuery = true)
    byte[] getAvatarById(@Param("id") long id);
}
