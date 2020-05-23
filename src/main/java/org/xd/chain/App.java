package org.xd.chain;

import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.Options;
import org.xd.chain.application.Cli;
import org.xd.chain.core.Handler;


/**
 * Hello world!
 */
public final class App {
    public static void main(String[] args){
        Node.start();
    }
}
