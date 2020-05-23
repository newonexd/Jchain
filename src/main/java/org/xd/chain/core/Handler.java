package org.xd.chain.core;

import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.xd.chain.application.Cli;
import org.xd.chain.network.NetworkHandler;
import org.xd.chain.network.Server;
import org.xd.chain.storage.CouchDb;
import org.xd.chain.wallet.Wallet;

import lombok.Getter;

@Getter
public class Handler {
    private static final Logger LOGGER = Logger.getLogger(Handler.class);
    private static Blockchain Bc;
    private static Wallet wallet;



    public static void handler(){
        LOGGER.info("初始化数据库连接器");
        CouchDb.init();
        LOGGER.info("初始化数据库连接器成功！");
        LOGGER.info("初始化区块链");
        Bc = Blockchain.getInstance();
        LOGGER.info("初始化区块链成功!");
        LOGGER.info("初始化钱包");
        wallet = Wallet.getInstance();
        LOGGER.info("初始化钱包成功!");
    }
}