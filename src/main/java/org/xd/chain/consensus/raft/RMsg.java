package org.xd.chain.consensus.raft;


import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author rxd
 * @ClassName Rlog
 * Description TODO
 * @date 2019-09-22 18:23
 * @Version 1.0
 */

@Getter
@Setter
@NoArgsConstructor
@JSONType
public final class RMsg implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * *************************************************************************************************************************
     * 消息类型：心跳消息 选举消息 被选举消息 日志消息 发送日志状态消息
     * ，错误消息(LEADER节点宕机后发送心跳消息接收，包含最新网络状态信息，如新的LEADER) 消息属性： 日志类型： log_type int 任期：
     * term int 索引: index int 发送消息的节点： ori_node RNode 发送消息的节点类型：ori_node_type int
     * (在节点中定义) 消息类型： type int LEADER： leader RNode 日志信息： log HashMap ->
     * 包含之前的所有index term 日志状态消息： log_state boolean 需要选举的LEADER： pre_leader RNode
     * etc....
     * <p>
     * <p>
     * <p>
     * ***********************************************************************************************************************
     */


    //日志状态消息
    @JSONField(ordinal = 3)
    private boolean log_flag;


    //日志类型
    @JSONField(ordinal = 0)
    private String log_type;
    //索引号
    @JSONField(ordinal = 2)
    private int index = 0;

    //日志信息
    private HashMap<Integer, RMsg> hashMap = new HashMap<>();
    //任期
    @JSONField(ordinal = 1)
    private int term;
    //LEADER
    private RNode leader;
    //选举LEADER 只在选举阶段使用
    private RNode pre_leader;
    //当前发送信息的节点
    @JSONField(ordinal = 4)
    private RNode node;

    //消息是否已提交
    private boolean commit;


    public RMsg(int term, int index) {
        this.term = term;
        this.index = index;
    }

    public RMsg addHashMap(int key, RMsg value) {
        this.hashMap.put(key, value);
        return this;
    }


}
