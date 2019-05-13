package org.kinecosystem.sampleSenderApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppsDiscoveryAlertDialog;
import org.kinecosystem.sampleSenderApp.sampleWallet.OnBoarding;
import org.kinecosystem.sampleSenderApp.sampleWallet.SampleWallet;

import kin.sdk.Balance;
import kin.utils.Request;
import kin.utils.ResultCallback;

public class SenderMainActivity extends AppCompatActivity {
    private static final String APP_ID = "send";
    SampleWallet sampleWallet;
    boolean activityCreated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        findViewById(R.id.discoverAlert).setOnClickListener(v -> {
            AppsDiscoveryAlertDialog dialog = new AppsDiscoveryAlertDialog(SenderMainActivity.this);
            dialog.show();
        });

        sampleWallet = new SampleWallet(this, APP_ID);
        ((SenderApplication) getApplicationContext()).setSampleWallet(sampleWallet);

        activityCreated = true;

        initAccountAndViews();
    }


    private void initAccountAndViews() {

        TextView paddressView = findViewById((R.id.publicAddressView));
        if (sampleWallet.hasAccount()) {
            String text = getString(R.string.public_address, sampleWallet.getAccount().getPublicAddress());
            paddressView.setText(text);
            initBalance();
        } else {
            paddressView.setText(R.string.create_wallet);
            sampleWallet.createAccount(new OnBoarding.Callbacks() {
                @Override
                public void onSuccess() {
                    if (activityCreated) {
                        String text = getString(R.string.public_address, sampleWallet.getAccount().getPublicAddress());
                        paddressView.setText(text);
                        initBalance();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    if (activityCreated) {
                        paddressView.setText(R.string.create_wallet_error);
                    }
                }
            });
        }
    }


    private void initBalance() {
        TextView balanceView = findViewById(R.id.balanceView);
        updateBalance(balanceView);
        balanceView.setOnClickListener(v -> updateBalance((TextView) v));

    }

    private void updateBalance(TextView balanceView) {
        balanceView.setText(R.string.update_balance);
        Request<Balance> balanceRequest = sampleWallet.getAccount().getBalance();
        balanceRequest.run(new ResultCallback<Balance>() {
            @Override
            public void onResult(Balance result) {
                if (activityCreated) {
                    String text = getString(R.string.balance, result.value(0));
                    balanceView.setText(text);
                }
            }

            @Override
            public void onError(Exception e) {
                if (activityCreated) {
                    balanceView.setText(R.string.balance_error);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityCreated = false;
    }
}
