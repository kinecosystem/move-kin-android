package org.kinecosystem.appsdiscovery.receiver.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

public abstract class ReceiveKinServiceBase extends Service {

    //developer must implements
    public abstract void onTransactionCompleted(@NonNull String fromAddress, @NonNull String toAddress, int amount, @NonNull String transactionId, @NonNull String memo);

    public abstract void onTransactionFailed(@NonNull String error, @NonNull String fromAddress, @NonNull String toAddress, int amount, @NonNull String memo);

    //TODO need to change to support prefix of the pkg
    //and change the manifest to the convention for action name is FQN, i.e. org.kinecosystem....KinReceiverTransactionCompleted

    private static final String ACTION_TRANSACTION_COMPLETED = "KinReceiverTransactionCompleted";
    private static final String ACTION_TRANSACTION_FAILED = "KinReceiverTransactionFailed";
    private static final String SERVICE_ARG_FROM_ADDRESS = "fromAddress";
    private static final String SERVICE_ARG_TO_ADDRESS = "toAddress";
    private static final String SERVICE_ARG_AMOUNT = "amount";
    private static final String SERVICE_ARG_TRANSACTION_ID = "transactionId";
    private static final String SERVICE_ARG_MEMO = "memo";
    private static final String SERVICE_ARG_ERROR = "error";
    private static final int NO_AMOUNT = 0;
    private static final String SERVICE_NAME = "ReceiveKinService";


    public static class KinReceiverServiceException extends Exception {
        private String serviceFullPath;
        private boolean isNotFound;
        private boolean isServicePublic;


        public KinReceiverServiceException(@NonNull String serviceFullPath, boolean isNotFound, boolean isServicePublic) {
            super("service " + serviceFullPath + " exception");
            this.serviceFullPath = serviceFullPath;
            this.isNotFound = isNotFound;
            this.isServicePublic = isServicePublic;
        }

        @Override
        public String getMessage() {
            String message = "service exception";
            if (isNotFound) {
                message = "serviced " + serviceFullPath + " not found or implemented";
            } else if (isServicePublic) {
                message = "serviced " + serviceFullPath + " cant be define exported on manifest";

            }
            return message;
        }
    }


    public static void notifyTransactionCompleted(@NonNull Context context, @NonNull String receiverPackageName, @NonNull String fromAddress, @NonNull String toAddress, int amount, @NonNull String transactionId, @NonNull String memo) throws KinReceiverServiceException {
        Intent intent = getTransactionResultIntent(context, receiverPackageName, true);
        intent.putExtra(SERVICE_ARG_FROM_ADDRESS, fromAddress);
        intent.putExtra(SERVICE_ARG_TO_ADDRESS, toAddress);
        intent.putExtra(SERVICE_ARG_AMOUNT, amount);
        intent.putExtra(SERVICE_ARG_TRANSACTION_ID, transactionId);
        intent.putExtra(SERVICE_ARG_MEMO, memo);
        context.startService(intent);
    }

    public static void notifyTransactionFailed(@NonNull Context context, @NonNull String receiverPackageName, @NonNull String error, @NonNull String fromAddress, @NonNull String toAddress, int amount, @NonNull String memo) throws KinReceiverServiceException {
        Intent intent = getTransactionResultIntent(context, receiverPackageName, false);
        intent.putExtra(SERVICE_ARG_ERROR, error);
        intent.putExtra(SERVICE_ARG_FROM_ADDRESS, fromAddress);
        intent.putExtra(SERVICE_ARG_TO_ADDRESS, toAddress);
        intent.putExtra(SERVICE_ARG_AMOUNT, amount);
        intent.putExtra(SERVICE_ARG_MEMO, memo);
        context.startService(intent);
    }

    private static Intent getTransactionResultIntent(Context context, String receiverPackageName, final Boolean isCompleted) throws KinReceiverServiceException {
        String action = isCompleted ? ACTION_TRANSACTION_COMPLETED : ACTION_TRANSACTION_FAILED;
        Intent intent = new Intent();
        intent.setAction(action);
        String serviceFullPath = receiverPackageName + "." + SERVICE_NAME;
        intent.setComponent(new ComponentName(receiverPackageName, serviceFullPath));
        intent.setPackage(receiverPackageName);
        final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(intent, 0);
        if (resolveInfos.isEmpty()) {
            //TODO notify error service not found - hence the receiver will not be inform of the transaction
            Log.d("####", "#### cant find the service " + receiverPackageName + ".ReceiveKinService");
            throw new KinReceiverServiceException(serviceFullPath, true, true);
        } else {
            for (ResolveInfo info : resolveInfos) {
                if (info.serviceInfo.exported) {
                    //TODO throw exception service is exported
                    Log.d("####", "####  service " + receiverPackageName + ".ReceiveKinService export must be declared on manifest false");
                    throw new KinReceiverServiceException(serviceFullPath, false, true);
                }
            }
        }
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_TRANSACTION_COMPLETED.equals(intent.getAction())) {
            String fromAddress = intent.getStringExtra(SERVICE_ARG_FROM_ADDRESS);
            String toAddress = intent.getStringExtra(SERVICE_ARG_TO_ADDRESS);
            int amount = intent.getIntExtra(SERVICE_ARG_AMOUNT, NO_AMOUNT);
            String transactionId = intent.getStringExtra(SERVICE_ARG_TRANSACTION_ID);
            String memo = intent.getStringExtra(SERVICE_ARG_MEMO);
            onTransactionCompleted(fromAddress, toAddress, amount, transactionId, memo);
        }
        if (ACTION_TRANSACTION_FAILED.equals(intent.getAction())) {
            String error = intent.getStringExtra(SERVICE_ARG_ERROR);
            String fromAddress = intent.getStringExtra(SERVICE_ARG_FROM_ADDRESS);
            String toAddress = intent.getStringExtra(SERVICE_ARG_TO_ADDRESS);
            int amount = intent.getIntExtra(SERVICE_ARG_AMOUNT, 0);
            String memo = intent.getStringExtra(SERVICE_ARG_MEMO);
            onTransactionFailed(error, fromAddress, toAddress, amount, memo);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
