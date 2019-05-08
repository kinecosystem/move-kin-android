package org.kinecosystem.appsdiscovery.sender.discovery.view.customView

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.sender.discovery.view.AppsDiscoveryActivity

class AppsDiscoveryAlertDialog(context: Context) : AlertDialog(context) {

    init {
        val dialogView = View.inflate(context, R.layout.apps_discovery_dialog, null)
        setView(dialogView)
        dialogView.findViewById<TextView>(R.id.positiveBtn).setOnClickListener {
            startAppsDiscoveryActivity()
            dismiss()
        }
        dialogView.findViewById<TextView>(R.id.negativeBtn).setOnClickListener {
            dismiss()
        }
    }

    private fun startAppsDiscoveryActivity() {
        context.startActivity(AppsDiscoveryActivity.getIntent(context))
    }
}