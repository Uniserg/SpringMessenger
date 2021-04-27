package com.serguni.messenger.dbms.repositories.messagecontent;

import com.serguni.messenger.dbms.models.contenttype.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
