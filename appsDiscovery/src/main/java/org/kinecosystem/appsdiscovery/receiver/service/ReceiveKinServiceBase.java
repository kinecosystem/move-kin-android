package org.kinecosystem.appsdiscovery.receiver.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import static org.kinecosystem.appsdiscovery.receiver.service.ReceiveKinNotifier.*;

public abstract class ReceiveKinServiceBase extends Service {
    private Handler mainThreadHandler;

    /**
     * This method is called once a transaction sending kin to this app (from another ecosystem app)
     * has been completed.
     * The method is called on the UI thread. The service will be stopped 10 seconds
     * after the method completes so if you wish to run a background thread within the method,
     * then you have 10 seconds to complete it. If you need more or less that 10 seconds you can
     * also configure the delay by overriding {@link ReceiveKinServiceBase#getStopDelayInSeconds()}
     *
     * @param fromAddress   the public address of the wallet sending the kin
     * @param senderAppName the sender application name
     * @param toAddress     the public address of the wallet receiving the kin
     * @param amount        the amount received in Kin
     * @param transactionId the transaction hash
     * @param memo          the full transaction memo (including the 1-[sourceappid] prefix)
     */
    protected abstract void onTransactionCompleted(@NonNull String fromAddress, @NonNull String senderAppName, @NonNull String toAddress,
                                                   int amount, @NonNull String transactionId, @NonNull String memo);

    /**
     * This method will be called when an attempt to send kin to this app (from another ecosystem app) has failed.
     * The method is called on the UI thread. The service will be stopped 10 seconds
     * after the method completes so if you wish to run a background thread within the method,
     * then you have 10 seconds to complete it. If you need more or less that 10 seconds you can
     * also configure the delay by overriding {@link ReceiveKinServiceBase#getStopDelayInSeconds()}
     *
     * @param error       error message
     * @param fromAddress the public address of the wallet attempting to send the kin
     * @param senderAppName the sender application name
     * @param toAddress   the public address of the wallet attempting to receive the kin
     * @param amount      the amount in Kin
     * @param memo        the full transaction memo (including the 1-[sourceappid] prefix)
     */
    protected abstract void onTransactionFailed(@NonNull String error, @NonNull String fromAddress, @NonNull String senderAppName,
                                                @NonNull String toAddress, int amount, @NonNull String memo);


    /**
     * This method can be used to configure the number of seconds to delay stopping the service after
     * {@link ReceiveKinServiceBase#onTransactionCompleted(String, String, int, String, String)}
     * {@link ReceiveKinServiceBase#onTransactionFailed(String, String, String, int, String)}
     * methods have completed. If you do not need to run a background thread in the implementation of
     * onTransactionCompleted/onTransactionFailed, you can override this method and return 0.
     * @return the amount of seconds to delay before stopping the service
     */
    protected int getStopDelayInSeconds() {
        return 10;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            if (ACTION_TRANSACTION_COMPLETED.equals(intent.getAction())) {
                String fromAddress = intent.getStringExtra(SERVICE_ARG_FROM_ADDRESS);
                String fromApp = intent.getStringExtra(SERVICE_ARG_FROM_APP);
                String toAddress = intent.getStringExtra(SERVICE_ARG_TO_ADDRESS);
                int amount = intent.getIntExtra(SERVICE_ARG_AMOUNT, NO_AMOUNT);
                String transactionId = intent.getStringExtra(SERVICE_ARG_TRANSACTION_ID);
                String memo = intent.getStringExtra(SERVICE_ARG_MEMO);
                onTransactionCompleted(fromAddress, fromApp, toAddress, amount, transactionId, memo);
            }
            if (ACTION_TRANSACTION_FAILED.equals(intent.getAction())) {
                String error = intent.getStringExtra(SERVICE_ARG_ERROR);
                String fromAddress = intent.getStringExtra(SERVICE_ARG_FROM_ADDRESS);
                String fromApp = intent.getStringExtra(SERVICE_ARG_FROM_APP);
                String toAddress = intent.getStringExtra(SERVICE_ARG_TO_ADDRESS);
                int amount = intent.getIntExtra(SERVICE_ARG_AMOUNT, 0);
                String memo = intent.getStringExtra(SERVICE_ARG_MEMO);
                onTransactionFailed(error, fromAddress, fromApp, toAddress, amount, memo);
            }
        }
        stopWithDelay();
        return START_NOT_STICKY;
    }

    private void stopWithDelay() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, getStopDelayInSeconds());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
