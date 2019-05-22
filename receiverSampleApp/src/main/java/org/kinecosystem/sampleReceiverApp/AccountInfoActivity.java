package org.kinecosystem.sampleReceiverApp;

import org.kinecosystem.transfer.receiver.view.AccountInfoActivityBase;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;


public class AccountInfoActivity extends AccountInfoActivityBase {

    @Override
    public String getPublicAddress() {
        SampleWallet sampleWallet = ((ReceiverApplication)getApplicationContext()).getSampleWallet();
        if (sampleWallet != null && sampleWallet.hasActiveAccount() ) {
            return sampleWallet.getAccount().getPublicAddress();
        }
        return null;
    }

}
