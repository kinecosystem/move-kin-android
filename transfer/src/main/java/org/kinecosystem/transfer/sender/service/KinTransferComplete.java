package org.kinecosystem.transfer.sender.service;

import android.support.annotation.NonNull;

public class KinTransferComplete {
    private final String senderAddress;
    private final String transactionId;
    private final String transactionMemo;

    public KinTransferComplete(@NonNull String senderAddress, @NonNull String transactionId,
                               @NonNull String transactionMemo) {
        this.senderAddress = senderAddress;
        this.transactionId = transactionId;
        this.transactionMemo = transactionMemo;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionMemo() { return transactionMemo; }
}