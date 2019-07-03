package org.kinecosystem.transfer.receiver.manager;

import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener.ActionType;

public class AccountInfoException extends Exception {

    private ActionType actionType;

    public AccountInfoException(ActionType actionType, String message) {
        super(message);
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }
}