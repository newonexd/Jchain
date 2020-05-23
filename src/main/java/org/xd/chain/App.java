package org.xd.chain;

import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.apache.commons.cli.Options;
import org.xd.chain.application.Cli;
import org.xd.chain.core.Blockchain;


/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, Exception {
        Scanner sc = new Scanner(System.in);
        Options options = Cli.define();
        Blockchain.getInstance();
        while(true){
            if(sc.hasNextLine()){
                Cli.excute(Cli.parser(options, sc.nextLine()));
            }
        }
    }
}
