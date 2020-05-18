package org.xd.chain.core;

import java.beans.Transient;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.xd.chain.tools.Storage;
import org.xd.chain.transaction.Transaction;
import org.xd.chain.util.Util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Block implements Serializable{
    private static final long serialVersionUID = 1L;
    // 区块号
    public int blkNum;
    // 当前区块哈希值
    public String curBlockHash;
    // 前一个区块的哈希值
    public String prevBlockHash;
    // 生成当前区块的时间，用时间戳表示
    public String timeStamp;
    // 当前区块中的Transaction,使用字符串简单代替
    public String data;
    //产出该区块的难度
    public int nonce;
    //当前区块中的交易
    public Transaction transaction;

    public Block(int blkNum,String data, String prevBlockHash){
        this.blkNum = blkNum;
        this.data = data;
        this.prevBlockHash = prevBlockHash;
        this.timeStamp = Util.getTimeStamp();
    }

    public Block(int blkNum,Transaction transaction,String prevBlockHash){
        this.blkNum = blkNum;
        this.transaction = transaction;
        this.prevBlockHash = prevBlockHash;
        this.timeStamp = Util.getTimeStamp();
    }

       /**
     * 是否存在前一个区块
     */
    public boolean hasPrevBlock(){
        if(this.getBlkNum()!=1){
            return true;
        }
        return false;
    }

    @Transient
    @JsonIgnore
    /**
     * 获取前一个区块
     */
    public Block getPrevBlock() throws FileNotFoundException, ClassNotFoundException, IOException {
        if(this.hasPrevBlock())
            return Storage.Deserialize(this.getBlkNum()-1);
        return null;          
    }


    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}