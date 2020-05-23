package org.xd.chain.consensus.pbft;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * @author rxd
 * @ClassName PMsg
 * Description TODO
 * @date 2019-10-05 15:38
 * @Version 1.0
 */
@Data
public class PMsg implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // 消息类型
    @NonNull
    @JSONField(ordinal = 0)
    private String pType;
    //当前视图
    @JSONField(ordinal = 1)
    private int view;
    //发送消息的节点ID
    @JSONField(ordinal = 2)
    private int pNode_id;
    //消息编号
    @JSONField(ordinal = 3)
    private int order;
    //消息内容的摘要
    @JSONField(ordinal = 4)
    private String digest;

    @JSONField(ordinal = 5)
    private String pNode_location;

    @JSONField(ordinal = 6)
    //当前primary节点以及下一主节点
    private int pPrimaryId;

    public PMsg() {
    }
}
