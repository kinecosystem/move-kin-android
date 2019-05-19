package org.kinecosystem.sampleSenderApp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppsDiscoveryAlertDialog;
import org.kinecosystem.baseSampleApp.SampleBaseActivity;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;

public class SenderMainActivity extends SampleBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sample;
    }

    @Override
    protected SampleWallet getSampleWallet() {
        return ((SenderApplication) getApplicationContext()).getSampleWallet();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.discoverAlert).setOnClickListener(v -> {
            AppsDiscoveryAlertDialog dialog = new AppsDiscoveryAlertDialog(SenderMainActivity.this);
            dialog.show();
        });
    }
}
