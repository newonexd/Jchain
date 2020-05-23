package org.xd.chain.core;

import java.beans.Transient;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.xd.chain.attach.Merkle;
import org.xd.chain.storage.Storage;
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
    // 产出该区块的难度
    public int nonce;
    // 当前区块中的交易
    public ArrayList<Transaction> transaction;
    // MerkleRoot
    public String merkleRoot;


    public Block(int blkNum, Transaction transaction, String prevBlockHash) throws NoSuchAlgorithmException, Exception {
        this.blkNum = blkNum;
        this.transaction = new ArrayList<>();
        
        if(transaction!=null)
            this.transaction.add(transaction);
        this.transaction.add(Transaction.newCoinBase());

        this.prevBlockHash = prevBlockHash;
        this.timeStamp = Util.getTimeStamp();
        this.merkleRoot = Merkle.GetMerkleRoot(this.transaction);
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
        StringBuilder s = new StringBuilder();
        s.append(" block: \n");
        s.append("   hash: ").append(getCurBlockHash()).append('\n');
        s.append("   previous block: ").append(getPrevBlockHash()).append("\n");
        s.append("   time: ").append(getTimeStamp()).append('\n');
        s.append("   nonce: ").append(nonce).append("\n");
        if (getTransaction().size() >0) {
            s.append("   merkle root: ").append(getMerkleRoot()).append("\n");
            s.append("   with ").append(getTransaction().size()).append(" transaction(s):\n");
            ArrayList<Transaction> al = getTransaction();
            for (Transaction tx : al) {
                s.append(tx).append('\n');
            }
        }
        return s.toString();
    }
}