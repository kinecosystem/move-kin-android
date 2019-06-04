package org.kinecosystem.transfer.sender.view

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import org.kinecosystem.transfer.R

abstract class KinTransferDialogBase(context: Context) : AlertDialog(context) {

    abstract fun onActionClicked()
    abstract fun getTitle():String
    abstract fun getSubtitle():String

    open fun getPositiveButtonText():String{
        return context.resources.getString(R.string.ok)
    }

    open fun getNegativeButtonText():String{
        return context.resources.getString(R.string.cancel)
    }

    init {
        val dialogView = View.inflate(context, R.layout.kin_transfer_dialog, null)
        dialogView.findViewById<TextView>(R.id.title).text = getTitle()
        dialogView.findViewById<TextView>(R.id.subtitle).text = getSubtitle()
        dialogView.findViewById<TextView>(R.id.positiveBtn).text = getPositiveButtonText()
        dialogView.findViewById<TextView>(R.id.negativeBtn).text = getNegativeButtonText()

        setView(dialogView)

        dialogView.findViewById<TextView>(R.id.positiveBtn).setOnClickListener {
            onActionClicked()
            dismiss()
        }
        dialogView.findViewById<TextView>(R.id.negativeBtn).setOnClickListener {
            dismiss()
        }
    }
}