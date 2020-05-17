package org.xd.chain.tools;

import java.io.*;

import org.apache.log4j.Logger;
import org.xd.chain.core.Block;

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

}