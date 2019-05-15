package org.kinecosystem.sampleReceiverApp;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.kinecosystem.sampleReceiverApp.sampleWallet.OnBoarding;
import org.kinecosystem.sampleReceiverApp.sampleWallet.SampleWallet;
import org.kinecosystem.sampleReceiverApp.sampleWallet.TransfersLogs;

import kin.sdk.Balance;
import kin.utils.Request;
import kin.utils.ResultCallback;

public class ReceiverMainActivity extends AppCompatActivity {
    private boolean activityCreated = false;
    private TextView transferLogsView;
    private TransfersLogs transfersLogs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityCreated = true;
        initAccountAndViews();
    }

    public void clearHistory(View v) {
        transfersLogs.clear();
        updateTransfersView();
    }

    private SampleWallet getSampleWallet() {
        return ((ReceiverApplication) getApplicationContext()).getSampleWallet();
    }

    private void initAccountAndViews() {
        transferLogsView = findViewById(R.id.history);
        transferLogsView.setMovementMethod(new ScrollingMovementMethod());
        transfersLogs = new TransfersLogs(PreferenceManager.getDefaultSharedPreferences(this));
        SampleWallet sampleWallet = getSampleWallet();
        TextView addressView = findViewById((R.id.publicAddressView));
        if (sampleWallet.hasActiveAccount()) {
            String text = getString(R.string.public_address, sampleWallet.getAccount().getPublicAddress());
            addressView.setText(text);
            initBalance();
        } else {
            addressView.setText(R.string.create_wallet);
            sampleWallet.createAccount(new OnBoarding.Callbacks() {
                @Override
                public void onSuccess() {
                    if (activityCreated) {
                        String text = getString(R.string.public_address, sampleWallet.getAccount().getPublicAddress());
                        addressView.setText(text);
                        initBalance();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    if (activityCreated) {
                        addressView.setText(R.string.create_wallet_error);
                    }
                }
            });
        }
        updateTransfersView();
    }

    private void updateTransfersView() {
        transferLogsView.setText(transfersLogs.getTransfers());
    }

    private void initBalance() {
        TextView balanceView = findViewById(R.id.balanceView);
        updateBalance(balanceView);
        balanceView.setOnClickListener(v -> updateBalance((TextView) v));

    }

    private void updateBalance(TextView balanceView) {
        balanceView.setText(R.string.update_balance);
        Request<Balance> balanceRequest = getSampleWallet().getAccount().getBalance();
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
