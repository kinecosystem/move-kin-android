package org.kinecosystem.sampleReceiverApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.kinecosystem.baseSampleApp.SampleBaseActivity;
import org.kinecosystem.baseSampleApp.sampleWallet.OnBoarding;
import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;

import kin.sdk.Balance;
import kin.utils.Request;
import kin.utils.ResultCallback;

public class ReceiverMainActivity extends SampleBaseActivity {

    @Override
    protected SampleWallet getSampleWallet() {
        return ((ReceiverApplication) getApplicationContext()).getSampleWallet();
    }

}
