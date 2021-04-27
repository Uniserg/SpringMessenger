package com.serguni.messenger.dbms.repositories.messagecontent;

import com.serguni.messenger.dbms.models.contenttype.Voice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceRepository extends JpaRepository<Voice, Long> {
}
