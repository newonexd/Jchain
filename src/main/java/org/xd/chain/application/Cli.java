package org.xd.chain.application;

import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.xd.chain.core.Blockchain;
import org.xd.chain.transaction.Transaction;
import org.xd.chain.wallet.Wallet;

@Component
public class Cli implements ApplicationRunner {
    public static void cli(String arg) {

        Options options = new Options();
        options.addOption("t", "transfer", false, "transfer coin");   
        options.addOption(OptionBuilder.withLongOpt("from")
                .withDescription("transfer from where")
                .withArgName("fromAddress")
                .create());
        options.addOption(OptionBuilder.withLongOpt("to")
                .withDescription("transfer to where")
                .hasArg()
                .withArgName("toAddress")
                .create());
        options.addOption(OptionBuilder.withLongOpt("value")
                .withDescription("coin value")
                .hasArg()
                .withArgName("value")
                .create());
        options.addOption("q","query",false,"query block by number");
        options.addOption("h", "help", false, "Print help");
        options.addOption("s", "start", false, "start blockchain");
        options.addOption("w", "wallet", false, "init wallet");

        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        CommandLine commandLine = null;
        CommandLineParser parser = new PosixParser();
        try {
            String[] args = arg.split(" ");
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                // 打印使用帮助
                hf.printHelp("Jchain", options, true);
            }

            // 打印opts的名称和值
            System.out.println("--------------------------------------");
            Option[] opts = commandLine.getOptions();

            if (commandLine.hasOption("s") || commandLine.hasOption("start")) {
                Blockchain.getInstance();
            }
            // if (commandLine.hasOption("q") || commandLine.hasOption("query") ) {
            //     int num = Integer.valueOf(commandLine.getOptionValue("q"));
            //     if(num>=0){
            //         Blockchain.getInstance().getBlockByBlkNum(num);
            //     }
            // }
            if (commandLine.hasOption("w") || commandLine.hasOption("wallet")) {
                Wallet wallet = Wallet.getInstance();
                System.out.println("private Key:  " + Hex.encodeHexString(wallet.getPrivateKey()));
                System.out.println();
                System.out.println("public Key:  " + Hex.encodeHexString(wallet.getPublicKey()));
            }
            if (commandLine.hasOption("t") || commandLine.hasOption("transfer") && commandLine.hasOption("from")
                    && commandLine.hasOption("to") && commandLine.hasOption("value")) {
                int num = Integer.valueOf(commandLine.getOptionValue("value"));
                String fromAddress = commandLine.getOptionValue("from");

                if (fromAddress == null)
                    fromAddress = Wallet.getInstance().getAddress();
                String toAddress = commandLine.getOptionValue("to");
                Blockchain.getInstance().addBlock(Transaction.newUTXO(fromAddress, toAddress, num));
            }

            if (commandLine.getOptions().length < 1) {
                hf.printHelp("Jchain", options, true);
            }

        } catch (Exception e) {
            hf.printHelp("Jchain", options, true);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Scanner sc = new Scanner(System.in);
        while(true){
            if(sc.hasNextLine())
                cli(sc.nextLine());
        }
    }
}