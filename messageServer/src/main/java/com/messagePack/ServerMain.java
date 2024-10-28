package com.messagePack;

import com.messagePack.socket.MessageServer;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        int port = 7799;
        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (Exception e) {
            System.out.println("args[0] is not num");
        }
        new MessageServer(port).run();
    }
}