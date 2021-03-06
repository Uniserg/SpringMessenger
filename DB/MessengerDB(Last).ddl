-- Generated by Oracle SQL Developer Data Modeler 20.4.1.406.0906
--   at:        2021-04-03 00:15:10 MSK
--   site:      Oracle Database 11g
--   type:      Oracle Database 11g



-- predefined type, no DDL - MDSYS.SDO_GEOMETRY

-- predefined type, no DDL - XMLTYPE

CREATE TABLE audios (
    msg_id  INTEGER NOT NULL,
    id      INTEGER NOT NULL,
    audio   BLOB NOT NULL
);

ALTER TABLE audios ADD CONSTRAINT audios_pk PRIMARY KEY ( msg_id,
                                                          id );

CREATE TABLE blocked_users (
    usr_id      INTEGER NOT NULL,
    blk_usr_id  INTEGER NOT NULL,
    block_time  TIMESTAMP NOT NULL
);

ALTER TABLE blocked_users ADD CONSTRAINT blk_usr_pk PRIMARY KEY ( blk_usr_id,
                                                                  usr_id );

CREATE TABLE chats (
    id INTEGER NOT NULL
);

ALTER TABLE chats ADD CONSTRAINT chats_pk PRIMARY KEY ( id );

CREATE TABLE configurations (
    usr_id                 INTEGER NOT NULL,
    msg_from_friends_only  CHAR(1) NOT NULL,
    show_last_online       CHAR(1) NOT NULL,
    unvisible              CHAR(1) NOT NULL
);

ALTER TABLE configurations ADD CONSTRAINT cfg_pk PRIMARY KEY ( usr_id );

CREATE TABLE contents (
    msg_id  INTEGER NOT NULL,
    id      INTEGER NOT NULL
);

ALTER TABLE contents ADD CONSTRAINT contents_pk PRIMARY KEY ( msg_id,
                                                              id );

CREATE TABLE files (
    msg_id  INTEGER NOT NULL,
    id      INTEGER NOT NULL,
    "File"  BFILE NOT NULL
);

ALTER TABLE files ADD CONSTRAINT files_pk PRIMARY KEY ( msg_id,
                                                        id );

CREATE TABLE friends (
    usr_id         INTEGER NOT NULL,
    friend_usr_id  INTEGER NOT NULL
);

ALTER TABLE friends ADD CONSTRAINT friends_pk PRIMARY KEY ( usr_id,
                                                            friend_usr_id );

CREATE TABLE groups (
    id        INTEGER NOT NULL,
    name      VARCHAR2(100) NOT NULL,
    password  VARCHAR2(120),
    icon      BLOB
);

ALTER TABLE groups ADD CONSTRAINT groups_pk PRIMARY KEY ( id );

CREATE TABLE images (
    msg_id  INTEGER NOT NULL,
    id      INTEGER NOT NULL,
    img     BLOB NOT NULL
);

ALTER TABLE images ADD CONSTRAINT img_pk PRIMARY KEY ( msg_id,
                                                       id );

CREATE TABLE messages (
    id         INTEGER NOT NULL,
    send_time  TIMESTAMP NOT NULL,
    text       CLOB,
    read_time  TIMESTAMP,
    chat_id    INTEGER NOT NULL,
    usr_id     INTEGER NOT NULL
);

ALTER TABLE messages ADD CONSTRAINT msg_pk PRIMARY KEY ( id );

CREATE TABLE redirected_messages (
    msg_id      INTEGER NOT NULL,
    rdt_msg_id  INTEGER NOT NULL
);

ALTER TABLE redirected_messages ADD CONSTRAINT rdt_msg_pk PRIMARY KEY ( msg_id,
                                                                        rdt_msg_id );

CREATE TABLE sessions (
    id            INTEGER NOT NULL,
    sign_in_time  TIMESTAMP NOT NULL,
    last_online   TIMESTAMP NOT NULL,
    device        VARCHAR2(100) NOT NULL,
    os            VARCHAR2(50) NOT NULL,
    location      VARCHAR2(150),
    usr_id        INTEGER NOT NULL
);

ALTER TABLE sessions ADD CONSTRAINT sessions_pk PRIMARY KEY ( id );

CREATE TABLE temporary_keys (
    id               INTEGER NOT NULL,
    key              VARCHAR2(5) NOT NULL,
    create_datetime  TIMESTAMP NOT NULL,
    validity_period  TIMESTAMP NOT NULL,
    usr_id           INTEGER NOT NULL
);

ALTER TABLE temporary_keys ADD CONSTRAINT temporary_keys_pk PRIMARY KEY ( id );

CREATE TABLE users (
    id          INTEGER NOT NULL,
    nickname    VARCHAR2(100) NOT NULL,
    email       VARCHAR2(80) NOT NULL,
    first_name  VARCHAR2(80),
    last_name   VARCHAR2(80),
    about_me    VARCHAR2(350),
    avatar      BLOB
);

ALTER TABLE users ADD CONSTRAINT usr_pk PRIMARY KEY ( id );

ALTER TABLE users ADD CONSTRAINT usr_nickname_un UNIQUE ( nickname );

ALTER TABLE users ADD CONSTRAINT usr_email_un UNIQUE ( email );

CREATE TABLE videos (
    msg_id  INTEGER NOT NULL,
    id      INTEGER NOT NULL,
    video   BLOB NOT NULL
);

ALTER TABLE videos ADD CONSTRAINT videos_pk PRIMARY KEY ( msg_id,
                                                          id );

CREATE TABLE voices (
    msg_id  INTEGER NOT NULL,
    id      INTEGER NOT NULL,
    voice   BLOB NOT NULL
);

ALTER TABLE voices ADD CONSTRAINT voices_pk PRIMARY KEY ( msg_id,
                                                          id );

CREATE TABLE watched_chats (
    chat_id    INTEGER NOT NULL,
    usr_id     INTEGER NOT NULL,
    name       VARCHAR2(100) NOT NULL,
    sync_time  TIMESTAMP NOT NULL,
    is_admin   CHAR(1) NOT NULL,
    is_blk     CHAR(1) NOT NULL
);

ALTER TABLE watched_chats ADD CONSTRAINT wtch_chats_pk PRIMARY KEY ( usr_id,
                                                                     chat_id );

ALTER TABLE audios
    ADD CONSTRAINT audios_contents_fk FOREIGN KEY ( msg_id,
                                                    id )
        REFERENCES contents ( msg_id,
                              id );

ALTER TABLE blocked_users
    ADD CONSTRAINT blk_usr_b_usr_fk FOREIGN KEY ( blk_usr_id )
        REFERENCES users ( id );

ALTER TABLE blocked_users
    ADD CONSTRAINT blk_usr_usr_fk FOREIGN KEY ( usr_id )
        REFERENCES users ( id )
            ON DELETE CASCADE;

ALTER TABLE configurations
    ADD CONSTRAINT cfg_usr_fk FOREIGN KEY ( usr_id )
        REFERENCES users ( id );

ALTER TABLE contents
    ADD CONSTRAINT contents_msg_fk FOREIGN KEY ( msg_id )
        REFERENCES messages ( id );

ALTER TABLE files
    ADD CONSTRAINT files_contents_fk FOREIGN KEY ( msg_id,
                                                   id )
        REFERENCES contents ( msg_id,
                              id );

ALTER TABLE friends
    ADD CONSTRAINT friends_f_usr_fk FOREIGN KEY ( friend_usr_id )
        REFERENCES users ( id )
            ON DELETE CASCADE;

ALTER TABLE friends
    ADD CONSTRAINT friends_usr_fk FOREIGN KEY ( usr_id )
        REFERENCES users ( id )
            ON DELETE CASCADE;

ALTER TABLE groups
    ADD CONSTRAINT groups_chats_fk FOREIGN KEY ( id )
        REFERENCES chats ( id );

ALTER TABLE images
    ADD CONSTRAINT img_contents_fk FOREIGN KEY ( msg_id,
                                                 id )
        REFERENCES contents ( msg_id,
                              id );

ALTER TABLE messages
    ADD CONSTRAINT msg_chats_fk FOREIGN KEY ( chat_id )
        REFERENCES chats ( id )
            ON DELETE CASCADE;

ALTER TABLE messages
    ADD CONSTRAINT msg_usr_fk FOREIGN KEY ( usr_id )
        REFERENCES users ( id );

ALTER TABLE redirected_messages
    ADD CONSTRAINT rdt_msg_msg_fk FOREIGN KEY ( msg_id )
        REFERENCES messages ( id )
            ON DELETE CASCADE;

ALTER TABLE redirected_messages
    ADD CONSTRAINT rdt_msg_r_msg_fk FOREIGN KEY ( rdt_msg_id )
        REFERENCES messages ( id )
            ON DELETE CASCADE;

ALTER TABLE sessions
    ADD CONSTRAINT sessions_usr_fk FOREIGN KEY ( usr_id )
        REFERENCES users ( id )
            ON DELETE CASCADE;

ALTER TABLE temporary_keys
    ADD CONSTRAINT temporary_keys_usr_fk FOREIGN KEY ( usr_id )
        REFERENCES users ( id )
            ON DELETE CASCADE;

ALTER TABLE videos
    ADD CONSTRAINT videos_contents_fk FOREIGN KEY ( msg_id,
                                                    id )
        REFERENCES contents ( msg_id,
                              id );

ALTER TABLE voices
    ADD CONSTRAINT voices_contents_fk FOREIGN KEY ( msg_id,
                                                    id )
        REFERENCES contents ( msg_id,
                              id );

ALTER TABLE watched_chats
    ADD CONSTRAINT wtch_chats_chats_fk FOREIGN KEY ( chat_id )
        REFERENCES chats ( id )
            ON DELETE CASCADE;

ALTER TABLE watched_chats
    ADD CONSTRAINT wtch_chats_usr_fk FOREIGN KEY ( usr_id )
        REFERENCES users ( id )
            ON DELETE CASCADE;

CREATE OR REPLACE TRIGGER arc_fkarc_1_voices BEFORE
    INSERT OR UPDATE OF msg_id, id ON voices
    FOR EACH ROW
DECLARE
    d INTEGER;
BEGIN
    SELECT
        a.id
    INTO d
    FROM
        contents a
    WHERE
        a.msg_id = :new.msg_id
        AND a.id = :new.id;

    IF ( d IS NULL OR d <> 5 ) THEN
        raise_application_error(
                               -20223,
                               'FK Voices_Contents_FK in Table Voices violates Arc constraint on Table Contents - discriminator column Id doesn''t have value 5'
        );
    END IF;

EXCEPTION
    WHEN no_data_found THEN
        NULL;
    WHEN OTHERS THEN
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER arc_fkarc_1_images BEFORE
    INSERT OR UPDATE OF msg_id, id ON images
    FOR EACH ROW
DECLARE
    d INTEGER;
BEGIN
    SELECT
        a.id
    INTO d
    FROM
        contents a
    WHERE
        a.msg_id = :new.msg_id
        AND a.id = :new.id;

    IF ( d IS NULL OR d <> 3 ) THEN
        raise_application_error(
                               -20223,
                               'FK IMG_Contents_FK in Table Images violates Arc constraint on Table Contents - discriminator column Id doesn''t have value 3'
        );
    END IF;

EXCEPTION
    WHEN no_data_found THEN
        NULL;
    WHEN OTHERS THEN
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER arc_fkarc_1_files BEFORE
    INSERT OR UPDATE OF msg_id, id ON files
    FOR EACH ROW
DECLARE
    d INTEGER;
BEGIN
    SELECT
        a.id
    INTO d
    FROM
        contents a
    WHERE
        a.msg_id = :new.msg_id
        AND a.id = :new.id;

    IF ( d IS NULL OR d <> 2 ) THEN
        raise_application_error(
                               -20223,
                               'FK Files_Contents_FK in Table Files violates Arc constraint on Table Contents - discriminator column Id doesn''t have value 2'
        );
    END IF;

EXCEPTION
    WHEN no_data_found THEN
        NULL;
    WHEN OTHERS THEN
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER arc_fkarc_1_audios BEFORE
    INSERT OR UPDATE OF msg_id, id ON audios
    FOR EACH ROW
DECLARE
    d INTEGER;
BEGIN
    SELECT
        a.id
    INTO d
    FROM
        contents a
    WHERE
        a.msg_id = :new.msg_id
        AND a.id = :new.id;

    IF ( d IS NULL OR d <> 1 ) THEN
        raise_application_error(
                               -20223,
                               'FK Audios_Contents_FK in Table Audios violates Arc constraint on Table Contents - discriminator column Id doesn''t have value 1'
        );
    END IF;

EXCEPTION
    WHEN no_data_found THEN
        NULL;
    WHEN OTHERS THEN
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER arc_fkarc_1_videos BEFORE
    INSERT OR UPDATE OF msg_id, id ON videos
    FOR EACH ROW
DECLARE
    d INTEGER;
BEGIN
    SELECT
        a.id
    INTO d
    FROM
        contents a
    WHERE
        a.msg_id = :new.msg_id
        AND a.id = :new.id;

    IF ( d IS NULL OR d <> 4 ) THEN
        raise_application_error(
                               -20223,
                               'FK Videos_Contents_FK in Table Videos violates Arc constraint on Table Contents - discriminator column Id doesn''t have value 4'
        );
    END IF;

EXCEPTION
    WHEN no_data_found THEN
        NULL;
    WHEN OTHERS THEN
        RAISE;
END;
/



-- Oracle SQL Developer Data Modeler Summary Report: 
-- 
-- CREATE TABLE                            17
-- CREATE INDEX                             0
-- ALTER TABLE                             39
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           5
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- CREATE MATERIALIZED VIEW LOG             0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
