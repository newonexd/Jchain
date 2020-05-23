package org.xd.chain.consensus.raft;

import java.util.logging.Logger;

import org.xd.chain.consensus.Constant;

/**
 * @author rxd
 * @ClassName RCrtRmsg
 * Description TODO
 * @date 2019-09-28 09:40
 * @Version 1.0
 */
public final class RCrtRmsg {
    private static final Logger LOGGER = Logger.getLogger(RCrtRmsg.class.getName());

    /**
     * 创建心跳消息，只能由LEADER创建
     *
     * @param rNode
     * @return
     */
    public static RMsg crtHeartMsg(RNode rNode) {
        if (!rNode.getType().equals(Constant.R_LEADER)) {
            throw new IllegalStateException("节点不是LEADER");
        }
        LOGGER.info("创建" + Constant.R_HEARTMSG + "消息");
        RMsg rmsg = crtBasicMsg(rNode);
        rmsg.setLog_type(Constant.R_HEARTMSG);
        rmsg.setLeader(rNode);
        rmsg.setIndex(rNode.getIndex());
        return rmsg;
    }

    /**
     * 心跳响应消息
     */
    public static RMsg crtHeartResMsg(RNode node) {
        if (!node.getType().equals(Constant.R_FOLLOWER)) {
            throw new IllegalStateException("节点不是FOLLOWER");
        }
        LOGGER.info("创建" + Constant.R_HEARTRESMSG + "消息");
        RMsg msg = crtBasicMsg(node);
        msg.setLog_type(Constant.R_HEARTRESMSG);
        msg.setLeader(node.getLeader());
        msg.setLog_flag(true);
        return msg;
    }

    /**
     * 创建日志消息，只能由LEADER创建
     */
    public static RMsg crtLogMsg(RNode rNode) {
        if (!rNode.getType().equals(Constant.R_LEADER)) {
            throw new IllegalStateException("节点不是LEADER");
        }
        LOGGER.info("创建" + Constant.R_LOGGERMSG + "消息");
        RMsg log = crtBasicMsg(rNode);
        log.setLeader(rNode);
        log.setIndex(rNode.getIndex());
        RMsg msg = crtHeartMsg(rNode);
        msg.setIndex(log.getIndex());
        msg.addHashMap(log.getIndex(), log);
        return msg;
    }

    /**
     * 创建选举消息,只能由CANDIDATE创建
     *
     * @param rNode
     * @return
     */
    public static RMsg crtEleMsg(RNode rNode) {
        if (!rNode.getType().equals(Constant.R_CANDIDATE)) {
            throw new IllegalStateException("节点不是CANDIDATE");
        }
        LOGGER.info("创建" + Constant.R_ELECTEMSG + "消息");
        RMsg rmsg = crtBasicMsg(rNode);
        rmsg.setLog_type(Constant.R_ELECTEMSG);
        rmsg.setLog_flag(false);
        rmsg.setPre_leader(rNode);
        return rmsg;
    }

    /**
     * 创建被选举消息
     *
     * @param rNode
     * @param rmsg
     * @return
     */
    public static RMsg crtElectedMsg(RNode rNode, RMsg rmsg) {
        LOGGER.info("创建" + Constant.R_ELECTRESMSG + "消息");
        rmsg.setTerm(rNode.getTerm());
        rmsg.setLog_flag(true);
        rmsg.setNode(rNode);
        rmsg.setLog_type(Constant.R_ELECTRESMSG);
        return rmsg;
    }

    /**
     * 创建日志响应消息
     *
     * @param rNode
     * @return
     */
    public static RMsg crtLogStateMsg(RNode rNode, int matchIndex, boolean flag) {
        if (!rNode.getType().equals(Constant.R_FOLLOWER)) {
            throw new IllegalStateException("节点不是FOLLOWER");
        }
        LOGGER.info("创建" + Constant.R_LOGGERRESMSG + "消息");
        RMsg msg = crtBasicMsg(rNode);
        msg.setLog_type(Constant.R_LOGGERRESMSG);
        msg.setIndex(matchIndex);
        msg.setLog_flag(flag);
        msg.setLeader(rNode.getLeader());
        return msg;
    }

    /**
     * 创建日志提交消息
     *
     * @param rNode
     * @return
     */
    public static RMsg crtLogComMsg(RNode rNode, int index) {
        if (!rNode.getType().equals(Constant.R_LEADER)) {
            throw new IllegalStateException("节点不是LEADER");
        }
        LOGGER.info("创建" + Constant.R_LOGCOMMSG + "消息");
        RMsg msg = crtBasicMsg(rNode);
        msg.setLog_type(Constant.R_LOGCOMMSG);
        msg.setIndex(index);
        msg.setLeader(rNode);
        return msg;
    }

    /**
     * 创建错误消息
     *
     * @param rNode
     * @return
     */
    public static RMsg crtErrorMsg(RNode rNode) {
        LOGGER.info("创建" + Constant.R_ERRORMSG + "消息");
        RMsg msg = crtBasicMsg(rNode);
        msg.setLog_type(Constant.R_ERRORMSG);
        msg.setLeader(rNode.getLeader());
        return msg;
    }

    private static RMsg crtBasicMsg(RNode rNode) {
        RMsg rmsg = new RMsg();
        rmsg.setNode(rNode);
        rmsg.setTerm(rNode.getTerm());
        return rmsg;
    }
}
