package com.serguni.messenger.dbms.repositories.messagecontent;

import com.serguni.messenger.dbms.models.contenttype.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
