package org.kinecosystem.sampleReceiverApp;

import org.kinecosystem.appsdiscovery.receiver.view.AccountInfoActivityBase;


public class AccountInfoActivity extends AccountInfoActivityBase {

    @Override
    public String getPublicAddress() {
        return ((ReceiverApplication)getApplicationContext())
                .getSampleWallet().account.getPublicAddress();
    }

}
