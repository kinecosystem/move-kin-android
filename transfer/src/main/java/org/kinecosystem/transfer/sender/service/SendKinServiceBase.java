package org.kinecosystem.transfer.sender.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;

import org.kinecosystem.transfer.repositories.KinTransferCallback;

import java.math.BigDecimal;

public abstract class SendKinServiceBase extends Service {

    protected KinTransferCallback transferKinCallback = null;

    public void cancelCallback() { transferKinCallback = null; }

    public abstract KinTransferComplete transferKin(@NonNull final String receiverAppId, @NonNull final String receiverAppName,
                                                    @NonNull final String toAddress, final int amount, @NonNull final String memo) throws KinTransferException;

    public void transferKinAsync(@NonNull final String receiverAppId, @NonNull final String receiverAppName, @NonNull final String toAddress,
                                 final int amount, @NonNull final String memo, @NonNull KinTransferCallback callback){}

    public abstract BigDecimal getCurrentBalance() throws BalanceException;


    public final IBinder binder = new KinTransferServiceBinder();

    public class KinTransferServiceBinder extends Binder {
        public SendKinServiceBase getService() {
            return SendKinServiceBase.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class BalanceException extends Exception {
        public BalanceException(String getBalanceError) {
            super(getBalanceError);
        }

    }



}
