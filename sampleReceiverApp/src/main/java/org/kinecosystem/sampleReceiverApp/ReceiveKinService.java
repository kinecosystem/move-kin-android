package org.kinecosystem.sampleReceiverApp;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.kinecosystem.appsdiscovery.receiver.service.ReceiveKinServiceBase;
import org.kinecosystem.sampleReceiverApp.sampleWallet.TransfersLogs;

public class ReceiveKinService extends ReceiveKinServiceBase {
    private static final String TAG = ReceiveKinService.class.getSimpleName();

    public ReceiveKinService() {
        super();
    }

    @Override
    public void onTransactionCompleted(@NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String transactionId, @NonNull String memo) {
        Log.d(TAG, "Transaction completed from " + fromAddress + " to " + toAddress + " amount " + amount + " transactionId " + transactionId + " memo " + memo);
        TransfersLogs transfersLogs = new TransfersLogs(PreferenceManager.getDefaultSharedPreferences(this));
        transfersLogs.addTransferData("Received " + amount + "KIN from app:"+ senderAppName + " address:" + fromAddress + " (Memo:" + memo + " ,transactionId:" + transactionId + ")");
    }

    //the error is for the developers - put info as much as possible so the other side app can understand the transfer problem
    @Override
    public void onTransactionFailed(@NonNull String error, @NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress, int amount, @NonNull String memo) {
        Log.d(TAG, "Transaction failed from " + fromAddress + " to " + toAddress + " amount " + amount + " error " + error + " memo " + memo);
        TransfersLogs transfersLogs = new TransfersLogs(PreferenceManager.getDefaultSharedPreferences(this));
        transfersLogs.addTransferData("Failed receive " + amount + "KIN from app:"+ senderAppName +" address:" + fromAddress + " (Memo:" + memo + ")");
    }

}
