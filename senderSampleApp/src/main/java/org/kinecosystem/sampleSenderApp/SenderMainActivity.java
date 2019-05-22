package org.kinecosystem.sampleSenderApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppsDiscoveryAlertDialog;
import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppsDiscoveryButton;
import org.kinecosystem.baseSampleApp.SampleBaseActivity;

public class SenderMainActivity extends SampleBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sample;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AppsDiscoveryButton btn1 = findViewById(R.id.discoverBtnDark);
        final AppsDiscoveryButton btn2 = findViewById(R.id.discoverBtnLight);

        findViewById(R.id.discoverAlert).setOnClickListener(v -> {
            btn1.setEnabled(!btn1.isEnabled());
            btn2.setEnabled(!btn2.isEnabled());

            // btn.showIcon(btn.isEnabled());
           // AppsDiscoveryAlertDialog dialog = new AppsDiscoveryAlertDialog(SenderMainActivity.this);
           // dialog.show();
        });
    }
}
