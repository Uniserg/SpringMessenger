package com.serguni.messenger.controllers;

import com.google.gson.Gson;
import com.serguni.messenger.AbstractMvcMock;
import com.serguni.messenger.components.sockets.Server;
import com.serguni.messenger.dbms.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractMvcMock {

    @Test
    void signUp() throws Exception {
        User user = new User();
        user.setNickname("test");
        user.setEmail("test@mail.ru");

        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .content(new Gson().toJson(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    void logIn() throws Exception {
        User user = Server.userRepository.findByNickname("test");

        mvc.perform(MockMvcRequestBuilders
        .get("/users/" + user.getNickname())
        ).andExpect(status().isOk());
    }
}