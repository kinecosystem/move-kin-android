package org.kinecosystem.appstransfer.sender;

import android.support.annotation.NonNull;


public class KinTransferComplete extends org.kinecosystem.transfer.sender.service.KinTransferComplete {

    public KinTransferComplete(@NonNull String senderAddress, @NonNull String transactionId,
                               @NonNull String transactionMemo) {
        super(senderAddress, transactionId, transactionMemo);
    }
}
