package org.xd.chain;

import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.xd.chain.application.Cli;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     * @throws Exception
     * @throws NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, Exception {
        Scanner sc = new Scanner(System.in);
        while(true){
            if(sc.hasNextLine()){
                Cli.excute(Cli.parser(Cli.define(), sc.nextLine()));
            }
        }
    }
}
