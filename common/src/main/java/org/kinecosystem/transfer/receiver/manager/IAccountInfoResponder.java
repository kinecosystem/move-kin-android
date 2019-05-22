package org.kinecosystem.transfer.receiver.manager;

import android.support.annotation.NonNull;

public interface IAccountInfoResponder {

    boolean init(@NonNull final String accountInfo);

    void respondError(String errorMessage);

    void respondCancel();

    void respondOk();

    void onDestroy();
}
