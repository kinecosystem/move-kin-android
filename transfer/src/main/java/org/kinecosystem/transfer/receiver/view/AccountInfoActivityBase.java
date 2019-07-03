package org.kinecosystem.transfer.receiver.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.kinecosystem.common.utils.GeneralUtils;
import org.kinecosystem.transfer.R;
import org.kinecosystem.transfer.receiver.manager.AccountInfoResponder;
import org.kinecosystem.transfer.receiver.manager.IAccountInfo;
import org.kinecosystem.transfer.receiver.presenter.AccountInfoError;
import org.kinecosystem.transfer.receiver.presenter.AccountInfoPresenter;
import org.kinecosystem.transfer.receiver.presenter.IAccountInfoPresenter;
import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener;

public abstract class AccountInfoActivityBase extends AppCompatActivity implements IAccountInfoView, IAccountInfo {
    private IAccountInfoPresenter presenter;
    private String receiverAppName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiverAppName = getApplicationInfo().loadLabel(getPackageManager()).toString();
        setContentView(R.layout.receiver_approval_activity);
        initViews();
        presenter = new AccountInfoPresenter();
        presenter.onAttach(this);
        presenter.start(new AccountInfoResponder(this), this,
                getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onBackPressed() {
        if (presenter != null) {
            presenter.backButtonPressed();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.onDetach();
            presenter = null;
        }
        super.onDestroy();
    }

    @Override
    public void enabledAgreeButton() {
        findViewById(R.id.confirm_button).setEnabled(true);
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void updateTransactionInfo(String senderAppId, String senderAppName, String receiverAppId, String memo) {
        //could be overwritten on the receiver side in order to update transaction history
    }

    @Override
    public void updateTitle(String senderAppName) {
        TextView title = findViewById(R.id.transfer_title);
        String receiverAppName = getApplicationInfo().loadLabel(getPackageManager()).toString();
        title.setText(getString(R.string.receiver_activity_message, senderAppName, receiverAppName));
    }

    @Override
    public void showErrorDialog(AccountInfoError dataError, IErrorActionClickListener listener) {
        AccountInfoErrorDialog dialog = new AccountInfoErrorDialog(this, dataError, listener);
        dialog.show();
    }

    @Override
    public void lunchMainActivity() {
        GeneralUtils.launchMainActivity(this);
    }

    protected void initViews() {
        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    presenter.agreeClicked();
                }
            }
        });

        findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    presenter.closeClicked();
                }
            }
        });
    }
}
