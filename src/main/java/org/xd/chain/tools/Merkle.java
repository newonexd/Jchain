package org.xd.chain.tools;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.xd.chain.util.Util;

public final class Merkle {
    public static final String GetMerkleRoot(Set<String> data) throws NoSuchAlgorithmException {
        if(data==null || data.size()==0){
            return "";
        }
        if(data.size()==1){
            return Util.getSHA256((String)data.toArray()[0]);
        }
        
        ArrayList<String> al = new ArrayList<String>(data.size());
        Iterator<String> it = data.iterator();
        while(it.hasNext())
            al.add(it.next());
        al = getNextList(al);
        return al.get(0);
    }

    private static final ArrayList<String> getNextList(ArrayList<String> data) throws NoSuchAlgorithmException {
        int length = data.size();
        if(length==1)
            return data;
        if(length%2!=0){
            data.add(data.get(length-1));
        }
        length = data.size();
        ArrayList<String> al = new ArrayList<>(length/2);

        for(int i=0;i<length;i+=2){
            al.add(Util.getSHA256(data.get(i)+data.get(i+1)));
        }
        return getNextList(al);
    }
}