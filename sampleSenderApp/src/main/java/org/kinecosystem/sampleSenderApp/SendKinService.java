package org.kinecosystem.sampleSenderApp;

import android.support.annotation.NonNull;

import org.kinecosystem.appsdiscovery.sender.service.SendKinServiceBase;
import org.kinecosystem.sampleSenderApp.sampleWallet.SampleWallet;

import java.math.BigDecimal;

import kin.sdk.Transaction;
import kin.sdk.TransactionId;

public class SendKinService extends SendKinServiceBase {

    @Override
    public @NonNull
    KinTransferComplete transferKin(@NonNull String toAddress, int amount, @NonNull String memo) throws KinTransferException {
        SampleWallet sampleWallet = ((SenderApplication) getApplicationContext()).getSampleWallet();
        String sourceAddress = "None";

        if (!sampleWallet.hasActiveAccount()) {

            throw new KinTransferException(sourceAddress, "Cannot transfer Kin. Account not initialized");
        }

        try {
            sourceAddress = sampleWallet.getAccount().getPublicAddress();

            int fee = 100; // no whitelisting for sample app, so using a fee
            Transaction transaction = sampleWallet.getAccount().buildTransactionSync(toAddress,
                    new BigDecimal(amount), fee, memo);
            TransactionId transactionId = sampleWallet.getAccount().sendTransactionSync(transaction);

            // here you may add some code to add the transaction details to
            // your app's transaction history metadata

            return new KinTransferComplete(sourceAddress, transactionId.id(), transaction.getMemo());

        } catch (Exception e) {

            e.printStackTrace();

            throw new KinTransferException(sourceAddress,
                    "Cannot transfer Kin. Exception " + e + ", with message " + e.getMessage());

        }
    }

    @Override
    public BigDecimal getCurrentBalance() throws BalanceException {

        SampleWallet sampleWallet = ((SenderApplication) getApplicationContext()).getSampleWallet();

        if (!sampleWallet.hasActiveAccount()) {

            throw new BalanceException("Cannot retrieve Kin balance. Account not initialized");
        }

        try {
            return sampleWallet.getAccount().getBalanceSync().value();

        } catch (Exception e) {
            throw new BalanceException("Unable to retrieve Kin balance. Exception " + e + ", with message " + e.getMessage());
        }

    }
}
