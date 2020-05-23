package org.xd.chain.consensus;

import lombok.Getter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author rxd
 * @ClassName basic.Constant
 * Description TODO
 * @date 2019-09-29 10:34
 * @Version 1.0
 */
public final class Constant {


    //任期超时时间
    public static final int LIMIT_TIME = 30000;
    //LEADER接收到心跳响应信息后发送心跳信息的间隔时间
    public static final int IN_TIME = LIMIT_TIME / 2;
    //节点通信的端口号
    public static final int PORT = 10000;
    //更新任期后进行选举的延迟时间
    public static final int DELAY_TIME = 1500;
    //当前已连接的节点数
    public static final int R_CONNECTED_NODE = 3;
    //消息的最大容量
    public static final int MSG_SIZE = 1024 * 4;
    //消息的最大数量
    public static final int MAX_MSG_COUNT = 10000;

    @Getter
    private static final ReentrantLock lock = new ReentrantLock();

    private static volatile ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<>();

    public static void addMsg(Object obj) {
        Constant.queue.add(obj);
    }

    public static synchronized Object pollMsg() {
        return Constant.queue.poll();
    }

    @Getter
    private static final ExecutorService service = Executors.newFixedThreadPool(10);
//************************************RAFT*******************************************************
    /**
     * 节点类型
     */
    public static final String R_LEADER = "LEADER";
    public static final String R_FOLLOWER = "FOLLOWER";
    public static final String R_CANDIDATE = "CANDIDATE";

    /**
     * 日志消息类型
     */
    public static final String R_LOGGERRESMSG = "LOGGERRESMSG";
    public static final String R_LOGGERMSG = "LOGGERMSG";
    public static final String R_HEARTMSG = "HEARTMSG";
    public static final String R_HEARTRESMSG = "HEARTRESMSG";
    public static final String R_ELECTEMSG = "ELECTEMSG";
    public static final String R_ELECTRESMSG = "ELECTRESMSG";
    public static final String R_LOGCOMMSG = "LOGCOMMSG";
    public static final String R_ERRORMSG = "ERRORMSG";


//************************************RAFT*******************************************************


    //************************************PBFT********************************************************
    //用于网络初始化发现节点
    public static final String P_DISCOVER = "DISCOVER";
    //用于发现节点回应
    public static final String P_RE_DISCOVER = "RE_DISCOVER";
    //更新节点
    public static final String P_RESET = "RESET";
    public static final String P_HEART = "HEART";
    public static final String P_PRE_PREPARE = "PRE_PREPARE";
    public static final String P_PREPARE = "PREPARE";
    public static final String P_COMMIT = "COMMIT";


    public static final String P_REQUEST = "REQUEST";
    public static final String P_REPLY = "REPLY";
    public static final String P_VIEW_CHANGE = "VIEW_CHANGE";
    public static final String P_NEW_VIEW = "NEW_VIEW";
    public static final String P_VIEW_CHANGE_ACK = "VIEW_CHANGE_ACK";
    public static final String P_ERROR = "ERROR";


    public static final String P_UNKNOWN = "UNKNOWN";
    public static final String P_PRIMARY = "PRIMARY";
    public static final String P_REPLICA = "REPLICA";
    public static final String P_CLIENT = "CLIENT";


    //算当前节点
    public static final int REAL_NODE_COUNT = 4;

//************************************PBFT*******************************************************
}
