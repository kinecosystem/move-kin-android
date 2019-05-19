package org.kinecosystem.appsdiscovery.receiver.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.kinecosystem.common.base.BasePresenter;
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

    private static class TaskResponse {
        String errorMessage = null;
        @TaskState
        int taskState = TASK_STATE_UNDEFINED;
    }

    private IAccountInfoResponder accountInfoResponder;
    private AccountInfoAsyncTask asyncTask;
    private boolean isPaused = false;
    private TaskResponse taskResponse = new TaskResponse();


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
        onError("Unable to initialize confirmation activity. Incoming intent was null or EXTRA_SOURCE_APP_NAME missing or activity killed");
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

    private void onError(String errorMessage) {
        if (accountInfoResponder != null) {
            accountInfoResponder.respondError(errorMessage);
            if (getView() != null) {
                getView().close();
            }
        }
    }

    private void onTaskComplete(TaskResponse response) {
        taskResponse = response;
        if (!isPaused) {
            checkTaskState();
        }
    }

    private void checkTaskState() {
        IAccountInfoView accountInfoView = getView();
        int taskState = taskResponse.taskState;
        if (taskState == TASK_STATE_SUCCESS) {
            if (accountInfoView != null) {
                accountInfoView.enabledAgreeButton();
            }
            taskResponse.taskState = TASK_STATE_UNDEFINED;
        } else if (taskState == TASK_STATE_FAILURE) {
            onError(taskResponse.errorMessage);
            taskResponse.taskState = TASK_STATE_UNDEFINED;
        }
    }

    private class AccountInfoAsyncTask extends AsyncTask<Void, Void, TaskResponse> {
        private IAccountInfo accountInfo;

        AccountInfoAsyncTask(IAccountInfo accountInfo) {
            this.accountInfo = accountInfo;
        }

        @Override
        protected TaskResponse doInBackground(Void... args) {
            TaskResponse response = new TaskResponse();
            String address = null;

            if (accountInfo != null) {
                address = accountInfo.getPublicAddress();
            }
            if (!TextUtils.isEmpty(address) && accountInfoResponder != null) {
                if (accountInfoResponder.init(address)) {
                    response.taskState = TASK_STATE_SUCCESS;
                }
            } else {
                response.taskState = TASK_STATE_FAILURE;
                response.errorMessage = "Unable to retrieve or initialize responder with kin account public address. " +
                        "Address=" + address + ", accountInfo=" + accountInfo + ", accountInfoResponder=" + accountInfoResponder;
            }
            return response;
        }

        @Override
        protected void onPostExecute(TaskResponse response) {
            super.onPostExecute(response);
            onTaskComplete(response);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            accountInfo = null;
        }
    }
}
