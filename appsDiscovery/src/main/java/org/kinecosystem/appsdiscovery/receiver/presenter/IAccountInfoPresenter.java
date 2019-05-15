package org.kinecosystem.appsdiscovery.receiver.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.kinecosystem.common.base.IBasePresenter;
import org.kinecosystem.appsdiscovery.receiver.manager.IAccountInfo;
import org.kinecosystem.appsdiscovery.receiver.manager.IAccountInfoResponder;
import org.kinecosystem.appsdiscovery.receiver.view.IAccountInfoView;

public interface IAccountInfoPresenter extends IBasePresenter<IAccountInfoView> {
    void agreeClicked();

    void backButtonPressed();

    void closeClicked();

    void onResume();

    void onPause();

    void start(IAccountInfoResponder accountInfoResponder, IAccountInfo accountInfo, @NonNull Intent intent);
}
