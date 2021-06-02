package com.serguni.messenger.controllers;

import com.google.gson.Gson;
import com.serguni.messenger.AbstractMvcMock;
import com.serguni.messenger.components.sockets.Server;
import com.serguni.messenger.dbms.models.Session;
import com.serguni.messenger.dbms.models.TemporaryKey;
import com.serguni.messenger.dbms.models.User;
import com.serguni.messenger.dto.SessionContext;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.InetAddress;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ValidationKeyControllerTest extends AbstractMvcMock {

    @Test
    void createSession() throws Exception {
        User user = Server.userRepository.findById(1L).orElse(null);
        assertNotNull(user);

        TemporaryKey temporaryKey = ValidationKeyController.temporaryKeyRepository
                .findFirstByUserIdOrderByCreateTimeDesc(user.getId());

        String[] inetAddress = InetAddress.getLocalHost().toString().split("/");
        String device = inetAddress[0];
        String ip = inetAddress[1];

        Session session = new Session();
        session.setOs(System.getProperty("os.name"));
        session.setLocation(null);
        session.setIp(ip);
        session.setDevice(device);

        SessionContext sessionContext = new SessionContext();
        sessionContext.setUserId(user.getId());
        sessionContext.setSession(session);
        sessionContext.setKey(temporaryKey.getKey());

        mvc.perform(MockMvcRequestBuilders
                .post("/valid/")
                .content(new Gson().toJson(sessionContext))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)

        ).andExpect(status().isOk());
    }
}