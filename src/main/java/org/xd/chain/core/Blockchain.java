package org.xd.chain.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xd.chain.tools.Storage;
import org.xd.chain.util.Util;

public final class Blockchain {

    private transient static final Logger LOGGER = Logger.getLogger(Blockchain.class);
    // 单例模式不再说明
    private static Blockchain BC;
    public Block block;

    private Blockchain() {
    }

    public static Blockchain getInstance() throws FileNotFoundException, ClassNotFoundException, IOException {
        if (BC == null) {
            synchronized (Blockchain.class) {
                if (BC == null) {
                    BC = new Blockchain();
                }
            }
        }
        if(BC.block==null){
            Block block = BC.getLastBlock();
            BC.block = block;
            if(block==null){
                BC.CrtGenesisBlock();
            }
        }
        return BC;
    }

    private Block CrtGenesisBlock() throws IOException {
        Block block = new Block(1,"Genesis Block","00000000000000000");
        block.setNonce(
            Pow.calc(block));
        //计算区块哈希值
        String hash = Util.getSHA256(block.getBlkNum()+block.getData()+block.getPrevBlockHash()+block.getPrevBlockHash()+block.getNonce());
        block.setCurBlockHash(hash);
        //序列化
        Storage.Serialize(block);
        this.block=block;
       // LOGGER.info(BC.block.get(0).toString());
        return this.block;
    }

    public Block addBlock(String data) throws IOException {
        int num = this.block.getBlkNum();
        Block block = new Block(
            num+1,data, this.block.curBlockHash);
        //每次将区块添加进区块链之前需要计算难度值
        block.setNonce(
            Pow.calc(block));
        //计算区块哈希值
        String hash = Util.getSHA256(block.getBlkNum()+block.getData()+block.getPrevBlockHash()+block.getPrevBlockHash()+block.getNonce());
        block.setCurBlockHash(hash);
        //序列化
        Storage.Serialize(block);
        this.block = block;
        // LOGGER.info(block.toString());
        return this.block;
    }

            /**
     * 获取最新的区块
     * @return
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Block getLastBlock() throws FileNotFoundException, ClassNotFoundException, IOException {
        File file = new File("src/main/resources/blocks");
        
        String[] files = file.list();
        if(files.length!=0){
            int MaxFileNum = 1;
            for(String s:files){
                int num = Integer.valueOf(s.substring(0, 1));
                if(num>=MaxFileNum)
                    MaxFileNum = num;
            }
            LOGGER.info("Current Last Block num is:"+MaxFileNum);
           return Storage.Deserialize(MaxFileNum);
        }
        return null;
    }
}