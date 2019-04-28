package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.apps_discovery_dialog.view.*
import org.kinecosystem.appsdiscovery.R

class AppsDiscoveryAlertDialog(val context: Context, positiveAction: (() -> Unit)?=null, negativeAction: (() -> Unit)?=null){

    private val alertDialog:AlertDialog
    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.apps_discovery_dialog, null)
        alertDialog = AlertDialog.Builder(context).setView(dialogView).create()
        with(dialogView.positiveBtn) {
            setOnClickListener {
                startAppsDiscoveryActivity()
                positiveAction?.invoke()
                alertDialog.dismiss()
            }
        }
        with(dialogView.negativeBtn) {
            setOnClickListener {
                negativeAction?.invoke()
                alertDialog.dismiss()
            }
        }
    }

    fun show(){
        alertDialog.show()
    }

    private fun startAppsDiscoveryActivity() {
        context.startActivity(AppsDiscoveryActivity.getIntent(context))
    }
}