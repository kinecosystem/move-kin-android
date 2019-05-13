package org.kinecosystem.sampleSenderApp.sampleWallet;


import android.content.Context;

import kin.sdk.Environment;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;
import kin.sdk.exception.CreateAccountException;

public class SampleWallet {
    private static final String SDK_TEST_NETWORK_URL = "https://horizon-testnet.kininfrastructure.com/";
    private static final String SDK_TEST_NETWORK_ID = "Kin Testnet ; December 2018";

    private KinAccount account;
    private KinClient kinClient;

    public SampleWallet(Context context, String appId) {
        kinClient = new KinClient(context,
                new Environment(SDK_TEST_NETWORK_URL, SDK_TEST_NETWORK_ID), appId);
        if (kinClient.hasAccount()) {
            account = kinClient.getAccount(kinClient.getAccountCount() - 1);
        }
    }

    public boolean hasAccount() {
        return account != null;
    }

    public KinAccount getAccount() {
        return account;
    }

    public KinClient getKinClient() {
        return kinClient;
    }

    public void createAccount(OnBoarding.Callbacks callbacks) {
        try {
            account = kinClient.addAccount();
            OnBoarding onboarding = new OnBoarding();
            onboarding.onBoard(account, callbacks);
        } catch (CreateAccountException e) {
            e.printStackTrace();
        }

    }
}
