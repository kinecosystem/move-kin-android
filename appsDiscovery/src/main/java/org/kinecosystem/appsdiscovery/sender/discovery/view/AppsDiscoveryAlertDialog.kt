package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.apps_discovery_dialog.view.*
import org.kinecosystem.appsdiscovery.R

class AppsDiscoveryAlertDialog(val context: Context) {


    private val alertDialog: AlertDialog

    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.apps_discovery_dialog, null)
        alertDialog = AlertDialog.Builder(context).setView(dialogView).create()
        with(dialogView.positiveBtn) {
            setOnClickListener {
                startAppsDiscoveryActivity()
                alertDialog.dismiss()
            }
        }
        with(dialogView.negativeBtn) {
            setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    fun show() {
        alertDialog.show()
    }

    private fun startAppsDiscoveryActivity() {
        context.startActivity(AppsDiscoveryActivity.getIntent(context))
    }
}