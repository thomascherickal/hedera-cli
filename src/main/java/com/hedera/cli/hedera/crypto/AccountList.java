package com.hedera.cli.hedera.crypto;

import java.util.Map;

import com.hedera.cli.hedera.utils.AccountUtils;
import com.hedera.cli.hedera.utils.DataDirectory;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Component
@Command(name = "ls", description = "@|fg(225) List of all accounts for the current network.|@")
public class AccountList implements Runnable {

    @Override
    public void run() {
        System.out.println("List of accounts in the current network");
        DataDirectory dataDirectory = new DataDirectory();
        AccountUtils accountUtils = new AccountUtils();
        String pathToIndexTxt = accountUtils.pathToIndexTxt();
        Map<String, String> readingIndexAccount = dataDirectory.readFileHashmap(pathToIndexTxt);

        for (Map.Entry<String, String> entry : readingIndexAccount.entrySet()) {
            System.out.println(entry.getKey() + " (" + entry.getValue() + ")");
        }
    }
}