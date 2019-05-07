package org.kinecosystem.appsdiscovery.receiver.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import static org.kinecosystem.appsdiscovery.receiver.service.ReceiveKinManager.*;

public abstract class ReceiveKinServiceBase extends Service {
    private Handler mainThreadHandler;
    private IBinder kinReceiverBinder;

    /**
     * This method is called once a transaction sending kin to this app (from another ecosystem app)
     * has been completed. Method is called on the UI thread. The service will be stopped 10 seconds
     * after the method completes. If you wish a longer or no delay override #getStopDelayInSeconds
     *
     * @param fromAddress   the public address of the wallet sending the kin
     * @param toAddress     the public address of the wallet receiving the kin
     * @param amount        the amount received in Kin
     * @param transactionId the transaction hash
     * @param memo          the memo in the transaction
     */
    protected abstract void onTransactionCompleted(@NonNull String fromAddress, @NonNull String toAddress, int amount, @NonNull String transactionId, @NonNull String memo);

    /**
     * This method will be called when an attempt to send kin to this app (from another ecosystem app) has failed.
     * Method is called on the UI thread. The service will be stopped 10 seconds
     * after the method completes. If you wish a longer or no delay override #getStopDelayInSeconds
     *
     * @param error       error message
     * @param fromAddress the public address of the wallet attempting to send the kin
     * @param toAddress   the public address of the wallet attempting to receive the kin
     * @param amount      the amount in Kin
     * @param memo        the memo of the transaction
     */
    protected abstract void onTransactionFailed(@NonNull String error, @NonNull String fromAddress, @NonNull String toAddress, int amount, @NonNull String memo);


    /**
     * This method is called after the user has accepted sharing his public address with another app.
     * The method is called on the UI thread. Service will be stopped as soon as the method returns.
     *
     * @return the public address of the user current Kin Account
     */
    protected abstract String getCurrentAccountPublicAddress();


    /**
     * @return the amount of seconds to delay before stopping the service
     */
    protected int getStopDelayInSeconds() {
        return 10;
    }

    public class KinReceiverBinder extends Binder {
        public String getCurrentAccountPublicAddress() {
            return ReceiveKinServiceBase.this.getCurrentAccountPublicAddress();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mainThreadHandler = new Handler(Looper.getMainLooper());
        kinReceiverBinder = new KinReceiverBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
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
        }
        stopWithDelay();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return kinReceiverBinder;
    }

    private void stopWithDelay() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, getStopDelayInSeconds());
    }
}
