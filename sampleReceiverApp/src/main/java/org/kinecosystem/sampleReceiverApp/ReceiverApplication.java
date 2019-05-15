package org.kinecosystem.sampleReceiverApp;


import android.app.Application;
import android.util.Log;

import org.kinecosystem.sampleReceiverApp.sampleWallet.SampleWallet;
import org.kinecosystem.sampleReceiverApp.sampleWallet.TransfersLogs;

public class ReceiverApplication extends Application {
    private static final String APP_ID = "recv";
    private SampleWallet sampleWallet;

    @Override
    public void onCreate() {
        super.onCreate();
        sampleWallet = new SampleWallet(this, APP_ID);
        Log.d("###","####ReceiverApplication");
    }

    public SampleWallet getSampleWallet() {
        return sampleWallet;
    }


}
