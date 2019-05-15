package org.kinecosystem.sampleSenderApp;

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
    protected void initCustomViews() {
        super.initCustomViews();
        findViewById(R.id.discoverAlert).setOnClickListener(v -> {
            AppsDiscoveryAlertDialog dialog = new AppsDiscoveryAlertDialog(SenderMainActivity.this);
            dialog.show();
        });
    }
}
