package org.xd.chain.consensus.raft;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import org.xd.chain.consensus.Constant;
import org.xd.chain.consensus.Server;
import org.xd.chain.util.Timer;

import lombok.Getter;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * @author rxd
 * @ClassName RNode
 * Description TODO
 * @date 2019-09-22 16:54
 * @Version 1.0
 */
@JSONType(ignores = {"Connect_node"})
public final class RNode implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(RNode.class.getName());

    /**
     * 节点属性：
     * 节点类型：           LEADER：0   FOLLOWER：1  CANDIDATE：2
     * 当前任期：           term                int
     * 当前已收到日志：      recv_log          ConcurrentHashMap
     * 计时器：
     * 是否已选举过节点：    elect_flag         boolean
     * 节点标识：           name              String
     * 当前LEADER；        current_leader     AtomicReference<RNode>
     * 当前选举票数：       elected_count     AtomicInteger
     * 当前已连接的节点      connect_node      AtomicReference<RNode>
     * etc....
     */


    //当前任期
    @JSONField(ordinal = 2)
    private AtomicInteger term;

    //节点启动时默认为FOLLOWER
    @JSONField(ordinal = 1)
    private volatile String type;

    //当前已收到的日志
    @JSONField(ordinal = 6)
    private transient List<RMsg> recv_log = Collections.synchronizedList(new ArrayList<RMsg>());

    //当前任期内是否已经选举过节点
    @JSONField(ordinal = 5)
    private AtomicBoolean elect_flag;

    //当前节点的标识
    @Getter
    @JSONField(ordinal = 0)
    private final String name;
    @Getter
    @JSONField(ordinal = 4)
    private final String location;

    //当前的LEADER
    @JSONField(ordinal = 6)
    private final AtomicReference<RNode> leader = new AtomicReference<>();


    //当前选举票数，需要线程安全
    @JSONField(serialize = false)
    private transient final ConcurrentHashMap<String, RMsg> elected_count = new ConcurrentHashMap<>();

    //保存已连接到当前节点
    @JSONField(serialize = false)
    private final ConcurrentHashMap<String, String> connect_node = new ConcurrentHashMap<>();

    //当前节点最新消息的索引
    private volatile int index = 0;


    //当前日志消息已匹配的节点
    @JSONField(serialize = false)
    private transient volatile ConcurrentHashMap<String, Integer> match_node = new ConcurrentHashMap<String, Integer>();

    @Getter
    @JSONField(serialize = false)
    private final transient Server recv_thread = new Server();


    @Getter
    @JSONField(serialize = false)
    private transient final Timer rTimer = new Timer();

    public static RNode getINSTANCE() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        return new RNode(address.getHostName(), address.getHostAddress());
    }

    private RNode(String name, String location) {
        LOGGER.info("正在启动节点......");
        LOGGER.info("当前节点" + name + "地址为" + location);
        this.name = name;
        this.location = location;
        this.type = Constant.R_CANDIDATE;
        this.term = new AtomicInteger(1);
        //设置为未进行选举
        this.elect_flag = new AtomicBoolean(false);

        //启动当前服务器线程
        this.recv_thread.start();
        //构造完成后启动计时器线程
        LOGGER.info("节点启动成功!!!!");
        LOGGER.info("当前节点状态为：" + this.toString());
    }

    public boolean resetTimer() {
        return this.rTimer.resetTime();
    }

    public void shutdownTimer() {
        this.rTimer.shutdownTimer();
    }

    public boolean startTimer() {
        return this.rTimer.startTimer();
    }


    public int getTerm() {
        return term.get();
    }

    public void incrementTerm() {
        /**
         * 任期加一
         * 1.清空投票数
         * 2.设置为未进行投票
         */
        this.term.incrementAndGet();
    }

    public void setTerm(int term) {
        this.term.set(term);
    }

    public void setElect_flag(boolean flag) {
        elect_flag.set(flag);
    }

    public boolean getElect_flag() {
        return elect_flag.get();
    }

    public void add_Connect_node(RNode rNode) {
        this.connect_node.put(rNode.getName(), rNode.getLocation());
    }

    public String remove_Connect_node(String node_name) {
        return connect_node.remove(node_name);
    }

    @JSONField(serialize = false)
    public int get_Connect_node_count() {
        //根据实际情况调整
//        return connect_node.size();
        //这里指定为3
        return Constant.R_CONNECTED_NODE;
    }

    public ConcurrentHashMap<String, String> get_Connect_node() {
        return this.connect_node;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RNode getLeader() {
        return leader.get();
    }

    public void setLeader(RNode leader) {
        this.leader.set(leader);
    }

    public int getElected_count() {
        return elected_count.size();
    }

    public void setElected_count(RNode rNode, RMsg rmsg) {
        elected_count.put(rNode.getName(), rmsg);
    }

    public List<RMsg> getRecv_log() {
        return this.recv_log;
    }

    public void addRecv_log(RMsg rmsg) {
        this.recv_log.set(rmsg.getIndex() - 1, rmsg);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public void setRecv_log(List<RMsg> list) {
        this.recv_log = list;
    }

    public void clear_elected_count() {
        elected_count.clear();
    }

    public void addMatch_node(String node_name, int index) {
        this.match_node.put(node_name, index);
    }

    public void clear_Match_node() {
        this.match_node.clear();
    }

    public ConcurrentHashMap<String, Integer> getMatch_node() {
        return this.match_node;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }


}
