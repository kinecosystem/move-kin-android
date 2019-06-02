package org.kinecosystem.linkSampleApp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.kinecosystem.baseSampleApp.SampleBaseActivity;
import org.kinecosystem.baseSampleApp.SampleBaseApplication;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;
import org.kinecosystem.onewallet.OneWalletClient;

import kin.sdk.KinAccount;

public class LinkMainActivity extends SampleBaseActivity {

    private static final int ONE_WALLET_REQUEST_CODE = 50;

    private OneWalletClient oneWalletClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleWallet sampleWallet = ((SampleBaseApplication) getApplication()).getSampleWallet();
        KinAccount account = sampleWallet.getAccount();
        if (account != null && !TextUtils.isEmpty(account.getPublicAddress())) {
            oneWalletClient = new OneWalletClient();
            oneWalletClient.onActivityCreated(this, account, ONE_WALLET_REQUEST_CODE,
                    R.id.oneWalletActionButton, R.id.oneWalletProgressBar);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sample_layout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (oneWalletClient != null && requestCode == ONE_WALLET_REQUEST_CODE) {
            oneWalletClient.onActivityResult(
                    this, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oneWalletClient.onActivityDestroyed();
        oneWalletClient = null;
    }
}
