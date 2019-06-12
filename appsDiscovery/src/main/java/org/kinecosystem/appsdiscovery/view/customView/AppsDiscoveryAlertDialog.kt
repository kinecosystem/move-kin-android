package org.kinecosystem.appsdiscovery.view.customView

import android.content.Context
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.view.AppsDiscoveryActivity
import org.kinecosystem.transfer.sender.view.KinTransferDialogBase

class AppsDiscoveryAlertDialog(context: Context) : KinTransferDialogBase(context) {

    override fun getTitle(): String {
        return context.resources.getString(R.string.apps_discovery_alert_title)
    }

    override fun getSubtitle(): String {
        return context.resources.getString(R.string.apps_discovery_alert_subtitle)
    }

    override fun onActionClicked() {
        context.startActivity(AppsDiscoveryActivity.getIntent(context))
    }

    override fun getPositiveButtonText():String{
        return context.resources.getString(R.string.apps_discovery_explore)
    }

    override fun getNegativeButtonText():String{
        return context.resources.getString(R.string.apps_discovery_not_now)
    }
}