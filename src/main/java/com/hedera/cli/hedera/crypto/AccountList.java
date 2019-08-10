package com.hedera.cli.hedera.crypto;

import picocli.CommandLine.Command;

import java.io.File;

import com.hedera.cli.hedera.utils.DataDirectory;

@Command(name = "list", description = "@|fg(magenta) List of all accounts for the current network.|@")
public class AccountList implements Runnable {

  @Override
  public void run() {
    System.out.println("List of accounts in the current network");
    // OS-agnostic path to ~/.hedera/[currentNetwork]/accounts
    DataDirectory dataDirectory = new DataDirectory();
    String currentNetwork = dataDirectory.readFile("network.txt", "aspen");
    String path = currentNetwork + File.separator + "accounts";
    dataDirectory.listFiles(path);
  }

}