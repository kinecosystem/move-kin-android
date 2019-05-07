package org.kinecosystem.sampleSenderApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppsDiscoveryAlertDialog;

public class SampleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        findViewById(R.id.discoverAlert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppsDiscoveryAlertDialog dialog = new AppsDiscoveryAlertDialog(SampleActivity.this);
                dialog.show();
            }
        });
    }
}
