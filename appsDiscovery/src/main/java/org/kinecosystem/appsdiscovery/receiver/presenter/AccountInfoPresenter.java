package org.kinecosystem.appsdiscovery.receiver.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.kinecosystem.appsdiscovery.base.BasePresenter;
import org.kinecosystem.appsdiscovery.receiver.manager.IAccountInfo;
import org.kinecosystem.appsdiscovery.receiver.manager.IAccountInfoResponder;
import org.kinecosystem.appsdiscovery.receiver.view.IAccountInfoView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AccountInfoPresenter extends BasePresenter<IAccountInfoView> implements IAccountInfoPresenter {
    private static final String EXTRA_SOURCE_APP_NAME = "EXTRA_SOURCE_APP_NAME";
    private static final int TASK_STATE_UNDEFINED = 0;
    private static final int TASK_STATE_SUCCESS = 10;
    private static final int TASK_STATE_FAILURE = 20;

    @IntDef({TASK_STATE_UNDEFINED, TASK_STATE_SUCCESS, TASK_STATE_FAILURE})
    @Retention(RetentionPolicy.SOURCE)
    @interface TaskState {
    }

    private IAccountInfoResponder accountInfoResponder;
    private AccountInfoAsyncTask asyncTask;
    private boolean isPaused = false;
    @TaskState
    private int taskState = TASK_STATE_UNDEFINED;

    public AccountInfoPresenter() {
    }

    @Override
    public void onAttach(@NonNull IAccountInfoView view) {
        super.onAttach(view);
    }

    @Override
    public void start(IAccountInfoResponder accountInfoResponder, IAccountInfo accountInfo, @NonNull Intent intent) {
        this.accountInfoResponder = accountInfoResponder;
        if (processIntent(intent)) {
            startAccountInfoTask(accountInfo);
        }
    }

    private boolean processIntent(Intent intent) {
        if (intent != null && intent.hasExtra(EXTRA_SOURCE_APP_NAME) && getView() != null) {
            String sourceApp = intent.getStringExtra(EXTRA_SOURCE_APP_NAME);
            if (!sourceApp.isEmpty()) {
                getView().updateSourceApp(sourceApp);
                return true;
            }
        }
        onError();
        return false;
    }

    @Override
    public void agreeClicked() {
        if (accountInfoResponder != null) {
            accountInfoResponder.respondOk();
            if (getView() != null) {
                getView().close();
            }
        }
    }

    @Override
    public void backButtonPressed() {
        if (accountInfoResponder != null) {
            accountInfoResponder.respondCancel();
        }
    }

    @Override
    public void closeClicked() {
        if (accountInfoResponder != null) {
            accountInfoResponder.respondCancel();
            if (getView() != null) {
                getView().close();
            }
        }
    }

    @Override
    public void onDetach() {
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
        if (accountInfoResponder != null) {
            accountInfoResponder.onDestroy();
            accountInfoResponder = null;
        }
        super.onDetach();
    }

    @Override
    public void onResume() {
        isPaused = false;
        checkTaskState();
    }

    @Override
    public void onPause() {
        isPaused = true;
    }


    private void startAccountInfoTask(IAccountInfo accountInfo) {
        if (accountInfo != null) {
            asyncTask = new AccountInfoAsyncTask(accountInfo);
            asyncTask.execute();
        }
    }

    private void onError() {
        if (accountInfoResponder != null) {
            accountInfoResponder.respondError();
            if (getView() != null) {
                getView().close();
            }
        }
    }

    private void onTaskComplete(Integer state) {
        taskState = state;
        if (!isPaused) {
            checkTaskState();
        }
    }

    private void checkTaskState() {
        IAccountInfoView accountInfoView = getView();
        if (taskState == TASK_STATE_SUCCESS) {
            if (accountInfoView != null) {
                accountInfoView.enabledAgreeButton();
            }
            taskState = TASK_STATE_UNDEFINED;
        } else if (taskState == TASK_STATE_FAILURE) {
            onError();
            taskState = TASK_STATE_UNDEFINED;
        }
    }

    private class AccountInfoAsyncTask extends AsyncTask<Void, Void, Integer> {
        private IAccountInfo accountInfo;

        AccountInfoAsyncTask(IAccountInfo accountInfo) {
            this.accountInfo = accountInfo;
        }

        @Override
        protected Integer doInBackground(Void... args) {
            @TaskState int state = TASK_STATE_FAILURE;
            String address = null;
            if (accountInfo != null) {
                address = accountInfo.getPublicAddress();
            }
            if (!TextUtils.isEmpty(address) && accountInfoResponder != null) {
                if (accountInfoResponder.init(address)) {
                    state = TASK_STATE_SUCCESS;
                }
            }
            return state;
        }

        @Override
        protected void onPostExecute(Integer state) {
            super.onPostExecute(state);
            onTaskComplete(state);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            accountInfo = null;
        }
    }
}
