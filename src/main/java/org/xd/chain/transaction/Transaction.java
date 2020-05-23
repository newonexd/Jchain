package org.xd.chain.transaction;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.xd.chain.core.Blockchain;
import org.xd.chain.util.Util;
import org.xd.chain.wallet.Wallet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction{
    private transient static final Logger LOGGER = Logger.getLogger(Transaction.class);

    private transient static final int COINBASE = 50;
    public String txId;
    // 交易输入的集合
    public ArrayList<TxInput> tips;
    // 交易输出的集合 String:address
    public HashMap<String, TxOutput> tops;

    private Transaction() {
    }

    private void setTxId(){
        this.txId = Util.getSHA256(Util.getTimeStamp()+Util.getRandom()+this.toString());
    }


    public static Transaction newCoinBase() throws NoSuchAlgorithmException, Exception {
        Transaction t = new Transaction();
        t.tips = new ArrayList<>();
        t.tops = new  HashMap<>();
        t.tops.put(Wallet.getInstance().getAddress(), new TxOutput(COINBASE, Wallet.getInstance().getAddress()));
        t.setTxId();
        LOGGER.info("创建Coinbase....."+t.toString());
        return t;
    }

    public static Transaction newUTXO(String fromAddress, String toAddress, int value)
            throws NoSuchAlgorithmException, Exception {
        /**
         * 1.收集所有Txoutput的锁定脚本为from的UTXO 2.判断是否大于value 3.收集用到的Txoutput并创建对应的TxInput
         * 4.如果value不均等，则返回多余的value到原地址(TxOutput) 5.创建UTXO
         */
        Transaction[] txs = Blockchain.getInstance().findAllUnspendableUTXO(fromAddress);
        if (txs.length == 0) {
            LOGGER.info("当前地址"+fromAddress+"没有未消费的UTXO！！！");
            throw new Exception("当前地址"+fromAddress+"没有未消费的UTXO,交易失败！！！");
        }
        TxOutput top;
        // 记录需要使用的TxOutput
        HashMap<String, TxOutput> tops = new HashMap<String, TxOutput>();
        int maxValue = 0;
        // 遍历交易集合
        for (int i = 0; i < txs.length; i++) {
            // 查找包括地址为fromAddress的TxOutput
            if (txs[i].tops.containsKey(fromAddress)) {
                top = txs[i].tops.get(fromAddress);
                // 添加进Map
                tops.put(txs[i].txId, top);
                // 记录该TxOutput中的value
                maxValue += top.value;
                // 如果大于需要使用的则退出
                if (maxValue >= value) {
                    break;
                }
            }
        }
        // 是否有足够的coin
        if (maxValue >= value) {
            // 创建tx
            Transaction t = new Transaction();
            t.tops = new HashMap<>();
            t.tips = new ArrayList<TxInput>(tops.size());

            // 遍历所有需要用到的Txoutput
            tops.forEach((s, to) -> {
                // 变为TxInput
                try {
                    t.tips.add(new TxInput(s, to, Wallet.getInstance()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            //如果值不均等
            if(maxValue>value){
                //创建TxOutput返还多余的coin
                top = new TxOutput(maxValue-value, Wallet.getInstance().getAddress());
                t.tops.put(top.getLockScript(), top);
            }
            //目的地址
            top = new TxOutput(value, toAddress);
            t.tops.put(top.getLockScript(), top);
            t.setTxId();
            LOGGER.info("创建UTXO: "+t.toString());
            return t;
        }
        LOGGER.info("当前地址余额不足!!,余额为"+maxValue);
        throw new Exception("当前地址余额不足!!,余额为"+maxValue);
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("   {").append("\n");
        s.append("      TransactionId: ").append(this.txId+"\n");
        s.append("      UTXOInputs:   \n");
        s.append("      [").append("\n");
        for(TxInput tip:tips){
            s.append(tip).append("\n");
        }
        s.append("      ]").append("\n");
        s.append("      UTXOOutputs:   \n");
        s.append("      [").append("\n");
        Collection<TxOutput> toutps = tops.values();
        for(TxOutput top:toutps){
            s.append(top);
        }
        s.append("      ]").append("\n");
        s.append("   }");
        return s.toString();
    }
    
}