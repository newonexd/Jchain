package org.xd.chain;

import java.util.Scanner;

import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.xd.chain.application.Cli;
import org.xd.chain.core.Handler;
import org.xd.chain.network.NetworkHandler;

public class Node {
    private static final Logger LOGGER = Logger.getLogger(Node.class);
    public static void start(){
        Handler.handler();
        NetworkHandler.handler();
        
        Thread cli = new Thread(()->{
            Scanner sc = new Scanner(System.in);
            Options options = Cli.define();
            LOGGER.info("等待用户发起请求.................");
            while(true){
                if(sc.hasNextLine()){
                    Cli.excute(Cli.parser(options, sc.nextLine()));
                }
            }
        });
        cli.start();
    }
}