package com.swelly;

import android.support.annotation.NonNull;

import org.kinecosystem.appsdiscovery.sender.service.SendKinServiceBase;

public class SendKinService extends SendKinServiceBase {

    @Override
    public @NonNull
    KinTransferComplete transferKin(@NonNull String toAddress, int amount, @NonNull String memo) throws KinTransferException {
        String transactionId = "myImplswellyTransactionId";
        String address = "myImpladdressSwellyAddress";
        try {
            //long operation
            Thread.sleep(8000);
            //get its own wallet address - from address

            //get transaction id
            if (transactionId.isEmpty()) {
                throw new KinTransferException(address, "swelly cant make the transfer");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new KinTransferComplete(address, transactionId);

    }

    @Override
    public int getCurrentBalance() throws BalanceException {
        int balance = 79;
        //int balance = -79;
        try {
            Thread.sleep(5000);
            if (balance < 0) {
                throw new BalanceException("balance not found! no account");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return balance;
    }
}
