package org.xd.chain.network;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkHandler {
    private static ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<>();

    static void addMsg(Object obj) {
        queue.add(obj);
    }

    static Object pollMsg() {
        return queue.poll();
    }

    public static void handler(){
        Server.initServer();
    }
}