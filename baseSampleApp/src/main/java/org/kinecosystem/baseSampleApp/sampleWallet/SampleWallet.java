package org.kinecosystem.baseSampleApp.sampleWallet;


import android.content.Context;
import android.content.SharedPreferences;

import kin.sdk.Environment;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;
import kin.sdk.exception.CreateAccountException;

public class SampleWallet {
    private static final String SDK_TEST_NETWORK_URL = "https://horizon-testnet.kininfrastructure.com/";
    private static final String SDK_TEST_NETWORK_ID = "Kin Testnet ; December 2018";

    private KinAccount account;
    private KinClient kinClient;
    private SharedPreferences sharedPreferences;

    public SampleWallet(Context context, String appId) {
        sharedPreferences = context.getSharedPreferences("SampleReceiver", Context.MODE_PRIVATE);
        kinClient = new KinClient(context,
                new Environment(SDK_TEST_NETWORK_URL, SDK_TEST_NETWORK_ID), appId);
        if (kinClient.hasAccount()) {
            account = kinClient.getAccount(kinClient.getAccountCount() - 1);
        }
    }

    public boolean hasActiveAccount() {
        return account != null && isAccountCreated();
    }

    public KinAccount getAccount() {
        return account;
    }

    public void createAccount(OnBoarding.Callbacks callbacks) {
        try {
            account = kinClient.addAccount();
            OnBoarding onboarding = new OnBoarding();
            onboarding.onBoard(account, new OnBoarding.Callbacks() {
                @Override
                public void onSuccess() {
                    setAccountCreated();
                    callbacks.onSuccess();
                }

                @Override
                public void onFailure(Exception e) {
                    callbacks.onFailure(e);
                }
            });
        } catch (CreateAccountException e) {
            e.printStackTrace();
        }

    }

    private boolean isAccountCreated() {
        return sharedPreferences.getBoolean("account_created", false);
    }

    private void setAccountCreated() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("account_created", true);
        editor.apply();
    }
}
