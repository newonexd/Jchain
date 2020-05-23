package org.xd.chain;

import java.security.KeyPairGenerator;
import java.util.Map;



import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.HashMap;
import javax.crypto.Cipher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class RSAKey {

     //非对称密钥算法
     public static final String KEY_ALGORITHM = "RSA";


     /**
      * 密钥长度必须是64的倍数，在512到65536位之间
      */
     private static final int KEY_SIZE = 512;
     //公钥
     private static final String PUBLIC_KEY = "RSAPublicKey";
 
     //私钥
     private static final String PRIVATE_KEY = "RSAPrivateKey";



    private byte[] privateKey;
    private byte[] publicKey;
    private String address;

    private RSAKey() {
    }

    /**
     * 生成密钥
     */
    public static RSAKey GenerateKeyPair() throws Exception {
        RSAKey key = new RSAKey();
        Map<String, Object> keyPair = key.initKey();

        Key pk = (Key) keyPair.get(PRIVATE_KEY);
        key.setPrivateKey(pk.getEncoded());
        
        pk = (Key)keyPair.get(PUBLIC_KEY);
        key.setPublicKey(pk.getEncoded());

        return key;
    }

 
 
    private Map<String, Object> initKey() throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //将密钥存储在map中
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
 
    }
 
 
    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key       密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data,byte[] pk) throws Exception {
 
        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pk);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }


   /**
    * 公钥解密
    *
    * @param data 待解密数据
    * @param key  密钥
    * @return byte[] 解密数据
    */
    public static byte[] decryptByPublicKey(byte[] data,byte[] pk) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pk);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

}