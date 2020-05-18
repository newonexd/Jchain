package org.xd.chain.core;

import java.security.NoSuchAlgorithmException;

import org.xd.chain.transaction.Transaction;
import org.xd.chain.wallet.Wallet;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException, Exception {
        Blockchain.getInstance().addBlock(
            Transaction.newUTXO(
                Wallet.getInstance().getAddress(), "address", 30))
                .toString();
        Blockchain.getInstance().addBlock(
            Transaction.newUTXO(
                "address", "address1", 20))
                .toString();
    }
}