package org.kinecosystem.onewallet

import android.app.Activity
import android.content.Intent
import kin.sdk.KinAccount
import org.kinecosystem.onewallet.view.LinkingProgressBar
import org.kinecosystem.onewallet.view.OneWalletActionButton
import org.kinecosystem.onewallet.view.UnifiedBalanceBar

interface IOneWalletClient {

    fun onActivityCreated(activity: Activity, kinAccount: KinAccount, requestCode: Int)

    fun bindViews(actionButtonView: OneWalletActionButton, progressBarView: LinkingProgressBar,
                  unifiedBalanceBar: UnifiedBalanceBar)

    fun unbindViews()

    fun onActivityResult(resultCode: Int, intent: Intent)

    fun onActivityDestroyed()
}
