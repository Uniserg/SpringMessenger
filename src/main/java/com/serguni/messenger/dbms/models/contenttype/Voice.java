package com.serguni.messenger.dbms.models.contenttype;

import com.serguni.messenger.dbms.models.MapContent;

import javax.persistence.*;

@Entity
@Table(name = "voices")
public class Voice extends MapContent {
    @Column(nullable = false)
    private byte[] voice;
}
