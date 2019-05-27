package org.kinecosystem.transfer.receiver.service;

import android.support.annotation.NonNull;

public class ServiceConfigurationException extends Exception {
    private String serviceFullPath;

    public ServiceConfigurationException(@NonNull String serviceFullPath, String errorDetails) {
        super("Unable to use service " + serviceFullPath + ". Reason: " + errorDetails);
        this.serviceFullPath = serviceFullPath;
    }

    public String getRequiredServiceFullPath() {
        return serviceFullPath;
    }
}