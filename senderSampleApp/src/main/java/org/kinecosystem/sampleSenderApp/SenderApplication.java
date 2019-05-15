package org.kinecosystem.sampleSenderApp;


import android.app.Application;

import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;


public class SenderApplication extends Application {
    private static final String APP_ID = "sndr";
    private SampleWallet sampleWallet;

    @Override
    public void onCreate() {
        super.onCreate();
        sampleWallet = new SampleWallet(this, APP_ID);
    }

    public SampleWallet getSampleWallet(){
        return sampleWallet;
    }
}
