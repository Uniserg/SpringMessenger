package com.serguni.messenger.dbms.models.contenttype;

import com.serguni.messenger.dbms.models.MapContent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "images")
public class Image extends MapContent {
    @Column(nullable = false)
    private byte[] image;
}
