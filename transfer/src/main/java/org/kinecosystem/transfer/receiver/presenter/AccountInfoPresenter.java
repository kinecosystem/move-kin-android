package org.kinecosystem.transfer.receiver.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.kinecosystem.common.base.BasePresenter;
import org.kinecosystem.transfer.TransferIntent;
import org.kinecosystem.transfer.receiver.manager.AccountInfoException;
import org.kinecosystem.transfer.receiver.manager.IAccountInfo;
import org.kinecosystem.transfer.receiver.manager.IAccountInfoResponder;
import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener.ActionType;
import org.kinecosystem.transfer.receiver.view.IAccountInfoView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AccountInfoPresenter extends BasePresenter<IAccountInfoView> implements IAccountInfoPresenter {
    private static final int TASK_STATE_UNDEFINED = 0;
    private static final int TASK_STATE_SUCCESS = 10;
    private static final int TASK_STATE_FAILURE = 20;
    private static final int TASK_STATE_ACCOUNT_DATA_EXCEPTION = 30;

    @IntDef({TASK_STATE_UNDEFINED, TASK_STATE_SUCCESS, TASK_STATE_FAILURE})
    @Retention(RetentionPolicy.SOURCE)
    @interface TaskState {
    }

    private static class TaskResponse {
        ActionType errorType = ActionType.None;
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
    public void start(IAccountInfoResponder accountInfoResponder, IAccountInfo accountInfo, @NonNull Intent intent) {
        this.accountInfoResponder = accountInfoResponder;
        if (processIntent(intent)) {
            startAccountInfoTask(accountInfo);
        }
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

    @Override
    public void onAttach(@NonNull IAccountInfoView view) {
        super.onAttach(view);
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
        } else if (taskState == TASK_STATE_ACCOUNT_DATA_EXCEPTION) {
            onDataError(taskResponse.errorType, taskResponse.errorMessage);
            taskResponse.taskState = TASK_STATE_UNDEFINED;
        }
    }

    private void onDataError(ActionType actionType, String errorMessage) {
        if (getView() != null) {
            getView().showErrorDialog(new AccountInfoError(actionType, errorMessage), new IErrorActionClickListener() {
                @Override
                public void onOkClicked(@NotNull ActionType errorType) {
                    if (errorType.equals(ActionType.LaunchMainActivity)) {
                        getView().lunchMainActivity();
                    }
                    closeClicked();
                }
            });
        }
    }

    private boolean processIntent(Intent intent) {
        if (intent != null && intent.hasExtra(TransferIntent.EXTRA_SENDER_APP_NAME)
                && intent.hasExtra(TransferIntent.EXTRA_MEMO)
                && intent.hasExtra(TransferIntent.EXTRA_SENDER_APP_NAME)
                && intent.hasExtra(TransferIntent.EXTRA_RECEIVER_APP_ID)
                && getView() != null) {
            String senderAppName = intent.getStringExtra(TransferIntent.EXTRA_SENDER_APP_NAME);
            String memo = intent.getStringExtra(TransferIntent.EXTRA_MEMO);
            String senderAppId = intent.getStringExtra(TransferIntent.EXTRA_SENDER_APP_ID);
            String receiverAppId = intent.getStringExtra(TransferIntent.EXTRA_RECEIVER_APP_ID);

            if (!senderAppName.isEmpty() && !memo.isEmpty() && !senderAppId.isEmpty() && !receiverAppId.isEmpty()) {
                getView().updateTitle(senderAppName);
                getView().updateTransactionInfo(senderAppId, senderAppName, receiverAppId, formatFullMemo(receiverAppId, memo));
                return true;
            }
        }
        onError("Unable to initialize confirmation activity. Incoming intent was null or EXTRA_SENDER_APP_NAME/EXTRA_MEMO/EXTRA_SENDER_APP_NAME/EXTRA_RECEIVER_APP_ID missing or activity killed");
        return false;
    }

    private String formatFullMemo(String receiverAppId, String memo) {
        return "1-" + receiverAppId + "-" + memo;
    }

    private class AccountInfoAsyncTask extends AsyncTask<Void, Void, TaskResponse> {
        private IAccountInfo accountInfo;

        AccountInfoAsyncTask(IAccountInfo accountInfo) {
            this.accountInfo = accountInfo;
        }

        @Override
        protected TaskResponse doInBackground(Void... args) {
            TaskResponse response = new TaskResponse();
            String info = null;

            if (accountInfo != null) {
                try {
                    info = accountInfo.getData();
                    if (!TextUtils.isEmpty(info) && accountInfoResponder != null) {
                        if (accountInfoResponder.init(info)) {
                            response.taskState = TASK_STATE_SUCCESS;
                        }
                    } else {
                        response.errorType = ActionType.None;
                        response.taskState = TASK_STATE_FAILURE;
                        response.errorMessage = "Unable to retrieve or initialize responder with kin account data= " +
                                info + ", accountInfo=" + accountInfo + ", accountInfoResponder=" + accountInfoResponder;
                    }
                } catch (AccountInfoException e) {
                    response.errorType = e.getActionType();
                    response.errorMessage = e.getMessage();
                    response.taskState = TASK_STATE_ACCOUNT_DATA_EXCEPTION;
                }
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
