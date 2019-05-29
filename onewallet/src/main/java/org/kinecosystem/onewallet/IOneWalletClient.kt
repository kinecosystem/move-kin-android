package org.kinecosystem.onewallet

import android.app.Activity
import android.content.Intent

interface IOneWalletClient {

    fun init(activity: Activity, requestCode: Int,
             appAccountPublicAddress: String,
             button: OneWalletActionButton)

    fun processResult(activity: Activity, resultCode: Int, intent: Intent,
                      actionButton: OneWalletActionButton, progressBar: OneWalletProgressBar)
}
