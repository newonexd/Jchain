package org.xd.chain.application;

public class CliTest {
    public static void main(String[] args){
        // String[] str = {"-s","-w","-a","block"};
        // String[] str = {"-h"};
        String str = "-s -w -a block";
        Cli.cli(str);
    }
}