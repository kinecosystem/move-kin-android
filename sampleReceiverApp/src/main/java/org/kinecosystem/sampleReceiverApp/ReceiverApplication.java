package org.kinecosystem.sampleReceiverApp;


import android.app.Application;

import org.kinecosystem.sampleReceiverApp.sampleWallet.SampleWallet;

public class ReceiverApplication extends Application {

    private SampleWallet sampleWallet;

    public void setSampleWallet(SampleWallet wallet){
        sampleWallet = wallet;
    }

    public SampleWallet getSampleWallet(){
        return sampleWallet;
    }
}
