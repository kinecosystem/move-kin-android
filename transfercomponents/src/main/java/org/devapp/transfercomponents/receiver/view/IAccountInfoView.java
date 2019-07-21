package org.devapp.transfercomponents.receiver.view;

import org.kinecosystem.common.base.IBaseView;
import org.devapp.transfercomponents.receiver.presenter.AccountInfoError;
import org.devapp.transfercomponents.receiver.presenter.IErrorActionClickListener;

public interface IAccountInfoView extends IBaseView {
    void enabledAgreeButton();

    void close();

    void updateTransactionInfo(String senderAppId, String senderAppName, String receiverAppId, String memo);

    void showErrorDialog(AccountInfoError dataError, IErrorActionClickListener listener);

    void updateTitle(String senderAppName);

    void lunchMainActivity();
}
