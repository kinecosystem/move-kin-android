package org.kinecosystem.sampleReceiverApp;


import android.app.Application;

import org.kinecosystem.sampleReceiverApp.sampleWallet.SampleWallet;

public class ReceiverApplication extends Application {
    private static final String APP_ID = "recv";
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
