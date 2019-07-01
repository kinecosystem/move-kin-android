package org.kinecosystem.transfer.receiver.view;

import org.kinecosystem.common.base.IBaseView;

public interface IAccountInfoView extends IBaseView {
    void enabledAgreeButton();

    void close();

    void onTransactionInfoReceived(String senderAppName, String memo, String senderAppId,  String receiverAppId);
}
