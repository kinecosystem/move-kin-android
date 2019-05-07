package org.kinecosystem.appsdiscovery.receiver.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.kinecosystem.appsdiscovery.R;
import org.kinecosystem.appsdiscovery.receiver.manager.AccountInfoManager;
import org.kinecosystem.appsdiscovery.receiver.presenter.AccountInfoPresenter;
import org.kinecosystem.appsdiscovery.receiver.presenter.IAccountInfoPresenter;


public class AccountInfoActivity extends AppCompatActivity implements IAccountInfoView {
    private IAccountInfoPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AccountInfoPresenter(new AccountInfoManager(this), this, getIntent());
        setContentView(R.layout.receiver_activity);
        initViews();
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
    public void updateSourceApp(String sourceApp) {
        TextView title = findViewById(R.id.transfer_title);
        final CharSequence destinationApp = getApplicationInfo().loadLabel(getPackageManager());
        title.setText(getString(R.string.receiver_activity_message, sourceApp, destinationApp));
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
