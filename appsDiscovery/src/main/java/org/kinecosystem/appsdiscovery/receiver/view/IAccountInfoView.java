package org.kinecosystem.appsdiscovery.receiver.view;

import org.kinecosystem.common.base.IBaseView;

public interface IAccountInfoView extends IBaseView {
    void enabledAgreeButton();

    void close();

    void updateSourceApp(String sourceApp);

}
