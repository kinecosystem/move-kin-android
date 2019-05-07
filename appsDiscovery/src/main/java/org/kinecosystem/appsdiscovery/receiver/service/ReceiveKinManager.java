package org.kinecosystem.appsdiscovery.receiver.service;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

public class ReceiveKinManager {
    static final String ACTION_TRANSACTION_COMPLETED = "org.kinecosystem.KinReceiverTransactionCompleted";
    static final String ACTION_TRANSACTION_FAILED = "org.kinecosystem.KinReceiverTransactionFailed";
    static final String SERVICE_ARG_FROM_ADDRESS = "fromAddress";
    static final String SERVICE_ARG_TO_ADDRESS = "toAddress";
    static final String SERVICE_ARG_AMOUNT = "amount";
    static final String SERVICE_ARG_TRANSACTION_ID = "transactionId";
    static final String SERVICE_ARG_MEMO = "memo";
    static final String SERVICE_ARG_ERROR = "error";
    static final int NO_AMOUNT = 0;
    static final String SERVICE_NAME = "ReceiveKinService";

    public static void notifyTransactionCompleted(@NonNull Context context, @NonNull String receiverPackageName,
                                                  @NonNull String fromAddress, @NonNull String toAddress,
                                                  int amount, @NonNull String transactionId,
                                                  @NonNull String memo) throws KinReceiverServiceException {
        Intent intent = getTransactionResultIntent(context, receiverPackageName, true);
        intent.putExtra(SERVICE_ARG_FROM_ADDRESS, fromAddress);
        intent.putExtra(SERVICE_ARG_TO_ADDRESS, toAddress);
        intent.putExtra(SERVICE_ARG_AMOUNT, amount);
        intent.putExtra(SERVICE_ARG_TRANSACTION_ID, transactionId);
        intent.putExtra(SERVICE_ARG_MEMO, memo);
        context.startService(intent);
    }

    public static void notifyTransactionFailed(@NonNull Context context, @NonNull String receiverPackageName,
                                               @NonNull String error, @NonNull String fromAddress,
                                               @NonNull String toAddress, int amount,
                                               @NonNull String memo) throws KinReceiverServiceException {
        Intent intent = getTransactionResultIntent(context, receiverPackageName, false);
        intent.putExtra(SERVICE_ARG_ERROR, error);
        intent.putExtra(SERVICE_ARG_FROM_ADDRESS, fromAddress);
        intent.putExtra(SERVICE_ARG_TO_ADDRESS, toAddress);
        intent.putExtra(SERVICE_ARG_AMOUNT, amount);
        intent.putExtra(SERVICE_ARG_MEMO, memo);
        context.startService(intent);
    }

    private static Intent getTransactionResultIntent(Context context, String receiverPackageName,
                                                     final Boolean isCompleted) throws KinReceiverServiceException {
        String action = isCompleted ? ACTION_TRANSACTION_COMPLETED : ACTION_TRANSACTION_FAILED;
        Intent intent = new Intent();
        intent.setAction(action);
        String serviceFullPath = receiverPackageName + "." + SERVICE_NAME;
        intent.setComponent(new ComponentName(receiverPackageName, serviceFullPath));
        intent.setPackage(receiverPackageName);
        final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(intent, 0);
        if (resolveInfos.isEmpty()) {
            throw new KinReceiverServiceException(serviceFullPath, "service not found");
        } else {
            for (ResolveInfo info : resolveInfos) {
                if (!info.serviceInfo.exported) {
                    throw new KinReceiverServiceException(serviceFullPath, "service not public. Make sure it is exported on AndroidManifest.xml");
                }
            }
        }
        return intent;
    }

}
