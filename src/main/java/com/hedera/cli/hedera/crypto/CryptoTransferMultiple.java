package com.hedera.cli.hedera.crypto;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name= "multiple",
        description = "Transfer hbars to multiple accounts",
        helpCommand = true)
public class CryptoTransferMultiple implements Runnable {

//    @Option(names = {"-r", "--recipient"}, arity = "0..2",  description = "Recipient to transfer to"+
//            "%n@|bold,underline Usage:|@%n" +
//            "transfer single -r=1234,-a=100 OR %n" +
//            "transfer single --recipient=1234,--recipientAmt=100")
//    private String recipient;
//
//    @Option(names = {"-a", "--recipientAmt"}, arity = "0..2", description = "Amount to transfer")
//    private String recipientAmt;

    @Override
    public void run() {
        System.out.println("TODO multiple transfer list");
    }
}