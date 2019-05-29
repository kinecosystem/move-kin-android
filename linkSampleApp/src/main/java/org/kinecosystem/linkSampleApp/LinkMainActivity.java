package org.kinecosystem.linkSampleApp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.kinecosystem.baseSampleApp.SampleBaseActivity;
import org.kinecosystem.baseSampleApp.SampleBaseApplication;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;
import org.kinecosystem.onewallet.OneWalletActionButton;
import org.kinecosystem.onewallet.OneWalletClient;

import kin.sdk.KinAccount;

public class LinkMainActivity extends SampleBaseActivity {

    private static final int ONE_WALLET_REQUEST_CODE = 50;

    private OneWalletClient oneWalletClient;
    private OneWalletActionButton oneWalletActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleWallet sampleWallet = ((SampleBaseApplication) getApplication()).getSampleWallet();

        oneWalletClient = new OneWalletClient(sampleWallet.getAccount());
        oneWalletActionButton = findViewById(R.id.oneWalletActionButton);

        KinAccount account = sampleWallet.getAccount();
        if (account != null && !TextUtils.isEmpty(account.getPublicAddress())) {
            oneWalletClient.init(this, ONE_WALLET_REQUEST_CODE,
                    account.getPublicAddress(), oneWalletActionButton);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sample_layout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ONE_WALLET_REQUEST_CODE) {
            oneWalletClient.processResult(
                    this, resultCode, data,
                    oneWalletActionButton,
                    findViewById(R.id.oneWalletProgressBar));
        }
    }
}
