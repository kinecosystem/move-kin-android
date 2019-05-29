package org.kinecosystem.onewallet

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import kin.sdk.KinAccount
import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.transfer.sender.manager.TransferManager
import java.util.concurrent.Executors

class OneWalletClient constructor(private val kinAccount: KinAccount) : IOneWalletClient {
    companion object {
        val ONE_WALLET_APP_ID = "org.kinecosystem.kinit"
        val ONE_WALLET_LINK_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.LinkAccountActivity"
        val EXTRA_APP_PACKAGE_ID = "EXTRA_APP_PACKAGE_ID"
        val EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS = "EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS"
        val EXTRA_ACTION_TYPE = "EXTRA_ACTION_TYPE"
    }

    private var executorService = Executors.newCachedThreadPool()
    private lateinit var uiHandler: Handler
    private lateinit var localStore: LocalStore
    private lateinit var actionButton: OneWalletActionButton
    private lateinit var progressBar: OneWalletProgressBar

    override fun init(activity: Activity, requestCode: Int,
                      appAccountPublicAddress: String,
                      button: OneWalletActionButton) {
        actionButton = button
        uiHandler = Handler(Looper.getMainLooper())
        localStore = LocalStore(activity.applicationContext, "ONE_WALLET")
        actionButton.type = getButtonTypeFromStore()
        Log.d("OneWalletClient", "button.type = ${actionButton.type}")
        button.setOnClickListener {
            if (button.type == OneWalletActionButton.Type.LINK) {
                val transferManager = TransferManager(activity)
                transferManager.intentBuilder(ONE_WALLET_APP_ID, ONE_WALLET_LINK_ACTIVITY)
                        .addParam(EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS, appAccountPublicAddress)
                        .addParam(EXTRA_APP_PACKAGE_ID, BuildConfig.APPLICATION_ID)
                        .addParam(EXTRA_ACTION_TYPE, button.type.toString())
                        .build()
                        .start(requestCode)
            } else {
                Toast.makeText(activity, "Topup not yet implemented", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun processResult(activity: Activity, resultCode: Int, intent: Intent,
                               button: OneWalletActionButton, bar: OneWalletProgressBar) {

        actionButton = button
        progressBar = bar
        actionButton.isEnabled = false
        val transferManager = TransferManager(activity)
        transferManager.processResponse(resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                Log.d("Linking", "linking canceled")
                progressBar.text = "Linking Cancelled"
                actionButton.isEnabled = true
            }

            override fun onError(error: String) {
                Log.d("Linking", "linking error in building transaction in Kinit. Error is: " + error)
                progressBar.text = "Linking Error $error"
                actionButton.isEnabled = true
            }

            override fun onResult(data: String) {
                progressBar.text = "Linking In Progress..."
                processLinkingTransactionResult(data)
            }
        })
    }

    private fun processLinkingTransactionResult(data: String) {

        Log.d("Linking", "The linking transaction envelope is: " + data)
        // create transaction from envelope
        // sign it with app's signature
        // send to blockchain using whitelisting
        executorService.execute {
            try {
                var id = kinAccount.sendLinkAccountsTransaction(data)
                uiHandler.post {
                    progressBar.text = "Linking Success !!"
                    actionButton.isEnabled = true
                    resetButtonType(OneWalletActionButton.Type.TOP_UP)
                }
                Log.d("Linking", "Yay! The linking transaction was sent successfully and the transaction id is " + id.id())
            } catch (e: Exception) {
                Log.d("Linking", "Linking transaction failed with exception $e and message ${e.message}")
                e.printStackTrace()
                uiHandler.post {
                    progressBar.text = "Exception while linking $e ${e.message}"
                    actionButton.isEnabled = true
                }
            }
        }
    }


    private fun getButtonTypeFromStore(): OneWalletActionButton.Type {
        var buttonTypeString = localStore.getString(EXTRA_ACTION_TYPE, OneWalletActionButton.Type.LINK.toString())
        Log.d("OneWalletClient", "Wallet status " + buttonTypeString)

        if (buttonTypeString == OneWalletActionButton.Type.TOP_UP.toString()) {
            return OneWalletActionButton.Type.TOP_UP
        }
        return OneWalletActionButton.Type.LINK
    }

    private fun resetButtonType(type: OneWalletActionButton.Type) {
        Log.d("OneWalletClient", "Storing button type = $type")
        actionButton.type = type
        localStore.updateString(EXTRA_ACTION_TYPE, type.toString())
        Log.d("OneWalletClient", "button from cache  = " +
                "${localStore.getString(EXTRA_ACTION_TYPE, OneWalletActionButton.Type.LINK.toString())}")
    }
}

