package org.kinecosystem.linkwallet

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import kin.sdk.KinAccount
import org.kinecosystem.transfer.sender.manager.TransferManager
import java.util.concurrent.Executors


class LinkingClient constructor(val kinAccount: KinAccount) {
    private var executorService = Executors.newCachedThreadPool()

    companion object {
        val ONE_WALLET_APP_ID = "org.kinecosystem.kinit"
        val ONE_WALLET_LINK_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.LinkAccountActivity"
        val EXTRA_APP_PACKAGE_ID = "EXTRA_APP_PACKAGE_ID"
        val EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS = "EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS"
    }

    fun startLinkingTransactionRequest(activity: Activity, requestCode: Int,
                                       appPackageId: String, appAccountPublicAddress: String) {
        val transferManager = TransferManager(activity)
        transferManager.intentBuilder(ONE_WALLET_APP_ID, ONE_WALLET_LINK_ACTIVITY)
                .addParam(EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS, appAccountPublicAddress)
                .addParam(EXTRA_APP_PACKAGE_ID, appPackageId)
                .build()
                .start(requestCode)
    }

    fun processLinkingTransactionResult(activity: Activity, resultCode: Int,
                                        intent: Intent, view: View) {
        Log.d("Linking", "$resultCode $intent ")

        val transferManager = TransferManager(activity)
        transferManager.processResponse(resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                Log.d("Linking", "linking canceled")
            }

            override fun onError(error: String) {
                Log.d("Linking", "linking error in building transaction in Kinit. Error is: " + error)
            }

            override fun onResult(data: String) {
                Log.d("Linking", "The linking transaction envelope is: " + data)
                // create transaction from envelope
                // sign it with app's signature
                // send to blockchain using whitelisting
                executorService.execute {
                    try {
                        var id = kinAccount.sendLinkAccountsTransaction(data)
                        Log.d("Linking", "Yay! The linking transaction was sent successfully and the transaction id is "+id.id())
                    }
                    catch (e: Exception){
                        Log.d("Linking", "Linking transaction failed with exception $e and message ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        })

    }


}

