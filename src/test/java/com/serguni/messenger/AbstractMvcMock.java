package com.serguni.messenger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SpringMessengerApplication.class
)
@AutoConfigureMockMvc
public abstract class AbstractMvcMock {
    @Autowired
    protected MockMvc mvc;
}
