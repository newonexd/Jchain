package org.xd.chain.transaction;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class TxOutput implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // 交易输出的coin值。
    public int value;
    //锁定脚本 通常为地址
    public String lockScript;

    public TxOutput(int value,String address){
        this.value = value;
        this.lockScript = address;
    }
}