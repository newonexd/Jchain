package org.xd.chain.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.xd.chain.tools.Storage;
import org.xd.chain.transaction.Transaction;
import org.xd.chain.util.Util;
import org.xd.chain.wallet.Wallet;

public final class Blockchain {

    private transient static final Logger LOGGER = Logger.getLogger(Blockchain.class);
    // 单例模式不再说明
    private static Blockchain BC;
    public Block block;

    private Blockchain() {
    }

    public static Blockchain getInstance() throws NoSuchAlgorithmException, Exception {
        if (BC == null) {
            synchronized (Blockchain.class) {
                if (BC == null) {
                    BC = new Blockchain();
                }
            }
        }
        if (BC.block == null) {
            Block block = BC.getLastBlock();
            BC.block = block;
            if (block == null) {
                BC.CrtGenesisBlock();
            }
        }
        return BC;
    }

    private Block CrtGenesisBlock() throws NoSuchAlgorithmException, Exception {
        // Block block = new Block(1,"Genesis Block","00000000000000000");
        Block block = new Block(1, Transaction.newCoinBase(), "00000000000000000");
        block.setNonce(Pow.calc(block));
        // 计算区块哈希值
        String hash = Util.getSHA256(block.getBlkNum() + block.getData() + block.getPrevBlockHash()
                + block.getPrevBlockHash() + block.getNonce());
        block.setCurBlockHash(hash);
        // 序列化
        Storage.Serialize(block);
        this.block = block;
        // LOGGER.info(BC.block.get(0).toString());
        return this.block;
    }

    public Block addBlock(String data) throws IOException {
        int num = this.block.getBlkNum();
        Block block = new Block(num + 1, data, this.block.curBlockHash);
        // 每次将区块添加进区块链之前需要计算难度值
        block.setNonce(Pow.calc(block));
        // 计算区块哈希值
        String hash = Util.getSHA256(block.getBlkNum() + block.getData() + block.getPrevBlockHash()
                + block.getPrevBlockHash() + block.getNonce());
        block.setCurBlockHash(hash);
        // 序列化
        Storage.Serialize(block);
        this.block = block;
        // LOGGER.info(block.toString());
        return this.block;
    }

    public Block addBlock(Transaction tx) throws IOException {
        int num = this.block.getBlkNum();
        Block block = new Block(num + 1, tx, this.block.curBlockHash);
        // 每次将区块添加进区块链之前需要计算难度值
        block.setNonce(Pow.calc(block));
        // 计算区块哈希值
        String hash = Util.getSHA256(block.getBlkNum() + block.getData() + block.getPrevBlockHash()
                + block.getPrevBlockHash() + block.getNonce());
        block.setCurBlockHash(hash);
        // 序列化
        Storage.Serialize(block);
        this.block = block;
        LOGGER.info("当前区块信息为:"+block.toString());
        return this.block;
    }

    /**
     * 获取最新的区块
     * 
     * @return
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Block getLastBlock() throws FileNotFoundException, ClassNotFoundException, IOException {
        File file = new File("src/main/resources/blocks");

        String[] files = file.list();
        if (files.length != 0) {
            int MaxFileNum = 1;
            for (String s : files) {
                int num = Integer.valueOf(s.substring(0, 1));
                if (num >= MaxFileNum)
                    MaxFileNum = num;
            }
            LOGGER.info("Current Last Block num is:" + MaxFileNum);
            return Storage.Deserialize(MaxFileNum);
        }
        return null;
    }

    public Transaction[] findAllUnspendableUTXO(String address)
            throws FileNotFoundException, ClassNotFoundException, IOException {
        LOGGER.info("查找所有未消费的UTXO...............");
        HashMap<String, Transaction> txs = new HashMap<>();
        Block block = this.block;
        Transaction tx;
        // 从当前区块向前遍历查找UTXO txOutput
        do{
            tx = block.getTransaction();
            // 如果存在交易信息，且TxOutput地址包含address
            if (tx != null && tx.getTops().containsKey(address)) {
                txs.put(tx.getTxId(), tx);
            }
            block = block.getPrevBlock();
        }while(block!=null && block.hasPrevBlock()) ;
        // 创世区块
       // txs.put(block.getTransaction().getTxId(), block.getTransaction());
        // 再遍历一次查找已消费的UTXO
        block = this.block;
        do {
            tx = block.getTransaction();
            if (tx != null) {
                // 如果交易中的TxInput包含的交易ID存在于txs，移除
                tx.getTips().forEach(tip -> {
                    try {
                        if (Wallet.getInstance().verify(address,tip.unLockScript) 
                                && txs.containsKey(tip.preTxId))
                            txs.remove(tip.preTxId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            block = block.getPrevBlock();
        }while (block!=null && block.hasPrevBlock());
        Transaction[] t = new Transaction[txs.size()];
        return txs.values().toArray(t);
    }
}