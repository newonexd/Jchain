package org.xd.chain.core;

import java.security.NoSuchAlgorithmException;

import org.xd.chain.transaction.Transaction;
import org.xd.chain.wallet.Wallet;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException, Exception {
        Blockchain.getInstance().getBlockByBlkNum(1);
    }
}