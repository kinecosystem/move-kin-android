package org.kinecosystem.sampleSenderApp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppsDiscoveryAlertDialog;
import org.kinecosystem.baseSampleApp.SampleBaseActivity;

public class SenderMainActivity extends SampleBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sample;
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
