package org.xd.chain.application;

<<<<<<< HEAD:src/main/java/org/xd/chain/application/Cli.java

import org.apache.commons.cli.*;
import org.xd.chain.core.Blockchain;
import org.xd.chain.transaction.Transaction;
import org.xd.chain.wallet.Wallet;
=======
import java.security.NoSuchAlgorithmException;

import org.apache.commons.cli.*;
>>>>>>> e771e134671d59828667c48340df9270913d5376:src/main/java/org/xd/chain/Cli.java


public class Cli{
    public static Options define() {
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

    public static void excute(CommandLine commandLine){
            if(commandLine.hasOption("queryblock")){
                int num = Integer.valueOf(commandLine.getOptionValue("queryblock"));
                if(num>=0){
                    Blockchain.getInstance().getBlockByBlkNum(num);
                }
            }else if(commandLine.hasOption("querybalance")){
                Wallet.getInstance().fetchBalance();
            }  
        
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