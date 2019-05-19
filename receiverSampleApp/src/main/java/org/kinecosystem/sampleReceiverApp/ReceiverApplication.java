package org.kinecosystem.sampleReceiverApp;


import android.app.Application;

import org.kinecosystem.baseSampleApp.SampleBaseApplication;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;

public class ReceiverApplication extends SampleBaseApplication {
    private static final String APP_ID = "recv";

    @Override
    public String getAppId() {
        return APP_ID;
    }
}
