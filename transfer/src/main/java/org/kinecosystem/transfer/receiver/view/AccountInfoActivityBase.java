package org.kinecosystem.transfer.receiver.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.kinecosystem.transfer.R;
import org.kinecosystem.transfer.receiver.manager.AccountInfoResponder;
import org.kinecosystem.transfer.receiver.manager.IAccountInfo;
import org.kinecosystem.transfer.receiver.presenter.AccountInfoPresenter;
import org.kinecosystem.transfer.receiver.presenter.IAccountInfoPresenter;

public abstract class AccountInfoActivityBase extends AppCompatActivity implements IAccountInfoView, IAccountInfo {
    private IAccountInfoPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiver_approval_activity);
        initViews();
        presenter = new AccountInfoPresenter();
        presenter.onAttach(this);
        presenter.start(new AccountInfoResponder(this),  this,
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
    public void onTransactionInfoReceived(String senderAppName, String memo, String receiverAppId , String senderAppId) {
        TextView title = findViewById(R.id.transfer_title);
        final CharSequence destinationApp = getApplicationInfo().loadLabel(getPackageManager());
        title.setText(getString(R.string.receiver_activity_message, senderAppName, destinationApp));
    }

    private String formatFullMemo(String memo, String receiverAppId){
        return "1-" + receiverAppId + "-" + memo;
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
