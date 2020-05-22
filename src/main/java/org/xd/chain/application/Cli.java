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
        Option query = OptionBuilder.withLongOpt("query").withDescription("query block by number")
                .hasArg()
                .withArgName("n")
                .create();
        Options options = new Options();
        options.addOption("h", "help", false, "Print help");
        options.addOption("s", "start", false, "start blockchain");
        options.addOption("w", "wallet", false, "get wallet information");
        options.addOption("t", "transfer", false, "transfer coin");
        options.addOption(from);
        options.addOption(to);
        options.addOption(value);
        options.addOption(query);

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
            }

            if (commandLine.hasOption('h')) {
                // 打印使用帮助
                hf.printHelp("Jchain", options, true);
            }
            return commandLine;
        } catch (ParseException e) {
            hf.printHelp("Jchain", options, true);
        }
        return null;
    }

    public static void excute(CommandLine commandLine) throws NoSuchAlgorithmException, Exception {
        if (commandLine.hasOption("s") || commandLine.hasOption("start")) {
            Blockchain.getInstance();
        }
        if (commandLine.hasOption("query") ) {
            int num = Integer.valueOf(commandLine.getOptionValue("query"));
            if(num>=0){
                Blockchain.getInstance().getBlockByBlkNum(num);
            }
        }
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

    }
}