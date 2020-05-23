package org.xd.chain.consensus;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author rxd
 * @ClassName RecvMsg
 * Description TODO
 * @date 2019-09-27 09:30
 * @Version 1.0
 */
public final class Server extends Thread {
    private static final Logger LOGGER = Logger.getLogger(Server.class);


    @Override
    public void run() {
        LOGGER.info("正在启动服务器......");
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(Constant.PORT));

            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            LOGGER.info("服务器启动完成，等待消息中......");
            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0)
                    continue;
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer readBuffer = ByteBuffer.allocate(Constant.MSG_SIZE);
                        int num = socketChannel.read(readBuffer);
                        if (num > 0) {
                            byte[] buf = new byte[readBuffer.capacity()];
                            for (int i = 0; i < readBuffer.capacity(); i++) {
                                readBuffer.position(i);
                                buf[i] = readBuffer.get();
                            }
                            //清空缓冲区
                            readBuffer.clear();
                            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                            ObjectInputStream ois = new ObjectInputStream(bais);

                            Object msg = ois.readObject();
                            Constant.addMsg(msg);
                            bais.close();
                            ois.close();
                        } else if (num == -1)
                            socketChannel.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
