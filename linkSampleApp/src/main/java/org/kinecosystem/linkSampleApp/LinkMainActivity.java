package org.kinecosystem.linkSampleApp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import org.kinecosystem.baseSampleApp.SampleBaseActivity;
import org.kinecosystem.baseSampleApp.SampleBaseApplication;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;
import org.kinecosystem.linkwallet.LinkingClient;

public class LinkMainActivity extends SampleBaseActivity {

    private static final int REQUEST_CODE = 50;

    private LinkingClient linkingClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleWallet sampleWallet = ((SampleBaseApplication) getApplication()).getSampleWallet();

        Button linkButton = findViewById(R.id.linkButton);
        linkButton.setOnClickListener(v -> {
                    linkingClient = new LinkingClient(sampleWallet.getAccount());
                     Log.d("Linking", "Will link public address: "+sampleWallet.getAccount().getPublicAddress());
                    linkingClient.startLinkingTransactionRequest(this, REQUEST_CODE,
                            "org.kinecosystem.linkSampleApp",
                            sampleWallet.getAccount().getPublicAddress());
                }
        );

    }

    @Override
    protected int getLayoutId() {
        return R.layout.sample_layout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            linkingClient.processLinkingTransactionResult(
                    this, resultCode, data, findViewById(R.id.linkingBar));
        }
    }
}
