package org.kinecosystem.sampleSenderApp.kindiscover;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.kinecosystem.appsdiscovery.repositories.KinTransferCallback;
import org.kinecosystem.appsdiscovery.service.SendKinServiceBase;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;
import org.kinecosystem.sampleSenderApp.SenderApplication;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kin.sdk.Transaction;
import kin.sdk.TransactionId;

public class SendKinService extends SendKinServiceBase {
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public
    void transferKin(@NonNull String toAddress, int amount, @NonNull String memo, @NotNull KinTransferCallback callback) {
        SampleWallet sampleWallet = ((SenderApplication) getApplicationContext()).getSampleWallet();

        transferKinCallback = callback;

        if (!sampleWallet.hasActiveAccount()) {
            if (transferKinCallback != null)
                transferKinCallback.onError(new KinTransferException("None", "Cannot transfer Kin. Account not initialized"));
        }
        executorService.execute(() -> {
            try {
                String sourceAddress = sampleWallet.getAccount().getPublicAddress();
                int fee = 100; // no whitelisting for sample app, so using a fee
                Transaction transaction = sampleWallet.getAccount().buildTransactionSync(toAddress,
                        new BigDecimal(amount), fee, memo);
                TransactionId transactionId = sampleWallet.getAccount().sendTransactionSync(transaction);
                // here you may add some code to add the transaction details to
                // your app's transaction history metadata
                if (transferKinCallback != null)
                    transferKinCallback.onSuccess(new KinTransferComplete(sourceAddress, transactionId.id(), transaction.getMemo()));
            } catch (Exception e) {
                e.printStackTrace();
                if (transferKinCallback != null)
                    transferKinCallback.onError(new KinTransferException("None",
                        "Cannot transfer Kin. Exception " + e + ", with message " + e.getMessage()));

            }
        });
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
