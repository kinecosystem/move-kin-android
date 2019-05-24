package org.kinecosystem.linkwallet

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import kin.sdk.KinClient

class LinkingClient constructor(val kinClient: KinClient) {
    companion object {
        val ONE_WALLET_APP_ID = "org.kinecosystem.kinit"
        val ONE_WALLET_LINK_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.LinkAccountActivity"
        val EXTRA_SOURCE_APP_NAME = "EXTRA_SOURCE_APP_NAME"
        val EXTRA_APP_PACKAGE_ID = "EXTRA_APP_PACKAGE_ID"
        val EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS = "EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS"
    }

    class MasterWalletUnreachableException(message: String) : Exception(message)

    fun startLinkingWalletForResult(appName: String, appPackageId:String, appAccountPublicAddress:String,
                                    requestCode: Int, activity: Activity) {
        val intent = getIntentForRemoteActivity(ONE_WALLET_APP_ID, ONE_WALLET_LINK_ACTIVITY,
                activity.packageManager)

        intent.putExtra(EXTRA_SOURCE_APP_NAME, appName)
        intent.putExtra(EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS, appAccountPublicAddress)
        intent.putExtra(EXTRA_APP_PACKAGE_ID, appPackageId)
        activity.startActivityForResult(intent, requestCode)
    }

    fun processLinkingResult(resultCode: Int, intent: Intent, view: View) {
        Log.d("","$resultCode $intent ")
   //     intent.getExtraString("")

    }

    private fun getIntentForRemoteActivity(remoteAppId: String, remoteAppActivity:String, packageManager: PackageManager): Intent {
        val intent = Intent()

        intent.component = (ComponentName(remoteAppId, remoteAppActivity))
        val resolveInfos = packageManager.queryIntentActivities(intent, 0)
        if (resolveInfos.isEmpty()) {
            throw MasterWalletUnreachableException("")
        }

        val exported = resolveInfos[0].activityInfo.exported
        if (!exported)
            throw MasterWalletUnreachableException("Error in Master Wallet App configuration")
        return intent
    }
}

