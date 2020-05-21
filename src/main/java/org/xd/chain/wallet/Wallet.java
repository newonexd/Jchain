package org.xd.chain.wallet;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.xd.chain.tools.Storage;
import org.xd.chain.util.Util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wallet implements Serializable{
    private transient static final Logger LOGGER = Logger.getLogger(Wallet.class);
    private transient static Wallet wallet;
    private byte[] privateKey;
    private byte[] publicKey;
    private String address;

    private Wallet() throws Exception {
        RSAKey key  = RSAKey.GenerateKeyPair();
        this.privateKey = key.getPrivateKey();
        this.publicKey = key.getPublicKey();
        this.address = generateAddress();
        Storage.SerializeWallet(this);
    }

    public static Wallet getInstance() throws Exception {
        if (wallet == null) {
            wallet = Storage.DeserializeWallet();
            if (wallet == null) {
                wallet = new Wallet();
            }
        }
        return wallet;
    }


        /**
     * 根据密钥生成地址
     */
    private String generateAddress() throws NoSuchAlgorithmException {
        String pk = Hex.encodeHexString(this.publicKey);
        this.address = ("R" + Util.getSHA256(pk) + Util.getSHA256(Util.getSHA256(pk))).substring(0, 15);
        LOGGER.info("当前钱包地址为: "+this.address);
        return this.address;

    }

    /**
     * 获取地址
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String getAddress() throws NoSuchAlgorithmException {
        if(this.address!=""){
            return this.address;
        }
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
     * 
     * @throws Exception
     * @throws DecoderException
     */
    public String sign(String data) throws DecoderException, Exception {
        LOGGER.info("使用私钥对数据签名: "+data);
        return sign(data.getBytes());
    }


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
        LOGGER.info("验证签名: "+data);
        String[] str = data.split("%%%");
        // 原文     encry(hash(原文))
        if(str.length!=2){
            return false;
        }
        String hash = Util.getSHA256(str[0]);
        String hash2 = Hex.encodeHexString(this.decrypt(str[1]));
        if(hash.equals(hash2)){
            LOGGER.info("签名验证成功！！");
            return true;
        }
        LOGGER.info("签名验证失败！！");
        return false;

    }

    public boolean verify(String data,String sign) throws DecoderException, Exception {
        LOGGER.info("验证签名: "+data);
        String[] str = data.split("%%%");
        // 原文     encry(hash(原文))
        if(str.length!=2){
            return false;
        }
        String hash2 = Hex.encodeHexString(this.decrypt(str[1]));
        String hash3 = Util.getSHA256(data);
        if(hash3.equals(hash2)){
            LOGGER.info("签名验证成功！！");
            return true;
        }
        LOGGER.info("签名验证失败！！");
        return false;
    }

}

