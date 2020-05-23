package org.xd.chain.attach;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Set;

import org.xd.chain.util.Util;

public final class Bloom {

    /**
     * 添加数据集到布隆过滤器
     */
    public static HashMap<String,Boolean> addDatasToBloom(HashMap<String,Boolean> bloom,Set<String> datas)
            throws NoSuchAlgorithmException {
        if(datas.size()==0){
            return bloom;
        }
        for(String data:datas){
            bloom = Bloom.addDataToBloom(bloom, data);
        }
        return bloom;
    }

    /**
     * 添加数据到布隆过滤器
     */
    public static HashMap<String,Boolean> addDataToBloom(HashMap<String,Boolean> bloom, String data) throws NoSuchAlgorithmException {
        String[] str = Util.mutiHash(data);
        for(int i=0;i<3;i++){
            bloom.put(str[i],true);
        }
        return bloom;
    }

    /**
     * 从布隆过滤器查找数据是否存在
     */
    public static boolean findDataFromBloom(HashMap<String,Boolean> bloom,String data)
            throws NoSuchAlgorithmException {
        String[] str = Util.mutiHash(data);
        for(int i=0;i<3;i++){
            if(!bloom.get(str[i]))
                return false;
        }
        return true;
    }
}