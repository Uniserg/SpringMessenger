package com.serguni.messenger.dbms.repositories.messagecontent;

import com.serguni.messenger.dbms.models.contenttype.Audio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioRepository extends JpaRepository<Audio, Long> {
}
