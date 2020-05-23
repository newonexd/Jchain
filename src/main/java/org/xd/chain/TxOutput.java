package org.xd.chain;

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

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("         ").append("lockScript:  ").append(lockScript).append("\n");
        s.append("         ").append("value:  ").append(value).append("\n");
        return s.toString();
    }
}