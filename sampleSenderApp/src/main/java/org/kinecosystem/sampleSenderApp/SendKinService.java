package org.kinecosystem.sampleSenderApp;

import android.support.annotation.NonNull;

import org.kinecosystem.appsdiscovery.sender.service.SendKinServiceBase;

public class SendKinService extends SendKinServiceBase {

    @Override
    public @NonNull
    KinTransferComplete transferKin(@NonNull String toAddress, int amount, @NonNull String memo) throws KinTransferException {
        String transactionId = "atransactionid";
        String address = "apublicaddress";
        try {
            //long operation
            Thread.sleep(8000);
            //get its own wallet address - from address

            //get transaction id
            if (transactionId.isEmpty()) {
                throw new KinTransferException(address, "Failure while trying to transfer "+amount+" Kin to "
                        +toAddress+". Empty transaction id");
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
