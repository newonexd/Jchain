package org.xd.chain.application;

import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.apache.commons.cli.*;
import org.apache.commons.codec.binary.Hex;
import org.xd.chain.core.Blockchain;
import org.xd.chain.transaction.Transaction;
import org.xd.chain.wallet.Wallet;


public class Cli{

    public static Options define() {
        Option from = OptionBuilder.withLongOpt("from")
                .withDescription("transfer from where")
                .withArgName("fromAddress")
                .create();
        Option to = OptionBuilder.withLongOpt("to")
                .withDescription("transfer to where")
                .hasArg()
                .withArgName("toAddress").create();
        Option value = OptionBuilder.withLongOpt("value")
                .withDescription("coin value")
                .hasArg()
                .withArgName("value")
                .create();
        Option qblk = OptionBuilder.withLongOpt("queryblock").withDescription("query block by number")
                .hasArg()
                .withArgName("n")
                .create();
        Option qblc = OptionBuilder.withLongOpt("querybalance")
        .withDescription("query balance")
        .create();
        Options options = new Options();
        options.addOption("h", "help", false, "Print help");
        options.addOption("w", "wallet", false, "get wallet information");
        options.addOption("t", "transfer", false, "transfer coin");
        // options.addOption("q","query",false,"query function");
        // options.addOption(from);
        options.addOption(to);
        options.addOption(value);
        options.addOption(qblk);
        options.addOption(qblc);

        return options;
    }

    public static CommandLine parser(Options options, String arg) {
        String[] args = arg.split(" ");
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        CommandLineParser parser = new PosixParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.getOptions().length < 1) {
                hf.printHelp("Jchain", options, true);
                System.out.println("-------------------------------------");
            }

            if (commandLine.hasOption('h')) {
                // 打印使用帮助
                hf.printHelp("Jchain", options, true);
                System.out.println("-------------------------------------");
            }
            return commandLine;
        } catch (ParseException e) {
            hf.printHelp("Jchain", options, true);
            System.out.println("-------------------------------------");
        }
        return null;
    }

    public static void excute(CommandLine commandLine) throws NoSuchAlgorithmException, Exception {
        //if (commandLine.hasOption("q")) {
            if(commandLine.hasOption("queryblock")){
                int num = Integer.valueOf(commandLine.getOptionValue("queryblock"));
                if(num>=0){
                    Blockchain.getInstance().getBlockByBlkNum(num);
                }
            }else if(commandLine.hasOption("querybalance")){
                Wallet.getInstance().getBalance();
            }  
        //}
        if (commandLine.hasOption("w") || commandLine.hasOption("wallet")) {
            System.out.println(Wallet.getInstance().toString());
        }
        if ((commandLine.hasOption("t") || commandLine.hasOption("transfer"))
                && (commandLine.hasOption("to") && commandLine.hasOption("value"))) {
            int num = Integer.valueOf(commandLine.getOptionValue("value"));

            String fromAddress = Wallet.getInstance().getAddress();
            String toAddress = commandLine.getOptionValue("to");
            Blockchain.getInstance().addBlock(Transaction.newUTXO(fromAddress, toAddress, num));
        }

    }
}