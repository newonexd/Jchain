package org.xd.chain.consensus.raft;

import java.io.IOException;
import java.util.logging.Logger;

import org.xd.chain.consensus.Constant;

/**
 * @author rxd
 * @ClassName REntry
 * Description TODO
 * @date 2019-09-27 19:36
 * @Version 1.0
 */
public class REntry {
    private static final Logger LOGGER = Logger.getLogger(REntry.class.getName());
    private static volatile RNode rNode;

    public static void startUp() throws IOException {
        rNode = RNode.getINSTANCE();
        //启动计时器
        rNode.startTimer();
        rNode.setElect_flag(true);
        RMsg ele = RCrtRmsg.crtEleMsg(rNode);
        rNode.setElected_count(rNode, ele);
        /**
         * 发送第一条消息
         */
        LOGGER.info("发送投票消息.....");
        Thread begin = new Thread(() -> {
            //发送投票消息
        });

        /**
         * 判断是否超时的线程
         */
        Thread time = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (!rNode.getType().equals(Constant.R_LEADER)) {
                        if (!rNode.getRTimer().getFlag()) {
                            LOGGER.warning("当前任期" + rNode.getTerm() + "已超时！！！");
                            RProcessMsg.processTimeOut(rNode);
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        begin.start();
        time.start();
        /**
         * 处理消息
         */
        while (true) {
            RMsg msg = (RMsg) Constant.pollMsg();
            if (msg != null) {
                Constant.getService().submit(new Thread(() -> {
                    try {
                        RProcessMsg.processMsg(rNode, msg);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
            }
        }
    }

}
