package org.xd.chain.consensus.pbft;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import org.xd.chain.consensus.*;
import org.xd.chain.util.Timer;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rxd
 * @ClassName PNode
 * Description TODO
 * @date 2019-10-05 15:37
 * @Version 1.0
 */
@Getter
@Setter
@JSONType(ignores = {"timer", "recvMsgThread", "nodeIs", "node_collection", "prepare_msg", "prepare_msg_flag", "commit_msg", "commit_msg_flag", "view_change_msg", "view_change_flag_msg", "view_change_ack_msg", "view_change_ack_flag_msg", "finished_msg"})
public class PNode {
    //节点ID
    @NonNull
    @JSONField(ordinal = 2)
    private int pId;

    //节点IP地址
    @NonNull
    @JSONField(ordinal = 3)
    private String pLocation;

    //节点类型
    @NonNull
    @JSONField(ordinal = 0)
    private volatile String pType;

    //计时器
    private transient volatile Timer timer = new Timer();
    //用于处理消息的线程
    private transient volatile Server recvMsgThread = new Server();

    //节点当前视图
    @NonNull
    @JSONField(ordinal = 1)
    private AtomicInteger view = new AtomicInteger(1);

    //当前视图的主节点
    @JSONField(ordinal = 4)
    private AtomicInteger primary_id = new AtomicInteger(-1);

    //用于下次选出主节点,按顺序保存节点的ID
    private static volatile int[] nodeIs = new int[Constant.REAL_NODE_COUNT];
    //下次主节点index
    private static volatile int next_primary_index = 0;

    //节点集合  pId,pLocation
    @JSONField(ordinal = 5)
    private final ConcurrentHashMap<Integer, String> node_collection = new ConcurrentHashMap<Integer, String>();

    //已经达成共识的消息
    @JSONField(ordinal = 10)
    private final ConcurrentHashMap<Integer, PMsg> finished_msg = new ConcurrentHashMap<Integer, PMsg>();

    //接收到的PREPARE消息
    @JSONField(ordinal = 7)
    private final ConcurrentHashMap<Integer, PMsg> prepare_msg = new ConcurrentHashMap<Integer, PMsg>();
    //标记接收到的PREPARE消息是否已大于2f
    private final ConcurrentHashMap<Integer, Boolean> prepare_msg_flag = new ConcurrentHashMap<>();
    //接收到的COMMIT消息
    @JSONField(ordinal = 8)
    private final ConcurrentHashMap<Integer, PMsg> commit_msg = new ConcurrentHashMap<Integer, PMsg>();
    //标记接收到的COMMIT消息是否已大于2f+1
    private final ConcurrentHashMap<Integer, Boolean> commit_msg_flag = new ConcurrentHashMap<>();


    //接收到的VIEW_CHANGE消息
    @JSONField(ordinal = 9)
    private final ConcurrentHashMap<Integer, PMsg> view_change_msg = new ConcurrentHashMap<Integer, PMsg>();
    //标记接收到的VIEW_CHANGE消息是否已大于2f
    private final ConcurrentHashMap<Integer, Boolean> view_change_flag_msg = new ConcurrentHashMap<>();
    //接收到的VIEW_CHANGE_ACK消息
    private final ConcurrentHashMap<Integer, PMsg> view_change_ack_msg = new ConcurrentHashMap<Integer, PMsg>();
    //标记接收到的VIEW_CHANGE_ACK消息是否已大于2f
    private final ConcurrentHashMap<Integer, Boolean> view_change_ack_flag_msg = new ConcurrentHashMap<>();
    //*************************************MSG_PROCESS**********************************************************************

    public void addPrePareMsg(PMsg msg) {
        this.prepare_msg.put(msg.getPNode_id(), msg);
    }

    public void clearPrePareMsg_Size() {
        this.prepare_msg.clear();
    }

    public void setPrepare_msg_flag(PMsg msg) {
        this.prepare_msg_flag.put(msg.getOrder(), true);
    }

    public Boolean getPrepare_msg_flag(int order) {
        return this.prepare_msg_flag.get(order);
    }

    public void setCommit_msg_flag(PMsg msg) {
        this.commit_msg_flag.put(msg.getOrder(), true);
    }

    public Boolean getCommit_msg_flag(int order) {
        return this.commit_msg_flag.get(order);
    }

    public void addCommitMsg(PMsg msg) {
        this.commit_msg.put(msg.getPNode_id(), msg);
    }

    public void clearCommitMsg_Size() {
        this.commit_msg.clear();
    }

    public void addFinished_msg(PMsg msg) {
        this.finished_msg.put(msg.getOrder(), msg);
    }

    //******************************************CHANGE_VIEW*****************************************************************
    public Boolean getViewChangeMsgFlag(int view) {
        return this.view_change_flag_msg.get(view);
    }

    public void setViewChangeMsgFlag(int view) {
        this.view_change_flag_msg.put(view, true);
    }

    public void addViewChangeMsg(PMsg msg) {
        this.view_change_msg.put(msg.getPNode_id(), msg);
    }

    public void clearViewChangeMsg() {
        this.view_change_msg.clear();
    }

    public void setViewChangeMsgAckFlag(int view) {
        this.view_change_ack_flag_msg.put(view, true);
    }

    public Boolean getViewChangeAckMsgFlag(int view) {
        return this.view_change_ack_flag_msg.get(view);
    }

    public void addViewChangeAckMsg(PMsg msg) {
        this.view_change_ack_msg.put(msg.getPNode_id(), msg);
    }

    public void clearViewChangeAckMsg() {
        this.view_change_ack_msg.clear();
    }

    //**********************************************************************************************************************

    @JSONField(ordinal = 6)
    public int getNext_PrimaryId() {
        return this.getPre_PremaryId(PNode.next_primary_index);
    }

    public void setNext_primary_index() {
        PNode.next_primary_index = (PNode.next_primary_index + 1) % Constant.REAL_NODE_COUNT;
    }

    public String getNode_Location(int pId) {
        return this.node_collection.get(pId);
    }

    private int getPre_PremaryId(int index) {
        return PNode.nodeIs[index];
    }

    public int[] getNodeIs() {
        return PNode.nodeIs;
    }

    public ConcurrentHashMap<Integer, String> getNode_collection() {
        return this.node_collection;
    }

    public void addNode_collection(int pId, String pLocation) {
        this.node_collection.put(pId, pLocation);
    }

    public ConcurrentHashMap<Integer, PMsg> getFinished_msg() {
        return this.finished_msg;
    }

    private PNode(int pId, String pLocation) {
        this.pId = pId;
        this.pLocation = pLocation;
        this.pType = Constant.P_UNKNOWN;
        this.recvMsgThread.start();
    }

    public static PNode getINSTANCE(int pId, String pLocation) {
        return new PNode(pId, pLocation);
    }

    public void setView(int view) {
        this.view.set(view);
    }

    public void setPrimary_id(int id) {
        this.primary_id.set(id);
    }

    public void incrementView() {
        this.view.incrementAndGet();
    }
}
