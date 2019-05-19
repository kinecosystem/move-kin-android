package org.kinecosystem.baseSampleApp;

import android.app.Application;

import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;

public abstract class SampleBaseApplication extends Application {
    private SampleWallet sampleWallet;

    public abstract String getAppId();

    @Override
    public void onCreate() {
        super.onCreate();
        sampleWallet = new SampleWallet(this, getAppId());
    }

    public SampleWallet getSampleWallet() {
        return sampleWallet;
    }
}
