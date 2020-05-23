package org.xd.chain.storage;

import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.xd.chain.core.Block;
import org.xd.chain.wallet.Wallet;

public final class CouchDb {
    private static final Logger LOGGER = Logger.getLogger(CouchDb.class);
    private static CouchDbConnector db;

    public static void init() throws MalformedURLException {
        HttpClient httpClient = new StdHttpClient.Builder()
                                .url("http://localhost:5984")
                                .username("admin")
                                .password("admin")
                                .build();
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        db = dbInstance.createConnector("jchain", true);
    }   
    public static void save(Block block){
        if(getBlockBynum(block.blkNum)==null){
            LOGGER.info("存储区块信息: " +block.toString());
            db.update(block);
        }
    }
    public static Block getBlockBynum(int num){
        if(db.contains(String.valueOf(num))){
            Block block = db.get(Block.class, String.valueOf(num));
            return block;
        }
        return null;
    }

    public static void saveWallet(Wallet wallet){
        LOGGER.info("存储钱包信息............");
        db.update(wallet);
    }
    public static Wallet getWallet(String wallet){
        if(db.contains(wallet))
            return db.get(Wallet.class,wallet);
        return null;
    }

}