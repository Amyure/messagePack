package com.messagePack;

import com.messagePack.socket.MessageClient;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 7799;

//        try {
//            host = args[0];
//            port = Integer.parseInt(args[1]);
//        } catch (Exception e) {
//            System.out.printf("parse parameters error:%s%n, %s%n", e.getMessage(), e);
//        }
        new MessageClient(host, port).run();
    }
}