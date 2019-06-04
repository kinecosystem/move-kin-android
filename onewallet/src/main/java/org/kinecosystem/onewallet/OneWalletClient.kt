package org.kinecosystem.onewallet

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import kin.sdk.KinAccount
import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.onewallet.model.OneWalletActionModel
import org.kinecosystem.onewallet.presenter.LinkWalletPresenter
import org.kinecosystem.onewallet.view.LinkWalletViewHolder
import org.kinecosystem.onewallet.view.LinkingBarActionListener
import org.kinecosystem.onewallet.view.LinkingProgressBar
import org.kinecosystem.onewallet.view.OneWalletActionButton
import org.kinecosystem.transfer.sender.manager.TransferManager
import java.util.concurrent.Executors

class OneWalletClient : IOneWalletClient {
    companion object {
        val ONE_WALLET_APP_ID = "org.kinecosystem.kinit"
        val ONE_WALLET_LINK_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.LinkAccountActivity"
        val ONE_WALLET_MAIN_ACTIVITY = "org.kinecosystem.kinit.view.SplashActivity"
        val EXTRA_APP_PACKAGE_ID = "EXTRA_APP_PACKAGE_ID"
        val EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS = "EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS"
        val EXTRA_ACTION_TYPE = "EXTRA_ACTION_TYPE"
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
            hostActivity?.let { hostActivity ->
                setupActionListener(hostActivity, publicAddress, oneWalletRequestCode)
            }
        }
    }

    override fun onActivityResult(activity: Activity, resultCode: Int, intent: Intent) {

        linkWalletPresenter?.onLinkWalletStarted()
        val transferManager = TransferManager(activity)
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



    private fun setupActionListener(activity: Activity, publicAddress: String, requestCode: Int) {
        linkWalletPresenter?.view?.actionButton?.setOnClickListener {
            startOneWalletAction(activity, publicAddress, requestCode)
        }
        linkWalletPresenter?.view?.progressBar?.setBarActionListener(object : LinkingBarActionListener {
            override fun onLinkingRetryClicked() {
                startOneWalletAction(activity, publicAddress, requestCode)
            }

            override fun onOpenKinitClicked() {
                TransferManager(activity)
                        .intentBuilder(ONE_WALLET_APP_ID, ONE_WALLET_MAIN_ACTIVITY)
                        .build()
                        .start()

            }
        })
    }

    private fun startOneWalletAction(activity: Activity, publicAddress: String, requestCode: Int) {
        val model = linkWalletPresenter?.oneWalletActionModel
        model?.let {
            if (it.isLinkingButton()) {
                val transferManager = TransferManager(activity)
                val started = transferManager.intentBuilder(ONE_WALLET_APP_ID, ONE_WALLET_LINK_ACTIVITY)
                        .addParam(EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS, publicAddress)
                        .addParam(EXTRA_APP_PACKAGE_ID, BuildConfig.APPLICATION_ID)
                        .addParam(EXTRA_ACTION_TYPE, it.type.toString())
                        .build()
                        .startForResult(requestCode)
                if (!started) {
                    Toast.makeText(activity, "Make sure you have the latest version of Kinit installed on your device", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(activity, "Topup not yet implemented", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendLinkingTransaction(transactionEnvelope: String) {
        val startTime = System.currentTimeMillis()
        val timeoutRunnable = Runnable {
            linkWalletPresenter?.onLinkWalletTimeout()
        }

        uiHandler.postDelayed(timeoutRunnable, TIMEOUT_IN_MILLIS)

        executorService.execute {
            try {
                val id = kinAccount?.sendLinkAccountsTransaction(transactionEnvelope)
                uiHandler.removeCallbacks(timeoutRunnable)
                id?.let {
                    uiHandler.post {
                        oneWalletActionModel.type = OneWalletActionModel.Type.TOP_UP
                        if (!reachedTimeout(startTime)) {
                            linkWalletPresenter?.onLinkWalletSucceeded(oneWalletActionModel)
                        }
                    }
                } ?: postLinkWalletError(startTime, "Linking error. Null transaction id")

            } catch (e: Exception) {
                e.printStackTrace()
                uiHandler.removeCallbacks(timeoutRunnable)
                postLinkWalletError(startTime, "Exception while linking $e ${e.message}")
            }
        }
    }

    private fun postLinkWalletError(startTime: Long, errorMessage: String) {
        if (!reachedTimeout(startTime)) {
            uiHandler.post {
                linkWalletPresenter?.onLinkWalletError(errorMessage)
            }
        }
    }

    private fun reachedTimeout(startTime: Long): Boolean {
        return (System.currentTimeMillis() - startTime) >= TIMEOUT_IN_MILLIS
    }

}

