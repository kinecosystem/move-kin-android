package org.kinecosystem.sampleSenderApp;


import android.app.Application;

import org.kinecosystem.sampleSenderApp.sampleWallet.SampleWallet;

public class SenderApplication extends Application {

    private SampleWallet sampleWallet;

    public void setSampleWallet(SampleWallet wallet){
        sampleWallet = wallet;
    }

    public SampleWallet getSampleWallet(){
        return sampleWallet;
    }
}
