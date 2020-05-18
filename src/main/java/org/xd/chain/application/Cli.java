package org.xd.chain.application;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.codec.binary.Hex;
import org.xd.chain.core.Blockchain;
import org.xd.chain.wallet.Wallet;

public class Cli {
    public static void Start(String[] args) {
        Options options = new Options();
        Option opt = new Option("h", "help", false, "Print help");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("s", "start", false, "start blockchain");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("a", "add", true, "add block");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("w", "wallet", false, "init wallet");
        opt.setRequired(false);
        options.addOption(opt);


        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        CommandLine commandLine = null;
        CommandLineParser parser = new PosixParser();
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                // 打印使用帮助
                hf.printHelp("Jchain", options, true);
            }

            // 打印opts的名称和值
            System.out.println("--------------------------------------");
            Option[] opts = commandLine.getOptions();
            if (opts != null) {
                for (Option opt1 : opts) {
                    String name = opt1.getLongOpt();
                    String value = commandLine.getOptionValue(name);

                    if (name.equals("s") || name.equals("start")) {
                        System.out.println(Blockchain.getInstance().block.toString());
                    }
                    if(name.equals("a")||name.equals("add")&&value!=""){
                        System.out.println(Blockchain.getInstance().addBlock(value).toString());
                    } 
                    if(name.equals("w")||name.equals("wallet")){
                        Wallet wallet = Wallet.getInstance();
                        System.out.println("private Key:  "+Hex.encodeHexString(wallet.getPrivateKey()));
                        System.out.println();
                        System.out.println("public Key:  "+Hex.encodeHexString(wallet.getPublicKey()));
                    } 
                }
            }
        } catch (Exception e) {
            hf.printHelp("Jchain", options, true);
        }
    }
}