package org.kinecosystem.sampleReceiverApp;

import android.util.Log;

import org.kinecosystem.transfer.receiver.view.AccountInfoActivityBase;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;


public class AccountInfoActivity extends AccountInfoActivityBase {

    @Override
    public String getData() {
        SampleWallet sampleWallet = ((ReceiverApplication)getApplicationContext()).getSampleWallet();
        if (sampleWallet != null && sampleWallet.hasActiveAccount() ) {
            return sampleWallet.getAccount().getPublicAddress();
        }
        return null;
    }

    @Override
    public void onTransactionInfoReceived(String senderAppName, String memo, String receiverAppId, String senderAppId) {
        super.onTransactionInfoReceived(senderAppName, memo, receiverAppId, senderAppId);
        //server needs to check transaction got with that memo on the blockchain
        //if found add senderAppName to transaction history
        Log.d("AccountInfoActivity", "Validate memo" + memo + " if valid add to transaction history sender App Name " + senderAppName);
    }
}
