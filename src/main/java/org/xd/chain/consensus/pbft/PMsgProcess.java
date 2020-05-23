package org.xd.chain.consensus.pbft;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;

import org.xd.chain.consensus.*;

/**
 * @author rxd
 * @ClassName PMsgProcess
 * Description TODO
 * @date 2019-10-05 19:05
 * @Version 1.0
 */
public final class PMsgProcess {
    private static final Logger LOGGER = Logger.getLogger(PMsgProcess.class.getName());

    public static void pMsgProcess(PNode pNode, PMsg pMsg) throws IOException, InterruptedException {
        /**
         * 首先进行网络同步，获取已加入网络的节点
         */
        if (unknown_process(pNode, pMsg))
            return;
        /**
         * 根据消息视图对具体的消息做处理
         */
        if (pMsg.getView() == pNode.getView().get()) {
            //视图相同
            switch (pNode.getPType()) {
                case Constant.P_REPLICA: {
                    replica_process(pNode, pMsg);
                    break;
                }
                case Constant.P_PRIMARY: {
                    primary_process(pNode, pMsg);
                    break;
                }
                case Constant.P_CLIENT: {
                    client_process(pNode, pMsg);
                    break;
                }
                default: {
                    LOGGER.info("当前节点接收到类型错误的消息!!!");
                }
            }
        } else if (pMsg.getView() < pNode.getView().get()) {
            //视图比当前节点小
            if (!pMsg.getPType().equals(Constant.P_VIEW_CHANGE) && !pMsg.getPType().equals(Constant.P_VIEW_CHANGE_ACK)) {
                LOGGER.info(Thread.currentThread().getName() + "**********************节点 " + JSON.toJSONString(pNode) + "接收到视图小于当前节点的消息:\n" + JSON.toJSONString(pMsg));
                Client.send_msg(pMsg.getPNode_location(), PCrtPMsg.crtReset(pNode));
            }
        } else {
            /**
             *  视图比当前节点大
             *  或者是NEW_VIEW消息
             **/
            Constant.getLock().lock();
            if (pMsg.getPType().equals(Constant.P_NEW_VIEW)) {
                process_new_view(pNode, pMsg);
            } else {
                process_large_view_msg(pNode, pMsg);
            }
            Constant.getLock().unlock();
        }
    }

    /**
     * 客户端处理消息
     *
     * @param pNode
     * @param pMsg
     */
    private static void client_process(PNode pNode, PMsg pMsg) {
        switch (pMsg.getPType()) {
            case Constant.P_REPLY: {
                break;
            }
            case Constant.P_NEW_VIEW: {
                break;
            }
            default:
                throw new IllegalStateException("消息类型错误!!");
        }
    }

    /**
     * 从节点处理消息
     *
     * @param pNode
     * @param pMsg
     */
    private static void replica_process(PNode pNode, PMsg pMsg) throws IOException, InterruptedException {
        switch (pMsg.getPType()) {
            case Constant.P_PRE_PREPARE: {
                process_pre_preparemsg(pNode, pMsg);
                break;
            }
            case Constant.P_PREPARE: {
                process_preparemsg(pNode, pMsg);
                break;
            }
            case Constant.P_COMMIT: {
                Constant.getLock().lock();
                process_commitmsg(pNode, pMsg);
                Constant.getLock().unlock();
                break;
            }
            case Constant.P_VIEW_CHANGE: {
                Constant.getLock().lock();
                process_view_change(pNode, pMsg);
                Constant.getLock().unlock();
                break;
            }
            case Constant.P_VIEW_CHANGE_ACK: {
                Constant.getLock().lock();
                process_view_change_ack(pNode, pMsg);
                Constant.getLock().unlock();
                break;
            }
            case Constant.P_HEART: {
                process_heart(pNode, pMsg);
            }
            default:
                throw new IllegalStateException("消息类型错误!!");
        }
    }

    /**
     * 主节点处理消息
     *
     * @param pNode
     * @param pMsg
     */
    private static void primary_process(PNode pNode, PMsg pMsg) throws IOException, InterruptedException {
        switch (pMsg.getPType()) {
            case Constant.P_REQUEST: {
                LOGGER.info(Thread.currentThread().getName() + "**********************节点 " + JSON.toJSONString(pNode) + "接收到REQUEST消息:\n" + JSON.toJSONString(pMsg));
                broadcast(pNode, PCrtPMsg.crtPre_prepare(pNode, pMsg.getDigest()));
                break;
            }
            case Constant.P_PREPARE: {
                process_preparemsg(pNode, pMsg);
                break;
            }
            case Constant.P_COMMIT: {
                Constant.getLock().lock();
                process_commitmsg(pNode, pMsg);
                Constant.getLock().unlock();
                break;
            }
            default:
                throw new IllegalStateException("消息类型错误!!");
        }
    }


    private static boolean unknown_process(PNode pNode, PMsg pMsg) throws IOException, InterruptedException {
        boolean flag = false;
        Constant.getLock().lock();
        if (pMsg.getPType().equals(Constant.P_DISCOVER) || pMsg.getPType().equals(Constant.P_RE_DISCOVER)) {
            flag = true;
            if (pNode.getPType().equals(Constant.P_UNKNOWN)) {
                pNode.addNode_collection(pMsg.getPNode_id(), pMsg.getPNode_location());
                LOGGER.info(Thread.currentThread().getName() + "**********************将节点添加到集合中..当前节点数量为：" + pNode.getNode_collection().size());
                if (pNode.getNode_collection().size() == Constant.REAL_NODE_COUNT) {
                    LOGGER.info(Thread.currentThread().getName() + JSON.toJSONString(pNode) + "完成网络同步.......");
                    pNode.setPType(Constant.P_REPLICA);
                    sortNode(pNode);
                    int sum = pNode.getNext_PrimaryId();
                    LOGGER.info(Thread.currentThread().getName() + "**********************ID为" + sum + "的节点成为主节点");
                    if (sum == pNode.getPId()) {
                        LOGGER.info(Thread.currentThread().getName() + "**********************当前节点" + JSON.toJSONString(pNode) + "成功主节点..........");
                        pNode.setPType(Constant.P_PRIMARY);
                        pNode.setPrimary_id(pNode.getPId());
                        LOGGER.info(Thread.currentThread().getName() + "**********************当前节点" + JSON.toJSONString(pNode) + "广播第一条消息..........");
                        pMsg = PCrtPMsg.crtHeart(pNode);
//                        pMsg = PCrtPMsg.crtPre_prepare(pNode, Constant.P_PRE_PREPARE);
                        Thread.sleep(Constant.DELAY_TIME);
                        broadcast(pNode, pMsg);
                    } else {
                        LOGGER.info(Thread.currentThread().getName() + "**********************当前节点" + JSON.toJSONString(pNode) + "为从节点,启动计时器........");
                        pNode.setPrimary_id(sum);
                        pNode.getTimer().startTimer();
                    }
                }
            }
            if (pMsg.getPType().equals(Constant.P_DISCOVER)) {
                LOGGER.info(Thread.currentThread().getName() + "**********************当前节点" + JSON.toJSONString(pNode) + "接收到发现节点消息，返回发现节点响应消息........");
                Client.send_msg(pMsg.getPNode_location(), PCrtPMsg.crtRe_Discover(pNode, pMsg));
            }
        }
        Constant.getLock().unlock();
        return flag;
    }

    private static void process_pre_preparemsg(PNode pNode, PMsg pMsg) throws IOException, InterruptedException {
        /**
         * 不用变
         */
        LOGGER.info(Thread.currentThread().getName() + "**********************节点 " + JSON.toJSONString(pNode) + "接收到PRE_PREPARE消息:\n" + JSON.toJSONString(pMsg));
        pNode.getTimer().resetTime();
        if (pMsg.getPPrimaryId() == pNode.getPrimary_id().get()) {
            Thread.sleep(Constant.DELAY_TIME);
            broadcast(pNode, PCrtPMsg.crtPrepare(pNode, pMsg));
        }
    }

    private static void process_preparemsg(PNode pNode, PMsg pMsg) throws IOException, InterruptedException {
        /**
         * 大于2f个消息
         */
        if (pMsg.getPPrimaryId() == pNode.getPrimary_id().get() && pNode.getPrepare_msg_flag(pMsg.getOrder()) == null) {
            pNode.addPrePareMsg(pMsg);
            LOGGER.info(Thread.currentThread().getName() + "**********************节点 " + JSON.toJSONString(pNode) + "接收到PREPARE消息:\n" + JSON.toJSONString(pMsg) + " 当前已接收到" + pNode.getPrepare_msg().size() + "个PREPARE消息");
            if (match2(pNode.getPrepare_msg())) {
                LOGGER.info(Thread.currentThread().getName() + "**********************当前节点接收到大于2f个PREPARE消息,即将发送COMMIT消息........");
                pNode.setPrepare_msg_flag(pMsg);
                pNode.clearPrePareMsg_Size();
                if (pNode.getPId() != pNode.getPrimary_id().get())
                    pNode.getTimer().resetTime();
                Thread.sleep(Constant.DELAY_TIME);
                broadcast(pNode, PCrtPMsg.crtCommit(pNode, pMsg));
            }
        }
    }

    private static void process_commitmsg(PNode pNode, PMsg pMsg) throws IOException, InterruptedException {
        /**
         * 大于2f+1个消息
         */
        if (pMsg.getPPrimaryId() == pNode.getPrimary_id().get() && pNode.getCommit_msg_flag(pMsg.getOrder()) == null) {
            pNode.addCommitMsg(pMsg);
            LOGGER.info(Thread.currentThread().getName() + "**********************节点 " + JSON.toJSONString(pNode) + "接收到COMMIT消息:\n" + JSON.toJSONString(pMsg) + " 当前已接收到" + pNode.getCommit_msg().size() + "个COMMIT消息");
            if (match2_1(pNode.getCommit_msg())) {
                LOGGER.info(Thread.currentThread().getName() + "**********************节点" + JSON.toJSONString(pNode) + "接收到大于2f+1个数量的COMMIT消息,提交当前消息......");
                pNode.setCommit_msg_flag(pMsg);
                pNode.clearCommitMsg_Size();
                pNode.addFinished_msg(pMsg);
                if (pNode.getPType().equals(Constant.P_REPLICA))
                    pNode.getTimer().resetTime();
                if (pNode.getPType().equals(Constant.P_PRIMARY)) {
                    LOGGER.info(Thread.currentThread().getName() + "**********************节点" + JSON.toJSONString(pNode) + "广播新一轮消息........");
                    Thread.sleep(Constant.DELAY_TIME);
//                    pMsg = PCrtPMsg.crtPre_prepare(pNode, Constant.P_PRE_PREPARE);
                    pMsg = PCrtPMsg.crtHeart(pNode);
                    broadcast(pNode, pMsg);
                }
            }
        }
    }


    private static void process_heart(PNode pNode, PMsg pMsg) {
        if (pNode.getPrimary_id().get() == pMsg.getPNode_id()) {
            LOGGER.info(Thread.currentThread().getName() + "********************节点" + JSON.toJSONString(pNode) + "接收到心跳消息:\n" + JSON.toJSONString(pMsg));
            pNode.getTimer().resetTime();
        }
    }

    private static void process_large_view_msg(PNode pNode, PMsg pMsg) throws IOException {
        LOGGER.info(Thread.currentThread().getName() + "**********************节点 " + JSON.toJSONString(pNode) + "接收到视图大于当前节点的消息:\n" + JSON.toJSONString(pMsg));
        if (pMsg.getPType().equals(Constant.P_RESET)) {
            pNode.setPType(Constant.P_REPLICA);
            pNode.setView(pMsg.getView());
            if (pMsg.getPPrimaryId() != -1)
                pNode.setPrimary_id(pMsg.getPPrimaryId());
        } else {
            pMsg.setView(pNode.getView().get());
            pMsg.setPType(Constant.P_ERROR);
            Client.send_msg(pMsg.getPNode_location(), pMsg);
        }
    }

    /**
     * 处理视图更新消息
     *
     * @param pNode
     * @param pMsg
     */
    private static void process_view_change(PNode pNode, PMsg pMsg) throws IOException {
        LOGGER.info(Thread.currentThread().getName() + "**********************节点 " + JSON.toJSONString(pNode) + "接收到VIEW_CHANGE消息:\n" + JSON.toJSONString(pMsg) + "当前已接收到" + pNode.getView_change_msg().size() + "个VIEW_CHANGE消息");
        if (pMsg.getPPrimaryId() == pNode.getNext_PrimaryId() && pNode.getViewChangeMsgFlag(pMsg.getView()) == null) {
            pNode.addViewChangeMsg(pMsg);
            if (match2(pNode.getView_change_msg())) {
                LOGGER.info(Thread.currentThread().getName() + "**********************节点" + JSON.toJSONString(pNode) + "接收到大于2f个数量的VIEW_CHANGE消息,提交当前消息......");
                pNode.setViewChangeMsgFlag(pNode.getView().get());
                pNode.clearViewChangeMsg();
                if (pNode.getPId() != pMsg.getPPrimaryId()) {
                    LOGGER.info(Thread.currentThread().getName() + "************************发送VIEW_CHANGE_ACK消息到" + pNode.getNode_Location(pNode.getNext_PrimaryId()));
                    Client.send_msg(pNode.getNode_Location(pNode.getNext_PrimaryId()), PCrtPMsg.crtViewChangeAck(pNode));
                }
            }
        }
    }

    /**
     * 处理视图更新ACK消息
     */
    private static void process_view_change_ack(PNode pNode, PMsg pMsg) throws IOException, InterruptedException {
        LOGGER.info(Thread.currentThread().getName() + "**********************节点 " + JSON.toJSONString(pNode) + "接收到VIEW_CHANGE_ACK消息:\n" + JSON.toJSONString(pMsg) + "当前已接收到" + pNode.getView_change_ack_msg().size() + "个VIEW_CHANGE_ACK消息");
        if (pNode.getPId() == pMsg.getPPrimaryId() && pNode.getViewChangeAckMsgFlag(pMsg.getView()) == null) {
            pNode.addViewChangeAckMsg(pMsg);
            if (match2(pNode.getView_change_ack_msg())) {
                /**
                 * 节点更新视图
                 */
                LOGGER.info(Thread.currentThread().getName() + "**********************节点" + JSON.toJSONString(pNode) + "接收到大于2f个数量的VIEW_CHANGE_ACK消息,提交当前消息......");
                pNode.setViewChangeMsgAckFlag(pNode.getView().get());
                pNode.setPType(Constant.P_PRIMARY);
                pNode.getTimer().shutdownTimer();
                pNode.incrementView();
                pNode.setPrimary_id(pNode.getPId());
                broadcast(pNode, PCrtPMsg.crtNewView(pNode));
                pNode.clearViewChangeAckMsg();
                Thread.sleep(Constant.DELAY_TIME);
                LOGGER.info(Thread.currentThread().getName() + "**********************节点" + JSON.toJSONString(pNode) + "广播新一轮消息........");
                broadcast(pNode, PCrtPMsg.crtPre_prepare(pNode, Constant.P_PRE_PREPARE));
            }
        }
    }

    /**
     * 处理新视图消息
     */
    private static void process_new_view(PNode pNode, PMsg pMsg) {
        if (pNode.getNext_PrimaryId() == pMsg.getPNode_id() && pNode.getPId() != pMsg.getPNode_id() && (pNode.getView().get() + 1) == pMsg.getView()) {
            LOGGER.info(Thread.currentThread().getName() + "****************************接收到新视图消息，更新视图到" + pMsg.getView());
            pNode.incrementView();
            pNode.getTimer().resetTime();
            pNode.setPrimary_id(pMsg.getPNode_id());
        }
    }


    /**
     * 处理超时逻辑
     */
    public static void timeOut(PNode pNode) throws IOException, InterruptedException {
        pNode.getTimer().resetTime();
        pNode.setNext_primary_index();
        Thread.sleep(Constant.DELAY_TIME);
        LOGGER.info(Thread.currentThread().getName() + "**********************下一视图成功主节点的ID为" + pNode.getNext_PrimaryId());
        broadcast(pNode, PCrtPMsg.crtViewChange(pNode));
    }

    /**
     * 排序节点
     */
    public static void sortNode(PNode pNode) {
        int[] coll = pNode.getNodeIs();
        int i = 0;
        Enumeration<Integer> enumeration = pNode.getNode_collection().keys();
        while (enumeration.hasMoreElements())
            coll[i++] = enumeration.nextElement();
        Arrays.sort(coll);
    }

    /**
     * 判断是否大于2f+1(不包括自己则为2f)
     */
    private static boolean match2(ConcurrentHashMap<Integer, PMsg> hashMap) {
        return (hashMap.size() / 2 * 3 + 1) >= Constant.REAL_NODE_COUNT;
    }

    private static boolean match2_1(ConcurrentHashMap<Integer, PMsg> hashMap) {
        return ((hashMap.size() - 1) / 2 * 3 + 1) >= Constant.REAL_NODE_COUNT;
    }

    //广播消息
    public static void broadcast(PNode pNode, PMsg msg) throws IOException {
        Enumeration<String> enumeration = pNode.getNode_collection().elements();
        String location;
        while (enumeration.hasMoreElements()) {
            location = enumeration.nextElement();
            Client.send_msg(location, msg);
        }
    }
}
