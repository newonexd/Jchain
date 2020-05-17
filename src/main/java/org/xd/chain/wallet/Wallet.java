package org.xd.chain.wallet;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.xd.chain.util.Util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wallet {
    private static Wallet wallet;
    private byte[] privateKey;
    private byte[] publicKey;
    private String address;

    private Wallet() throws Exception {
        RSAKey key  = RSAKey.GenerateKeyPair();
        this.privateKey = key.getPrivateKey();
        this.publicKey = key.getPublicKey();
        this.address = generateAddress();
    }

    public static Wallet getInstance() throws Exception {
        if (wallet == null) {
            synchronized (Wallet.class) {
                if (wallet == null) {
                    wallet = new Wallet();
                }
            }
        }
        return wallet;
    }


        /**
     * 根据密钥生成地址
     */
    private String generateAddress() throws NoSuchAlgorithmException {
        String pk = Hex.encodeHexString(this.publicKey);
        this.address = "R" + Util.getSHA256(pk) + Util.getSHA256(Util.getSHA256(pk));
        return this.address;

    }

    /**
     * 获取地址
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String getAddress() throws NoSuchAlgorithmException {
        return this.generateAddress();
    }

    /**
     * 加密数据
     */
    public String encrypt(byte[] data) throws Exception {
        byte[] encry = RSAKey.encryptByPrivateKey(data,this.privateKey);
        return Hex.encodeHexString(encry);
    }

    /**
     *  解密数据
     */
    public byte[] decrypt(String encry) throws DecoderException, Exception {
        return RSAKey.decryptByPublicKey(Hex.decodeHex(encry),this.publicKey);
    }

    /**
     * 签名数据
     */
    public String sign(byte[] data) throws Exception {
        //原文首先进行哈希
        String hash = Util.getSHA256(
            Hex.encodeHexString(data));
        //哈希值进行加密
        String sign = encrypt(
            Hex.decodeHex(hash));
        //原文+encry(hash(原文))
        return Hex.encodeHexString(data)+"%%%"+sign;
    }

    /**
     * 验证签名
     */
    public boolean verify(String data) throws DecoderException, Exception {
        String[] str = data.split("%%%");
        // 原文     encry(hash(原文))
        if(str.length!=2){
            return false;
        }
        String hash = Util.getSHA256(str[0]);
        String hash2 = Hex.encodeHexString(this.decrypt(str[1]));
        if(hash.equals(hash2)){
            return true;
        }
        return false;

    }

}

