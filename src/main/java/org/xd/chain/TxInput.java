package org.xd.chain;

import java.io.Serializable;


public class TxInput implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // 所引用的前一个交易ID
    public String preTxId;
    // 该输入中包含的coin
    public int values;
    // 解锁脚本 通常为数字签名
    public String unLockScript;

    public TxInput(String txId, TxOutput top, Wallet wallet) throws Exception {
        //对引用的Txoutput中的地址进行签名，用于解锁引用的TxOutPut.
        this.unLockScript = wallet.sign(top.getLockScript());
        //记录引用的上一个交易ID
        this.preTxId = txId;
        //coin值等于引用的Txoutput的coin值
        this.values = top.value;
    }
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("         PreTxId: "+preTxId).append("\n");
        s.append("         ").append("unLockScript:  ").append(unLockScript).append("\n");
        s.append("         ").append("values:  ").append(values);
        return s.toString();
    }
}