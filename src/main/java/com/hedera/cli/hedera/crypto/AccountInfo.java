package com.hedera.cli.hedera.crypto;

import java.util.Arrays;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@NoArgsConstructor
@Setter
@Component
@Command(name = "info",
        description = "@|fg(225) Gets the information of the paying/operator account"
                + " returns a stateproof if requested|@")
public class AccountInfo implements Runnable {

    @Autowired
    ApplicationContext context;

    private Ed25519PrivateKey accPrivKey;
    private InputReader inputReader;

    @Option(names = { "-a", "--account-id" }, description = "Account ID in %nshardNum.realmNum.accountNum format")
    private String accountIDInString;

    @Override
    public void run() {
        try {
            String accPrivKeyInString = inputReader.prompt("Input account's private key", "secret", false);
            accPrivKey = Ed25519PrivateKey.fromString(accPrivKeyInString);

            Hedera hedera = new Hedera(context);
            var accountId = AccountId.fromString(accountIDInString);
            var client = hedera.createHederaClient()
                    .setMaxTransactionFee(100000000)
                    .setOperator(accountId, accPrivKey);
            AccountInfoQuery q;
            q = new AccountInfoQuery(client)
                    .setAccountId(accountId);
            var accountRes = q.execute();
            String[] accountInfo =
                    {
                            "accountId: " + accountRes.getAccountId() +
                                    "\n contractId: " + accountRes.getContractAccountId() +
                                    "\n balance: " + accountRes.getBalance() +
                                    "\n claim: " + accountRes.getClaims() +
                                    "\n autoRenewPeriod: " + accountRes.getAutoRenewPeriod() +
                                    "\n expirationTime: " + accountRes.getExpirationTime() +
                                    "\n receivedRecordThreshold: " + accountRes.getGenerateReceiveRecordThreshold() +
                                    "\n proxyAccountId: " + accountRes.getProxyAccountId()
                    };
            System.out.println(Arrays.asList(accountInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
