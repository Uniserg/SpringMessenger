package com.serguni.messenger.dbms.repositories;

import com.serguni.messenger.dbms.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
