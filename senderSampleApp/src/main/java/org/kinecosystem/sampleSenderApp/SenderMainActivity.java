package org.kinecosystem.sampleSenderApp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.kinecosystem.appsdiscovery.view.customView.AppsDiscoveryAlertDialog;
import org.kinecosystem.appstransfer.view.customview.AppsTransferDialog;
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

        findViewById(R.id.transferAlertBtn).setOnClickListener(v -> {
            AppsTransferDialog dialog = new AppsTransferDialog(SenderMainActivity.this);
            dialog.show();
        });



    }
}
