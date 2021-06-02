package com.serguni.messenger.controllers;


import com.serguni.messenger.components.sockets.Server;
import com.serguni.messenger.dto.SessionContext;
import com.serguni.messenger.dbms.models.Session;
import com.serguni.messenger.dbms.models.TemporaryKey;
import com.serguni.messenger.dbms.repositories.SessionRepository;
import com.serguni.messenger.dbms.repositories.TemporaryKeyRepository;
import com.serguni.messenger.dto.SessionCookie;
import com.serguni.messenger.utils.CryptoUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Date;

@Controller
public class ValidationKeyController {
    private final static long MAX_VALID_PERIOD = 60000;
    public static TemporaryKeyRepository temporaryKeyRepository;
    public static SessionRepository sessionRepository;

    public ValidationKeyController(TemporaryKeyRepository temporaryKeyRepository,
                                   SessionRepository sessionRepository) {
        ValidationKeyController.temporaryKeyRepository = temporaryKeyRepository;
        ValidationKeyController.sessionRepository = sessionRepository;
    }

    @PostMapping("/valid/")
    public ResponseEntity<SessionCookie> createSession(@RequestBody SessionContext sessionContext) {

        Session sessionReq = sessionContext.getSession();
        String key = sessionContext.getKey();

        TemporaryKey temporaryKey = temporaryKeyRepository.findFirstByUserIdOrderByCreateTimeDesc(sessionContext.getUserId());

        System.out.println(sessionReq + " " + key);
        if (temporaryKey != null &&
            temporaryKey.getKey().equals(key) &&
            new Date().getTime() - temporaryKey.getCreateTime().getTime() < MAX_VALID_PERIOD) {

            sessionReq.setActive(true);
            sessionReq.setSignInTime(new Date());
            sessionReq.setLastOnline(new Date());

            long nextId = sessionRepository.getNextId();
            sessionReq.setId(nextId);
            System.out.println(nextId);

            String cookie = CryptoUtil.createHash(String.valueOf(sessionReq.hashCode()));
            sessionReq.setCookie(cookie);
            sessionReq.setUser(temporaryKey.getUser());

            Session newSession = sessionRepository.save(sessionReq);
            Server.sendNewSession(newSession);
            return new ResponseEntity<>(new SessionCookie(newSession.getId(), newSession.getCookie()), HttpStatus.OK);

        } else {
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
