package com.serguni.messenger.dbms.models;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "contents")
public class Content extends ContentPK implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
//    public static class ContentPK implements Serializable {
//        protected long msgId;
//        protected long id;
//
//        public ContentPK() {
//        }
//
//        public ContentPK(long msgId, long id) {
//            this.msgId = msgId;
//            this.id = id;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            ContentPK contentPK = (ContentPK) o;
//            return msgId == contentPK.msgId && id == contentPK.id;
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(msgId, id);
//        }
//    }
//    @Id
//    @Column(name = "msg_id")
//    private long msgId;
//    @Id
//    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msg_id", updatable = false, insertable = false)
    private Message message;
//    @OneToOne(mappedBy = "content")
//    @MapsId
//    private Audio audio;
//    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
//    private List<Audio> audios;
//    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
//    private List<File> files;
//    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
//    private List<Image> images;
//    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
//    private List<Video> videos;
//    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
//    private List<Voice> voices;


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Content{" +
                "message=" + message +
                ", msgId=" + msgId +
                ", id=" + id +
                '}';
    }
}
