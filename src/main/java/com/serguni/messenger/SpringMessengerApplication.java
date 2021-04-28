package com.serguni.messenger;

import com.serguni.messenger.components.sockets.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringMessengerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMessengerApplication.class, args);
        Server.trackingRepository.deleteAll();
    }
}
