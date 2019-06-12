package org.kinecosystem.appstransfer.view.customview

import android.content.Context
import org.kinecosystem.appstransfer.R
import org.kinecosystem.appstransfer.view.AppsTransferActivity
import org.kinecosystem.common.utils.getApplicationName
import org.kinecosystem.transfer.sender.view.KinTransferDialogBase

class AppsTransferDialog(context: Context) : KinTransferDialogBase(context) {

    override fun getTitle(): String {
        return context.resources.getString(R.string.apps_transfer_use_kin_across)
    }

    override fun getSubtitle(): String {
        return context.resources.getString(R.string.apps_transfer_alert_subtitle, context.getApplicationName())
    }

    override fun onActionClicked() {
        context.startActivity(AppsTransferActivity.getIntent(context))
    }

    override fun getPositiveButtonText(): String {
        return context.resources.getString(R.string.apps_transfer_try_it)
    }

    override fun getNegativeButtonText(): String {
        return context.resources.getString(R.string.apps_transfer_later)
    }
}