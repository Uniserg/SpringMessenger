//package com.serguni.springmessenger.components.sockets;
//
//import com.serguni.springmessenger.dbms.models.Session;
//
//import java.io.IOException;
//import java.net.Socket;
//import java.net.UnknownHostException;
//
//public class Client extends Socket {
//    private Session session;
//
//    public Client(String host, int port, Session session) throws IOException {
//        super(host, port);
//        this.session = session;
//    }
//
//    public void setSession(Session session) {
//        this.session = session;
//    }
//
//    public Session getSession() {
//        return session;
//    }
//}
