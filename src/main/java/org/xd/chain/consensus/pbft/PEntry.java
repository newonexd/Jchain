package org.xd.chain.consensus.pbft;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;

import org.xd.chain.consensus.*;

/**
 * @author rxd
 * @ClassName PEntry
 * Description TODO
 * @date 2019-10-05 15:37
 * @Version 1.0
 */
public class PEntry {
    private static final Logger LOGGER = Logger.getLogger(PEntry.class.getName());
    private static volatile PNode pNode;

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        startUp();
    }

    private static void startUp() throws UnknownHostException, InterruptedException {
        LOGGER.info("开始创建节点.........");
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        int id = Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1));
        pNode = PNode.getINSTANCE(id, ip);
        LOGGER.info("节点创建成功：" + JSON.toJSONString(pNode));
        pNode.addNode_collection(pNode.getPId(), pNode.getPLocation());

        /**
         * 启动网络同步节点
         */
        Thread first = new Thread(() -> {
            PMsg msg = PCrtPMsg.crtDiscover(pNode);
            LOGGER.info("发送发现节点消息:" + JSON.toJSONString(msg));
            // Util.firstBroad(pNode.getPLocation(), msg);
        });


        /**
         * 建立新线程处理超时
         */
        Thread time = new Thread(() -> {
            while (true) {
                try {
                    if (pNode.getPType().equals(Constant.P_REPLICA) && !pNode.getTimer().getFlag()) {
                        PMsgProcess.timeOut(pNode);
                    } else if (pNode.getPType().equals(Constant.P_PRIMARY)) {
                        Thread.sleep(Constant.DELAY_TIME * 4);
                        PMsgProcess.broadcast(pNode, PCrtPMsg.crtHeart(pNode));
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread.sleep(Constant.DELAY_TIME);
        first.start();
        time.start();
        /**
         * 主线程处理接收到的消息
         */
        while (true) {
            PMsg msg_ = (PMsg) Constant.pollMsg();
            if (msg_ != null) {
                Constant.getService().submit(new Thread(() -> {
                    try {
                        PMsgProcess.pMsgProcess(pNode, msg_);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
            }
        }
    }


}
