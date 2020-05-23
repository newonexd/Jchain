package org.xd.chain.storage;

import java.net.MalformedURLException;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

public class CouchDb {
    public static CouchDbConnector init() throws MalformedURLException {
        HttpClient httpClient = new StdHttpClient.Builder()
                                .url("http://localhost:5984")
                                .username("admin")
                                .password("admin")
                                .build();
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        return dbInstance.createConnector("jchain", true);
    }   
    
}