package com.hedera.cli.hedera.crypto;

import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import com.hedera.cli.ExampleHelper;
import java.math.BigInteger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

@Command(name= "transfer",
//        headerHeading = "@|bold,underline Usage:|@%n%n",
//        synopsisHeading = "%n",
//        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
//        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
//        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = "Transfer hbars to a single or multiple accounts",
        description = "Transfer hbar to single or multiple hedera accounts",
        helpCommand = true)
public class CryptoTransfer implements Runnable {

    @Option(names = {"-r", "--recipient"}, arity = "0..2", description = "Recipient to transfer to")
    private String recipient;

    @Option(names = {"-a", "--recipientAmt"}, arity = "0..2", description = "Amount to transfer")
    private String recipientAmt;

    public ParseResult handleArgsHere() {
        CommandLine cmd = new CommandLine(this);
        System.out.println("handling args");
        System.out.println(recipient);
        System.out.println(recipientAmt);
        ParseResult result =  cmd.parseArgs();

        return result;
    }

    @Override
    public void run() {

        var operatorId = ExampleHelper.getOperatorId();
        var client = ExampleHelper.createHederaClient();
        var recipientId = AccountId.fromString("0.0." + recipient);
        var amount = new BigInteger(recipientAmt);

        try {
            var senderBalanceBefore = client.getAccountBalance(operatorId);
            var receiptBalanceBefore = client.getAccountBalance(recipientId);
            System.out.println("" + operatorId + " balance = " + senderBalanceBefore);
            System.out.println("" + recipientId + " balance = " + receiptBalanceBefore);
            new CryptoTransferTransaction(client)
                    // .addSender and .addRecipient can be called as many times as you want as long
                    // as the total sum from
                    // both sides is equivalent
                    .addSender(operatorId, amount.longValue()).addRecipient(recipientId, amount.longValue())
                    .setMemo("transfer test")
                    // As we are sending from the operator we do not need to explicitly sign the
                    // transaction
                    .executeForRecord();

            System.out.println("transferring " + amount.longValue() + " tinybar...");
            var senderBalanceAfter = client.getAccountBalance(operatorId);
            var receiptBalanceAfter = client.getAccountBalance(recipientId);
            System.out.println("" + operatorId + " balance = " + senderBalanceAfter +
                    "\n" + recipientId + " balance = " + receiptBalanceAfter);
        } catch (HederaException e) {
            e.printStackTrace();
        }
    }
}
