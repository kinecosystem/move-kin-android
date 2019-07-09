package org.kinecosystem.sampleReceiverApp.kindiscover;

import android.support.annotation.NonNull;
import android.util.Log;

import org.kinecosystem.transfer.receiver.service.ReceiveKinServiceBase;

public class ReceiveKinService extends ReceiveKinServiceBase {
    private static final String TAG = ReceiveKinService.class.getSimpleName();

    public ReceiveKinService() {
        super();
    }

    @Override
    public void onTransactionCompleted(@NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String transactionId, @NonNull String memo) {
        Log.d(TAG, "Kin Transfer completed. Account public address: " + toAddress + " received " + amount + " KIN from " + senderAppName + " public address: " + fromAddress
                + ".\n The transactionId is: " + transactionId + " with memo " + memo);

        // If you display a transaction history in your app, here is the place
        // to add the metadata of the transaction to your database.
    }

    @Override
    public void onTransactionFailed(@NonNull String error, @NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String memo) {
        Log.d(TAG, "Kin Transfer failed. Account public address: " + toAddress + ", DID NOT receive " + amount + " KIN from " + senderAppName + " public address: " + fromAddress
                + ". The attempted transaction memo was " + memo + ". The error message is " + error);
    }
}
