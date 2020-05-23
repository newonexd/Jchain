package org.xd.chain.transaction;


import lombok.Getter;

@Getter
public class TxOutput{

    // 交易输出的coin值。
    public int value;
    //锁定脚本 通常为地址
    public String lockScript;

    private TxOutput(){}
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