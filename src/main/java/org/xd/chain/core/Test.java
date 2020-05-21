package org.xd.chain.core;

import java.security.NoSuchAlgorithmException;


public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException, Exception {
        Blockchain.getInstance().getBlockByBlkNum(1);
    }
}