package org.kinecosystem.onewallet

import android.app.Activity
import android.content.Intent
import kin.sdk.KinAccount

interface IOneWalletClient {

    fun onActivityCreated(activity: Activity, kinAccount: KinAccount, requestCode: Int,
                          actionButtonResId: Int, progressBarResId: Int)

    fun onActivityResult(activity: Activity, resultCode: Int, intent: Intent)

    fun onActivityDestroyed()
}
