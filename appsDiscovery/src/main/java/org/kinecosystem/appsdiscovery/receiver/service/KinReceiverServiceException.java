package org.kinecosystem.appsdiscovery.receiver.service;

import android.support.annotation.NonNull;

public class KinReceiverServiceException extends Exception {
    private String serviceFullPath;

    public KinReceiverServiceException(@NonNull String serviceFullPath, String errorDetails) {
        super("Service" + serviceFullPath + " not found. " + errorDetails);
        this.serviceFullPath = serviceFullPath;
    }

    public String getRequiredServiceFullPath() {
        return serviceFullPath;
    }
}