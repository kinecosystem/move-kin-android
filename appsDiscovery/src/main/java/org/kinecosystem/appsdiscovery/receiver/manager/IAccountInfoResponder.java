package org.kinecosystem.appsdiscovery.receiver.manager;

import android.support.annotation.NonNull;

public interface IAccountInfoResponder {

    boolean init(@NonNull final String publicAddress);

    void respondError(String errorMessage);

    void respondCancel();

    void respondOk();

    void onDestroy();
}
