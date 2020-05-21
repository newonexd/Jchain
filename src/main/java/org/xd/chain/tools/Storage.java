package org.xd.chain.tools;

import java.io.*;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.log4j.Logger;
import org.xd.chain.core.Block;
import org.xd.chain.wallet.Wallet;

public final class Storage {
    private transient static final Logger LOGGER = Logger.getLogger(Storage.class);
     //序列化区块信息
     public static void Serialize(Block block) throws IOException {
        File file = new File("src/main/resources/blocks/"+block.getBlkNum()+".block");
        if(!file.exists()) file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        
        oos.writeObject(block);
        LOGGER.info("Serialize Block:"+block.toString());
        oos.close();
        fos.close();
    }


        /**
     * 反序列化区块
     */
    public static Block Deserialize(int num) throws FileNotFoundException, IOException, ClassNotFoundException {
        File file = new File("src/main/resources/blocks/"+num+".block");
        if(!file.exists()) return null;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        
        Block block = (Block)ois.readObject();
        LOGGER.info("Deserialize Block:"+block.toString());
        ois.close();
        return block;
    }


    /**
     * 钱包序列化与反序列化
     * 
     * @throws IOException
     */
    public static void SerializeWallet(Wallet wallet) throws IOException {
        File file = new File("src/main/resources/wallet/"+"wallet.json");
        if(!file.exists()) file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        
        oos.writeObject(wallet);
        LOGGER.info("Serialize Wallet:"+wallet.toString());
        oos.close();
        fos.close();
    }

    public static Wallet DeserializeWallet() throws Exception {
        File file = new File("src/main/resources/wallet/"+"wallet.json");
        if(!file.exists()) {
            return null;
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Wallet wlt = (Wallet)ois.readObject();

        LOGGER.info("Deserialize Wallet");
        
        ois.close();
        return wlt;
    }
}