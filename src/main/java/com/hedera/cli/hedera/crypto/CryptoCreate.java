
package com.hedera.cli.hedera.crypto;

import com.hedera.cli.ExampleHelper;
import com.hedera.hashgraph.sdk.HederaException;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.account.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "create",
//        headerHeading = "@|bold,underline Usage:|@%n%n",
//        synopsisHeading = "%n",
//        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
//        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
//        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = "Creates a new account in Hedera network",
        description = "Creates a new Hedera account and returns an accountID in the form of" +
                "shardNum.realmNum.accountNum.",
        helpCommand = true)
public class CryptoCreate implements Runnable {

  @Option(names = { "-r", "--record"}, description = "Generates a record that lasts 25hrs")
  private boolean generateRecord;

  @Option(names = {"-b", "--balance"}, description = "Initial balance of new account created")
  private int initBal;

  @Override
  public void run() {

      // Generate a Ed25519 private, public key pair
      var newKey = Ed25519PrivateKey.generate();
      var newPublicKey = newKey.getPublicKey();

      System.out.println("private key = " + newKey);
      System.out.println("public key = " + newPublicKey);

      var client = ExampleHelper.createHederaClient();

      var tx = new AccountCreateTransaction(client)
              // The only _required_ property here is `key`
              .setKey(newKey.getPublicKey())
              .setInitialBalance(this.initBal);

      // This will wait for the receipt to become available
      TransactionReceipt receipt = null;
      try {
          receipt = tx.executeForReceipt();
      } catch (HederaException e) {
          e.printStackTrace();
      }
      assert receipt != null;
      var newAccountId = receipt.getAccountId();
      System.out.println("account = " + newAccountId);
  }
}