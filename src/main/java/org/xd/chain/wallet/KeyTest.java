package org.xd.chain.wallet;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class KeyTest {

    public static void main(String[] args) throws DecoderException, Exception {
        Wallet wallet  = Wallet.getInstance();
        System.out.println("private Key:  "+Hex.encodeHexString(wallet.getPrivateKey()));
        System.out.println();
        System.out.println("public Key:  "+Hex.encodeHexString(wallet.getPublicKey()));
        System.out.println(
            wallet.verify(
                wallet.sign("test".getBytes())));
        System.out.println("address: "+wallet.getAddress());
    }

}