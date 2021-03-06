package com.hedera.cli.hedera.validation;

import com.hedera.cli.hedera.crypto.CryptoTransferOptions;
import com.hedera.cli.shell.ShellHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class ValidateTransferList {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private ValidateAccounts validateAccounts;

    @Autowired
    private ValidateAmount validateAmount;

    private List<String> amountList;
    private List<String> senderList;
    private List<String> recipientList;
    private List<String> finalAmountList;
    private boolean isTiny;
    private CryptoTransferOptions cryptoTransferOptions;

    public void setCryptoTransferOptions(CryptoTransferOptions cryptoTransferOptions) {
        this.cryptoTransferOptions = cryptoTransferOptions;
    }

    public void setFinalAmountList(List<String> finalAmountList) {
        this.finalAmountList = finalAmountList;
    }

    public List<String> getFinalAmountList(CryptoTransferOptions cryptoTransferOptions) {
        setCryptoTransferOptions(cryptoTransferOptions);
        return this.finalAmountList;
    }

    public long sumOfAmountList(List<String> amountList) {
        long sumOfAmount;
        if (isTiny) {
            sumOfAmount = validateAmount.sumOfTinybarsInLong(amountList);
        } else {
            sumOfAmount = validateAmount.sumOfHbarsInLong(amountList);
        }
        return sumOfAmount;
    }

    public List<String> updateAmountList(List<String> amountList, long sumOfRecipientsAmount) {
        if (!isTiny) {
            finalAmountList = new ArrayList<>(convertAmountListToTinybar(amountList));
        } else {
            finalAmountList = new ArrayList<>(amountList);
        }
        // adding the sender's amount to list
        String amount = "-" + sumOfRecipientsAmount;
        finalAmountList.add(0, amount);
        setFinalAmountList(finalAmountList);
        return finalAmountList;
    }

    public List<String> convertAmountListToTinybar(List<String> amountList) {
        List<String> convertedAmountList = new ArrayList<>();
        long hbarsToTiny = 0;
        for (int i = 0; i < amountList.size(); i++) {
            hbarsToTiny = validateAmount.convertHbarToLong(amountList.get(i));
            convertedAmountList.add(String.valueOf(hbarsToTiny));
        }
        return convertedAmountList;
    }

    private List<String> getAmountList(CryptoTransferOptions o) {
        return validateAmount.getAmountList(o);
    }

    private List<String> getSenderList(CryptoTransferOptions o) {
        return validateAccounts.getSenderList(o);
    }

    private List<String> getRecipientList(CryptoTransferOptions o) {
        return validateAccounts.getRecipientList(o);
    }

    private boolean isTiny(CryptoTransferOptions o) {
        return validateAmount.isTiny(o);
    }

    private boolean senderListHasOperator(CryptoTransferOptions o) {
        return validateAccounts.senderListHasOperator(o);
    }

    private boolean verifyZeroSum(long sumOfTransferAmount) {
        return validateAmount.verifyZeroSum(sumOfTransferAmount);
    }

    public boolean verifyCleanedAmountList(List<String> amountList) {
        List<String> cleanedAmountList;
        long sumOfTransferAmount = sumOfAmountList(amountList);
        if (errorInRecipientAmount(sumOfTransferAmount)) return false;
        if (!isTiny) {
            cleanedAmountList = convertAmountListToTinybar(amountList);
        } else {
            cleanedAmountList = amountList;
        }
        setFinalAmountList(cleanedAmountList);
        return validateAmount.verifyZeroSum(sumOfTransferAmount);
    }

    private boolean checkSum(CryptoTransferOptions o, List<String> amountList,
                             List<String> senderList, List<String> recipientList) {
        int amountSize = amountList.size();
        int transferSize = senderList.size() + recipientList.size();
        boolean amountListVerified = false;

        // 3 possible scenarios
        // (1) amountList.size == transferList.size, then verifyCleanedAmountList will make the determination between true or false
        // (2) senderList does not contain operator && amountList.size != transferList.size
        // (3) senderList contains operator && amountList.size != transferList.size

        if (amountSize == transferSize) {
            return verifyCleanedAmountList(amountList);
        }

        if (!senderListHasOperator(o)) {
            shellHelper.printError("Invalid transfer list. Your transfer list must sum up to 0");
            return amountListVerified; // false
        }

        if (senderListHasOperator(o)) {
            long sumOfRecipientAmount = sumOfAmountList(amountList);
            if (errorInRecipientAmount(sumOfRecipientAmount)) return false;
            // sum up recipient's amount and update transfer list
            long sumOfTransferAmount = updatedTransferListWithSenderAmount(amountList, sumOfRecipientAmount);
            amountListVerified = verifyZeroSum(sumOfTransferAmount);
        }
        return amountListVerified;
    }

    public long updatedTransferListWithSenderAmount(List<String> amountList, long sumOfRecipientsAmount) {
        List<String> updatedAmountList = updateAmountList(amountList, sumOfRecipientsAmount);
        return sumOfAmountList(updatedAmountList);
    }

    private boolean errorInRecipientAmount(long sumOfRecipientAmount) {
        return sumOfRecipientAmount == -1L;
    }

    public boolean verifyAmountList(CryptoTransferOptions o) {
        setCryptoTransferOptions(o);
        amountList = getAmountList(o);
        senderList = getSenderList(o);
        recipientList = getRecipientList(o);
        isTiny = isTiny(o);
        boolean amountListVerified = false;
        if (senderList.size() == 1) {
            amountListVerified = checkSum(o, amountList, senderList, recipientList);
        } else {
            shellHelper.printWarning("More than 2 senders not supported");
        }
        return amountListVerified;
    }
}
