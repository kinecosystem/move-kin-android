package org.kinecosystem.appsdiscovery.receiver.presenter;

import org.kinecosystem.appsdiscovery.base.IBasePresenter;
import org.kinecosystem.appsdiscovery.receiver.view.IAccountInfoView;

public interface IAccountInfoPresenter extends IBasePresenter<IAccountInfoView> {
    void agreeClicked();

    void backButtonPressed();

    void closeClicked();

    void onResume();

    void onPause();
}
