package com.serguni.messenger.dbms.models;

import javax.persistence.*;

@MappedSuperclass
public abstract class MapContent extends ContentPK {
    @MapsId
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "msg_id", referencedColumnName = "msg_id"),
            @JoinColumn(name = "id", referencedColumnName = "id")
    })
    private Content content;
}
