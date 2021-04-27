package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
//    Set<Configuration> findAllByInvisibleEquals(boolean isInvisible);
    Set<Configuration> findByInvisibleEqualsAndUserNicknameStartingWith(boolean isInvisible, String nick);
}
