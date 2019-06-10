package org.kinecosystem.onewallet

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import kin.sdk.KinAccount
import kin.sdk.Transaction
import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.onewallet.model.OneWalletActionModel
import org.kinecosystem.onewallet.presenter.LinkWalletPresenter
import org.kinecosystem.onewallet.view.LinkWalletViewHolder
import org.kinecosystem.onewallet.view.LinkingBarActionListener
import org.kinecosystem.onewallet.view.LinkingProgressBar
import org.kinecosystem.onewallet.view.OneWalletActionButton
import org.kinecosystem.transfer.sender.manager.TransferManager
import java.util.concurrent.Executors

class OneWalletClient(private val appId: String) : IOneWalletClient {
    companion object {
        val ONE_WALLET_APP_ID = "org.kinecosystem.kinit"
        val ONE_WALLET_LINK_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.LinkAccountActivity"
        val ONE_WALLET_TOPUP_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.TopupAccountActivity"
        val ONE_WALLET_MAIN_ACTIVITY = "org.kinecosystem.kinit.view.SplashActivity"
        val EXTRA_ACTION_TYPE = "EXTRA_ACTION_TYPE"
        val EXTRA_APP_ID = "EXTRA_APP_ID"
        val EXTRA_PUBLIC_ADDRESS = "EXTRA_PUBLIC_ADDRESS"
        val TIMEOUT_IN_MILLIS = 10005L
    }

    private var executorService = Executors.newCachedThreadPool()
    private lateinit var uiHandler: Handler
    private var linkWalletPresenter: LinkWalletPresenter? = null
    private var kinAccount: KinAccount? = null
    private lateinit var oneWalletActionModel: OneWalletActionModel
    private var hostActivity: Activity? = null
    private var oneWalletRequestCode = 99

    override fun onActivityCreated(activity: Activity, account: KinAccount, requestCode: Int) {
        uiHandler = Handler(Looper.getMainLooper())
        val localStore = LocalStore(activity.applicationContext, "ONE_WALLET")
        oneWalletActionModel = OneWalletActionModel(localStore)
        kinAccount = account
        hostActivity = activity
        oneWalletRequestCode = requestCode
    }

    override fun bindViews(actionButton: OneWalletActionButton, progressBar: LinkingProgressBar) {
        linkWalletPresenter = LinkWalletPresenter(oneWalletActionModel)
        linkWalletPresenter?.onAttach(LinkWalletViewHolder(
                actionButton, progressBar))

        kinAccount?.publicAddress?.let { publicAddress ->
            setupActionListeners(publicAddress, oneWalletRequestCode)
        }
    }

    override fun onActivityResult(resultCode: Int, intent: Intent) {

        linkWalletPresenter?.onLinkWalletStarted()
        val transferManager = TransferManager(hostActivity)
        transferManager.processResponse(resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                linkWalletPresenter?.onLinkWalletCancelled()
            }

            override fun onError(error: String) {
                Log.e("Linking", "One Wallet (Kinit) was unable to build the linking transaction. Error is: " + error)
                linkWalletPresenter?.onLinkWalletError(error)
            }

            override fun onResult(data: String) {
                sendLinkingTransaction(data)
            }
        })
    }

    override fun unbindViews() {
        linkWalletPresenter?.onDetach()
        linkWalletPresenter = null
    }

    override fun onActivityDestroyed() {
        hostActivity = null
    }

    private fun setupActionListeners(publicAddress: String, requestCode: Int) {
        linkWalletPresenter?.view?.actionButton?.setOnClickListener {
            startOneWalletAction(publicAddress, requestCode)
        }

        linkWalletPresenter?.view?.progressBar?.setBarActionListener(object : LinkingBarActionListener {
            override fun onLinkingRetryClicked() {
                // retry
                startOneWalletAction(publicAddress, requestCode)
            }

            override fun onOpenKinitClicked() {
                TransferManager(hostActivity)
                        .intentBuilder(ONE_WALLET_APP_ID, ONE_WALLET_MAIN_ACTIVITY)
                        .build()
                        .start()
            }
        })
    }

    private fun startOneWalletAction(publicAddress: String, requestCode: Int) {
        val model = linkWalletPresenter?.oneWalletActionModel
        model?.let {
            val transferManager = TransferManager(hostActivity)
            val activityToLaunch = if (it.isLinkingButton())
                ONE_WALLET_LINK_ACTIVITY else
                ONE_WALLET_TOPUP_ACTIVITY
            val started = transferManager.intentBuilder(ONE_WALLET_APP_ID, activityToLaunch)
                    .addParam(EXTRA_PUBLIC_ADDRESS, publicAddress)
                    .addParam(EXTRA_APP_ID, appId)
                    .addParam(EXTRA_ACTION_TYPE, it.type.toString())
                    .build()
                    .startForResult(requestCode)
            if (!started) {
                Toast.makeText(hostActivity, "Make sure you have the latest version of Kinit installed on your device", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendLinkingTransaction(transactionEnvelope: String) {
        var reachedTimeout = false
        val timeoutRunnable = Runnable {
            reachedTimeout = true
            linkWalletPresenter?.onLinkWalletTimeout()
        }

        uiHandler.postDelayed(timeoutRunnable, TIMEOUT_IN_MILLIS)

        executorService.execute {
            try {
                val externalTransaction = Transaction.decodeTransaction(transactionEnvelope)
                externalTransaction.addSignature(kinAccount)
                val transactionId = kinAccount?.sendTransactionSync(externalTransaction)
                uiHandler.removeCallbacks(timeoutRunnable)
                transactionId?.let {
                    uiHandler.post {
                        oneWalletActionModel.type = OneWalletActionModel.Type.TOP_UP
                        if (!reachedTimeout) {
                            linkWalletPresenter?.onLinkWalletSucceeded(oneWalletActionModel)
                        }
                    }
                } ?: postLinkWalletError(reachedTimeout, "Linking error. Null transaction id")

            } catch (e: Exception) {
                e.printStackTrace()
                uiHandler.removeCallbacks(timeoutRunnable)
                postLinkWalletError(reachedTimeout, "Exception while linking $e ${e.message}")
            }
        }
    }

    private fun postLinkWalletError(reachedTimeout: Boolean, errorMessage: String) {
        if (!reachedTimeout) {
            uiHandler.post {
                linkWalletPresenter?.onLinkWalletError(errorMessage)
            }
        }
    }
}

