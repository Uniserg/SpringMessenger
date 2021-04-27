package com.serguni.messenger.dbms.repositories.messagecontent;

import com.serguni.messenger.dbms.models.contenttype.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
