package org.kinecosystem.transfer.receiver.view

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import org.kinecosystem.transfer.R
import org.kinecosystem.transfer.receiver.presenter.AccountInfoError
import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener

class AccountInfoErrorDialog(context: Context, dataError: AccountInfoError, listener: IErrorActionClickListener? = null) : AlertDialog(context) {

    init {
        val dialogView = View.inflate(context, R.layout.account_info_error_alert_dialog, null)
        setView(dialogView)
        dialogView.findViewById<TextView>(R.id.title).text = if (dataError.errorMessage.isEmpty()) {
            context.getString(R.string.kin_transfer_account_info_error_dialog_general_title)
        } else {
            dataError.errorMessage
        }
        dialogView.findViewById<TextView>(R.id.okBtn).setOnClickListener {
            dismiss()
            listener?.onOkClicked(dataError.actionType)
        }
    }
}