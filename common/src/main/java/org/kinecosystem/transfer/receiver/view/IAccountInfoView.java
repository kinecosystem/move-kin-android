package org.kinecosystem.transfer.receiver.view;

import org.kinecosystem.transfer.base.IBaseView;

public interface IAccountInfoView extends IBaseView {
    void enabledAgreeButton();

    void close();

    void updateSourceApp(String sourceApp);

}
