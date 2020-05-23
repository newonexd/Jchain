package org.xd.chain.consensus;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author rxd
 * @ClassName SendMsg
 * Description TODO
 * @date 2019-09-27 09:30
 * @Version 1.0
 */
public final class Client {
    private static ByteArrayOutputStream byteArrayOutputStream = null;
    private static ObjectOutputStream objectOutputStream = null;

    public static void send_msg(String location, Object obj) throws IOException {
        SocketChannel socketChannel =SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(location, Constant.PORT));
        if (socketChannel.finishConnect()) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            socketChannel.write(byteBuffer);
        }
        byteArrayOutputStream.close();
        objectOutputStream.close();
        socketChannel.close();

    }


}
