package org.kinecosystem.appstransfer.sender;

public interface KinTransferCallback {
    void onSuccess(KinTransferComplete kinTransferComplete);
    void onError(KinTransferException kinTransferException);
}
