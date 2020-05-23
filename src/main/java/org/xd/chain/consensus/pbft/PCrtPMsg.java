package org.xd.chain.consensus.pbft;

import org.xd.chain.consensus.*;

/**
 * @author rxd
 * @ClassName PCrtPMsg Description TODO
 * @date 2019-10-06 09:33
 * @Version 1.0
 */
public class PCrtPMsg {
    /**
     * 创建基本属性的消息
     *
     * @param pNode
     * @param digest
     * @return
     */
    private static PMsg crtBasicMsg(PNode pNode, String digest) {
        PMsg msg = new PMsg();
        msg.setDigest(digest);
        msg.setOrder(pNode.getFinished_msg().size() + 1);
        msg.setPNode_id(pNode.getPId());
        msg.setView(pNode.getView().get());
        msg.setPNode_location(pNode.getPLocation());
        return msg;
    }

    /**
     * 创建预准备消息
     *
     * @param pNode
     * @param digest
     * @return
     */
    static PMsg crtPre_prepare(PNode pNode, String digest) {
        if (!pNode.getPType().equals(Constant.P_PRIMARY))
            throw new IllegalStateException("节点类型错误!!");
        PMsg pre_ = crtBasicMsg(pNode, digest);
        pre_.setPPrimaryId(pNode.getPId());
        pre_.setPType(Constant.P_PRE_PREPARE);
        return pre_;
    }

    /**
     * 创建准备消息
     *
     * @param pNode
     * @param pre_pare
     * @return
     */
    static PMsg crtPrepare(PNode pNode, PMsg pre_pare) {
        if (pNode.getPType().equals(Constant.P_CLIENT))
            throw new IllegalStateException("节点类型错误!!");
        pre_pare.setPType(Constant.P_PREPARE);
        pre_pare.setDigest(Constant.P_PREPARE);
        pre_pare.setPNode_id(pNode.getPId());
        return pre_pare;
    }

    /**
     * 创建提交消息
     *
     * @param pNode
     * @param prepare
     * @return
     */
    static PMsg crtCommit(PNode pNode, PMsg prepare) {
        if (pNode.getPType().equals(Constant.P_CLIENT))
            throw new IllegalStateException("节点类型错误!!");
        prepare.setPNode_id(pNode.getPId());
        prepare.setPType(Constant.P_COMMIT);
        prepare.setDigest(Constant.P_COMMIT);
        return prepare;
    }


    /**
     * 创建发现节点消息
     *
     * @param pNode
     * @return
     */
    public static PMsg crtDiscover(PNode pNode) {
        PMsg dis = new PMsg();
        dis.setView(pNode.getView().get());
        dis.setPType(Constant.P_DISCOVER);
        dis.setPNode_location(pNode.getPLocation());
        dis.setPNode_id(pNode.getPId());
        return dis;
    }

    /**
     * 创建回复发现节点消息
     *
     * @param pNode
     * @param dis
     * @return
     */
    static PMsg crtRe_Discover(PNode pNode, PMsg dis) {
        dis.setPNode_id(pNode.getPId());
        dis.setPNode_location(pNode.getPLocation());
        dis.setPType(Constant.P_RE_DISCOVER);
        dis.setDigest(Constant.P_RE_DISCOVER);
        return dis;
    }

    /**
     * 创建重置属性消息
     *
     * @param pNode
     * @return
     */
    static PMsg crtReset(PNode pNode) {
        PMsg reset = new PMsg();
        reset.setPType(Constant.P_RESET);
        reset.setView(pNode.getView().get());
        reset.setPNode_id(pNode.getPId());
        reset.setPNode_location(pNode.getPLocation());
        if (pNode.getPrimary_id().get() != -1) {
            reset.setPPrimaryId(pNode.getPrimary_id().get());
        } else {
            reset.setPPrimaryId(-1);
        }
        return reset;
    }

    /**
     * 创建更改视图消息
     *
     * @param pNode
     * @return
     */
    static PMsg crtViewChange(PNode pNode) {
        PMsg msg = crtBasicMsg(pNode, Constant.P_VIEW_CHANGE);
        msg.setPPrimaryId(pNode.getNext_PrimaryId());
        msg.setPType(Constant.P_VIEW_CHANGE);
        return msg;
    }

    /**
     * 创建更改视图确认消息
     *
     * @param pNode
     * @return
     */
    static PMsg crtViewChangeAck(PNode pNode) {
        PMsg msg = crtBasicMsg(pNode, Constant.P_VIEW_CHANGE_ACK);
        msg.setPType(Constant.P_VIEW_CHANGE_ACK);
        msg.setPPrimaryId(pNode.getNext_PrimaryId());
        return msg;
    }

    /**
     * 创建新视图消息
     *
     * @param pNode
     * @return
     */
    static PMsg crtNewView(PNode pNode) {
        PMsg msg = crtBasicMsg(pNode, Constant.P_NEW_VIEW);
        msg.setPType(Constant.P_NEW_VIEW);
        msg.setPPrimaryId(pNode.getPId());
        return msg;
    }

    /**
     * 创建心跳消息
     */
    public static PMsg crtHeart(PNode pNode) {
        PMsg msg = crtBasicMsg(pNode, Constant.P_HEART);
        msg.setPType(Constant.P_HEART);
        msg.setPPrimaryId(pNode.getPId());
        return msg;
    }
}
