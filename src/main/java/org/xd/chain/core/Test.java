package org.xd.chain.core;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println(Blockchain.getInstance().block.toString());
        System.out.println(Blockchain.getInstance().addBlock("Block 2").toString());
    }
}