package com.messagePack;

import com.messagePack.model.AccountInfo;
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
        MessageClient messageClient = new MessageClient(host, port);
        messageClient.start();
        //等待連線建立完成
        Thread.sleep(   20000);
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(1L);
        messageClient.send(accountInfo);

        // 添加一个钩子以便在程序结束时关闭客户端
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("main close");
            messageClient.shutdown();
        }));
    }
}