package org.kinecosystem.appstransfer.view.customview

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import org.kinecosystem.appstransfer.R

class TransferErrorDialog(context: Context, errorType:ErrorType, private val receiverAppName:String) : AlertDialog(context) {

    enum class ErrorType{
        ConnectionFailed,
        ConnectionError
    }

    init {
        val dialogView = View.inflate(context, R.layout.transfer_error_alert_dialog, null)
        setView(dialogView)
        dialogView.findViewById<TextView>(R.id.title).text = toErrorTitle(errorType)
        dialogView.findViewById<TextView>(R.id.okBtn).setOnClickListener {
            dismiss()
        }
    }

    private fun toErrorTitle(errorType:ErrorType):String{
        return when(errorType){
            ErrorType.ConnectionFailed -> context.resources.getString(R.string.apps_transfer_connection_failed, receiverAppName)
            ErrorType.ConnectionError ->  context.resources.getString(R.string.apps_transfer_connection_error, receiverAppName)
        }
    }
}