package org.kinecosystem.sampleReceiverApp;

import android.util.Log;

import org.kinecosystem.baseSampleApp.sampleWallet.SampleWallet;
import org.kinecosystem.transfer.receiver.manager.AccountInfoException;
import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener.ActionType;
import org.kinecosystem.transfer.receiver.view.AccountInfoActivityBase;


public class AccountInfoActivity extends AccountInfoActivityBase {

    @Override
    public String getData() throws AccountInfoException {
        SampleWallet sampleWallet = ((ReceiverApplication) getApplicationContext()).getSampleWallet();
        if (sampleWallet == null || !sampleWallet.hasActiveAccount()) {
            String appName = getApplicationInfo().loadLabel(getPackageManager()).toString();
            throw new AccountInfoException(ActionType.LaunchMainActivity, getString(R.string.kin_transfer_account_info_error_relogin_title, appName));
        }
        return sampleWallet.getAccount().getPublicAddress();
    }

    @Override
    public void onTransactionInfoReceived(String senderAppName, String memo, String receiverAppId, String senderAppId) {
        super.onTransactionInfoReceived(senderAppName, memo, receiverAppId, senderAppId);
        //server needs to check transaction got with that memo on the blockchain
        //if found add senderAppName to transaction history
        Log.d("AccountInfoActivity", "Validate memo" + memo + " if valid add to transaction history sender App Name " + senderAppName);
    }
}
