package org.kinecosystem.appstransfer.sender;

import android.support.annotation.NonNull;

public class KinTransferException extends Exception {
    private String senderAddress;

    public KinTransferException(@NonNull String senderAddress, @NonNull String transferError) {
        super(transferError);
        this.senderAddress = senderAddress;
    }

    public String getSenderAddress() {
        return senderAddress;
    }
}