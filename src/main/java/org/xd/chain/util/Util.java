package org.xd.chain.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

public final class Util{
    public static String getMD5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(data.getBytes());
        return Hex.encodeHexString(b);
    }

    public static String getSHA(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] b = md.digest(data.getBytes());
        return Hex.encodeHexString(b);
    }

    public static String getSHA256(String data) {
        byte[] b = {};
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            b = md.digest(data.getBytes());
        }catch(NoSuchAlgorithmException e){

        }
        return Hex.encodeHexString(b);
    }


    public static String getTimeStamp(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new java.util.Date());
        return date;
    }

    public static String[] mutiHash(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String[] str = new String[3];
        byte[] buf = data.getBytes();
        for(int i=0;i<3;i++){
            buf = md.digest(buf);
            str[i] = Hex.encodeHexString(buf);
        }
        return str;
    }

    public static String getRandom(){
       return  UUID.randomUUID().toString().replace("-","");
    }

    public static int byteArrayToInt(byte[] b) {
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}

}