package org.kinecosystem.appsdiscovery.receiver.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.kinecosystem.appsdiscovery.base.BasePresenter;
import org.kinecosystem.appsdiscovery.receiver.manager.AccountInfoManager;
import org.kinecosystem.appsdiscovery.receiver.manager.IAccountInfo;
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

    private AccountInfoManager accountInfoManager;
    private AccountInfoAsyncTask asyncTask;
    private boolean isPaused = false;
    @TaskState
    private int taskState = TASK_STATE_UNDEFINED;
    private Intent intent;

    public AccountInfoPresenter(@NonNull AccountInfoManager accountInfoManager, @NonNull Intent intent) {
        this.accountInfoManager = accountInfoManager;
        this.intent = intent;
    }

    @Override
    public void onAttach(@NotNull IAccountInfoView view) {
        super.onAttach(view);
        if (processIntent(view)) {
            startAccountInfoTask(accountInfoManager.getAccountInfo());
        }
    }

    private boolean processIntent(IAccountInfoView view) {
        if (intent != null && intent.hasExtra(EXTRA_SOURCE_APP_NAME)) {
            String sourceApp = intent.getStringExtra(EXTRA_SOURCE_APP_NAME);
            if (!sourceApp.isEmpty()) {
                view.updateSourceApp(sourceApp);
                return true;
            }
        }
        onError(view);
        return false;
    }

    @Override
    public void agreeClicked() {
        if (accountInfoManager != null) {
            accountInfoManager.respondOk();
            if (getView() != null) {
                getView().close();
            }
        }
    }

    @Override
    public void backButtonPressed() {
        if (accountInfoManager != null) {
            accountInfoManager.respondCancel();
        }
    }

    @Override
    public void closeClicked() {
        if (accountInfoManager != null) {
            accountInfoManager.respondCancel();
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
        if (accountInfoManager != null) {
            accountInfoManager.onDestroy();
            accountInfoManager = null;
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
        asyncTask = new AccountInfoAsyncTask(accountInfo);
        asyncTask.execute();
    }

    private void onError(IAccountInfoView view) {
        if (accountInfoManager != null) {
            accountInfoManager.respondError();
            if (view != null) {
                view.close();
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
            onError(accountInfoView);
            taskState = TASK_STATE_UNDEFINED;
        }
    }

    private class AccountInfoAsyncTask extends AsyncTask<Void, Void, Integer> {
        private IAccountInfo accountInfo;

        public AccountInfoAsyncTask(IAccountInfo accountInfo) {
            this.accountInfo = accountInfo;
        }

        @Override
        protected Integer doInBackground(Void... args) {
            @TaskState int state = TASK_STATE_FAILURE;
            String address = null;
            if (accountInfo != null) {
                address = accountInfo.getPublicAddress();
            }
            if (!address.isEmpty() && accountInfoManager != null) {
                if (accountInfoManager.init(address)) {
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
