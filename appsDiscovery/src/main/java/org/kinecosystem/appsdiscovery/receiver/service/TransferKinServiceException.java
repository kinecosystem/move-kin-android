package org.kinecosystem.appsdiscovery.receiver.service;

import android.support.annotation.NonNull;

public class TransferKinServiceException extends Exception {
    private String serviceFullPath;

    public TransferKinServiceException(@NonNull String serviceFullPath, String errorDetails) {
        super("Service" + serviceFullPath + " not found. " + errorDetails);
        this.serviceFullPath = serviceFullPath;
    }

    public String getRequiredServiceFullPath() {
        return serviceFullPath;
    }
}