package org.xd.chain.consensus.raft;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import org.xd.chain.consensus.*;

/**
 * @author rxd
 * @ClassName RProcessMsg
 * Description TODO
 * @date 2019-09-29 18:58
 * @Version 1.0
 */
public final class RProcessMsg extends Thread {
    private static final Logger LOGGER = Logger.getLogger(RProcessMsg.class.getName());

    /**
     * 处理接收到的消息
     *
     * @param node
     * @param msg
     */
    public static void processMsg(RNode node, RMsg msg) throws IOException, InterruptedException {
        LOGGER.info(Thread.currentThread().getName() + ": " + "当前节点的状态为：" + node.toString() + "\n 接收到的消息内容为：" + msg.toString());
        //添加到当前已连接节点的容器
        node.add_Connect_node(msg.getNode());

        Constant.getLock().lock();
        //1.先判断任期
        if (msg.getTerm() > node.getTerm()) {
            //&& msg.getIndex() >= node.getIndex()) {
            //日志中任期大于当前节点任期，自动更新
            node.setTerm(msg.getTerm());
            node.resetTimer();
            if (msg.getLeader() != null)
                node.setLeader(msg.getLeader());
            node.setType(Constant.R_FOLLOWER);
            node.clear_elected_count();
            node.clear_Match_node();
            node.setElect_flag(false);
        }
        if (msg.getTerm() < node.getTerm()) {
            //说明日志中任期小于当前节点任期
            //返回错误消息
            Client.send_msg(msg.getNode().getLocation(), RCrtRmsg.crtErrorMsg(node));
        }
        Constant.getLock().unlock();

        //处理消息
        switch (node.getType()) {
            case Constant.R_CANDIDATE: {
                candidateProcessMsg(node, msg);
                break;
            }
            case Constant.R_FOLLOWER: {
                followerProcessMsg(node, msg);
                break;
            }
            case Constant.R_LEADER: {
                leaderProcessMsg(node, msg);
                break;
            }
        }
    }

    /**
     * 节点类型为LEADER时
     *
     * @param node
     * @param msg
     */
    private static void leaderProcessMsg(RNode node, RMsg msg) throws IOException, InterruptedException {
        switch (msg.getLog_type()) {
            case Constant.R_HEARTRESMSG: {
                Thread.sleep(Constant.IN_TIME);
                broadcast(node, RCrtRmsg.crtHeartMsg(node));
            }
            //什么也不做
            break;
            case Constant.R_LOGGERRESMSG: {
                if (msg.isLog_flag()) {
                    node.addMatch_node(msg.getNode().getName(), msg.getIndex());
                    /**
                     * 当前任期已匹配的日志是否大于当前节点数的一半
                     */
                    ArrayList<Integer> count = new ArrayList<>(node.get_Connect_node_count());
                    Iterator<Map.Entry<String, Integer>> iterator = node.getMatch_node().entrySet().iterator();
                    Map.Entry<String, Integer> entry;
                    int sum;
                    String location;
                    int index;
                    while (iterator.hasNext()) {
                        entry = iterator.next();
                        index = entry.getValue();
                        location = entry.getKey();
                        sum = count.get(index);
                        count.set(index, sum + 1);
                        if ((sum + 1) > node.get_Connect_node_count() / 2) {
                            //发送提交日志
                            Client.send_msg(location, RCrtRmsg.crtLogComMsg(node, index));
                        }
                    }
                } else {
                    int index = msg.getIndex();
                    RMsg rMsg = RCrtRmsg.crtHeartMsg(node);
                    Client.send_msg(msg.getNode().getLocation(), rMsg.addHashMap(index - 1, node.getRecv_log().get(index - 2)));
                }
                break;
            }
            case Constant.R_ERRORMSG: {
                processErrorMsg(node, msg);
                break;
            }
            default:
        }

    }

    /**
     * 节点类型为FOLLOWER时
     *
     * @param node
     * @param msg
     */
    private static void followerProcessMsg(RNode node, RMsg msg) throws IOException {
        switch (msg.getLog_type()) {
            case Constant.R_HEARTMSG: {
                node.resetTimer();
                node.setLeader(msg.getLeader());
                //包括LOGGER信息
                if (msg.getHashMap().size() == 0) {
                    //返回心跳响应消息
                    Client.send_msg(msg.getLeader().getLocation(), RCrtRmsg.crtHeartResMsg(node));
                    break;
                }
                RMsg rMsg = msg.getHashMap().get(0);
                int index = rMsg.getIndex();
                int term = rMsg.getTerm();
                if (node.getRecv_log().get(index - 1).getTerm() == term) {
                    //返回日志匹配消息
                    Client.send_msg(msg.getNode().getLocation(), RCrtRmsg.crtLogStateMsg(node, index, true));
                } else {
                    //返回日志不匹配消息
                    Client.send_msg(msg.getNode().getLocation(), RCrtRmsg.crtLogStateMsg(node, index, false));
                }
                break;
            }
            case Constant.R_ELECTEMSG: {
                if (!node.getElect_flag()) {
                    Client.send_msg(msg.getNode().getLocation(), RCrtRmsg.crtElectedMsg(node, msg));
                    node.setElect_flag(true);
                }
                break;
            }
            case Constant.R_LOGCOMMSG: {
                node.resetTimer();
                int index = msg.getIndex();
                List<RMsg> list = node.getRecv_log();
                RMsg rMsg;
                for (int i = index - 1; i >= 0; i--) {
                    rMsg = list.get(i);
                    if (!rMsg.isCommit()) {
                        rMsg.setCommit(true);
                        node.getRecv_log().add(i, rMsg);
                    }
                }
                node.setRecv_log(node.getRecv_log().subList(0, index));
                break;
            }
            case Constant.R_ERRORMSG: {
                LOGGER.warning("获取到错误类型的消息!!!");
                processErrorMsg(node, msg);
                break;
            }
            default:
        }
    }

    /**
     * 节点类型为CANDIDATE时
     *
     * @param node
     * @param msg
     */
    private static void candidateProcessMsg(RNode node, RMsg msg) throws IOException {
        switch (msg.getLog_type()) {
            case Constant.R_HEARTMSG: {
                node.setType(Constant.R_FOLLOWER);
                node.setLeader(msg.getLeader());
                node.resetTimer();
                break;
            }
            case Constant.R_ELECTEMSG: {
                if (node.getElect_flag()) {
                    LOGGER.info("当前节点已进行投票");
                    break;
                }
                LOGGER.info("当前节点进行投票");
                Client.send_msg(msg.getNode().getLocation(), RCrtRmsg.crtElectedMsg(node, msg));
                node.setElect_flag(true);
            }
            case Constant.R_ELECTRESMSG: {
                if (!msg.getPre_leader().getName().equals(node.getName())) {
                    break;
                }
                LOGGER.info("接收到投票响应.....");
                node.setElected_count(msg.getNode(), msg);
                LOGGER.info("当前投票数为：" + node.getElected_count() + ",当前已连接的节点数为:" + node.get_Connect_node_count());
                Constant.getLock().lock();
                if (node.getElected_count() > node.get_Connect_node_count() / 2 && (!node.getType().equals(Constant.R_LEADER))) {
                    LOGGER.info("当前节点接收到大多数投票，成功当选LEADER");
                    //更新为LEADER，关闭计时器
                    node.setType(Constant.R_LEADER);
                    node.setLeader(node);
                    node.setIndex(node.getIndex() + 1);
                    node.shutdownTimer();
                    //发送心跳信息
                    RMsg rMsg = RCrtRmsg.crtHeartMsg(node);
//                    RMsg rMsg = RCrtRmsg.crtLogMsg(node);
                    LOGGER.info("广播选举成功的心跳消息.....");
                    broadcast(node, rMsg);
                }
                Constant.getLock().unlock();
                break;
            }
            case Constant.R_ERRORMSG: {
                processErrorMsg(node, msg);
                break;
            }
            default:
        }
    }


    private static void processErrorMsg(RNode node, RMsg msg) {
        node.setType(Constant.R_FOLLOWER);
        node.setTerm(msg.getTerm());
        node.setLeader(msg.getLeader());
        node.resetTimer();
    }


    /**
     * 处理超时情况
     *
     * @param rNode
     * @throws IOException
     */
    public static void processTimeOut(RNode rNode) throws IOException {
        try {
            Thread.sleep((int) (Math.random() * Constant.DELAY_TIME));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rNode.setLeader(null);
        rNode.setType(Constant.R_CANDIDATE);
        rNode.clear_elected_count();
        rNode.incrementTerm();
        rNode.resetTimer();
        rNode.setElect_flag(true);
        LOGGER.info("当前节点状态为： " + rNode.toString());
        RMsg msg = RCrtRmsg.crtEleMsg(rNode);
        rNode.setElected_count(rNode, msg);
        broadcast(rNode, msg);
    }

    //广播消息
    public static void broadcast(RNode rNode, RMsg msg) throws IOException {
        Enumeration<String> enumeration = rNode.get_Connect_node().elements();
        String location;
        while (enumeration.hasMoreElements()) {
            location = enumeration.nextElement();
            Client.send_msg(location, msg);
        }
    }

}
