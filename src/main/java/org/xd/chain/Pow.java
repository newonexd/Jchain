package org.xd.chain;


public class Pow {
    //固定的难度值
    private static final String DIFFICULT = "0000";

    //最大难度值
    private static final int MAX_VALUE = Integer.MAX_VALUE;

    public static int calc(Block block){
        //nonce从0开始
        int nonce = 0;
        //将区块数据简单保存为字符串
        String data = block.getBlkNum()+block.getMerkleRoot()+block.getPrevBlockHash()+block.getPrevBlockHash();
        //如果nonce小于最大难度值
        while(nonce<MAX_VALUE){
            //计算哈希值
            if(Util.getSHA256(data+nonce)
                    //如果计算出的哈希值前缀满足条件，退出循环
                    .startsWith(DIFFICULT))
                break;
            //不满足条件，nonce+1，重新计算哈希值
            nonce++;
        }
        return nonce;
    }
}