package org.kinecosystem.onewallet

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import kin.sdk.KinAccount
import kin.sdk.RawTransaction
import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.onewallet.model.OneWalletDataModel
import org.kinecosystem.onewallet.presenter.BalanceBarPresenter
import org.kinecosystem.onewallet.presenter.LinkWalletPresenter
import org.kinecosystem.onewallet.view.*
import org.kinecosystem.transfer.sender.manager.TransferManager
import java.util.concurrent.Executors

class OneWalletClient(private val appId: String) : IOneWalletClient {

    companion object {
        val ONE_WALLET_APP_ID = "org.kinecosystem.kinit"
        val ONE_WALLET_LINK_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.LinkAccountActivity"
        val ONE_WALLET_TOPUP_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.TopupAccountActivity"
        val ONE_WALLET_MAIN_ACTIVITY = "org.kinecosystem.kinit.view.SplashActivity"
        val EXTRA_APP_ID = "EXTRA_APP_ID"
        val EXTRA_PUBLIC_ADDRESS = "EXTRA_PUBLIC_ADDRESS"
        val TIMEOUT_IN_MILLIS = 10005L
    }

    private var executorService = Executors.newCachedThreadPool()
    private lateinit var uiHandler: Handler
    private var linkWalletPresenter: LinkWalletPresenter? = null
    private var kinAccount: KinAccount? = null
    private lateinit var oneWalletDataModel: OneWalletDataModel
    private var hostActivity: Activity? = null
    private var oneWalletRequestCode = 99
    private var balanceBarPresenter: BalanceBarPresenter? = null

    override fun onActivityCreated(activity: Activity, account: KinAccount, requestCode: Int) {
        uiHandler = Handler(Looper.getMainLooper())
        val localStore = LocalStore(activity.applicationContext, "ONE_WALLET")
        oneWalletDataModel = OneWalletDataModel(localStore)
        kinAccount = account
        hostActivity = activity
        oneWalletRequestCode = requestCode
    }

    override fun bindViews(actionButton: OneWalletActionButton, progressBar: LinkingProgressBar,
                           unifiedBalanceBar: UnifiedBalanceBar) {
        bindLinkAndTopupViews(actionButton, progressBar)
        bindUnifiedBalanceBar(unifiedBalanceBar)
    }

    private fun bindLinkAndTopupViews(actionButton: OneWalletActionButton, progressBar: LinkingProgressBar) {
        linkWalletPresenter = LinkWalletPresenter(oneWalletDataModel)
        linkWalletPresenter?.onAttach(LinkWalletViewHolder(
                actionButton, progressBar))

        kinAccount?.publicAddress?.let { publicAddress ->
            setupActionListeners(publicAddress, oneWalletRequestCode)
        }
    }

    private fun bindUnifiedBalanceBar(unifiedBalanceBar: UnifiedBalanceBar) {
        balanceBarPresenter = BalanceBarPresenter()
        balanceBarPresenter?.onAttach(unifiedBalanceBar)
        balanceBarPresenter?.view?.setManageWalletsListener {
            startKinit()
        }
        if (oneWalletDataModel.isWalletLinked()) {
            fetchUnifiedBalance()
            balanceBarPresenter?.show()
        }
    }


    override fun onActivityResult(resultCode: Int, intent: Intent?) {
        if (!oneWalletDataModel.isWalletLinked()) {
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
    }

    override fun unbindViews() {
        linkWalletPresenter?.onDetach()
        linkWalletPresenter = null
        balanceBarPresenter?.onDetach()
        balanceBarPresenter = null
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
                startKinit()
            }

            override fun onClosed() {
                if (oneWalletDataModel.isWalletLinked()) {
                    balanceBarPresenter?.show()
                }
            }
        })
    }

    private fun startKinit() {
        TransferManager(hostActivity)
                .intentBuilder(ONE_WALLET_APP_ID, ONE_WALLET_MAIN_ACTIVITY)
                .build()
                .start()
    }

    private fun startOneWalletAction(publicAddress: String, requestCode: Int) {
        val model = linkWalletPresenter?.oneWalletDataModel
        model?.let {
            val transferManager = TransferManager(hostActivity)
            val activityToLaunch = if (it.isWalletLinked())
                ONE_WALLET_TOPUP_ACTIVITY else
                ONE_WALLET_LINK_ACTIVITY

            val started = transferManager.intentBuilder(ONE_WALLET_APP_ID, activityToLaunch)
                    .addParam(EXTRA_PUBLIC_ADDRESS, publicAddress)
                    .addParam(EXTRA_APP_ID, appId)
                    .build()
                    .startForResult(requestCode)
            if (!started) {
                Toast.makeText(hostActivity, "Make sure you have the latest version of Kinit installed on your device", Toast.LENGTH_LONG).show()
            }
        }
    }

    data class LinkingResult(val masterPublicAddress: String, val transactionEnvelope: String)

    private fun sendLinkingTransaction(linkTransactionResult: String) {
        var reachedTimeout = false
        val timeoutRunnable = Runnable {
            reachedTimeout = true
            linkWalletPresenter?.onLinkWalletTimeout()
        }

        uiHandler.postDelayed(timeoutRunnable, TIMEOUT_IN_MILLIS)

        executorService.execute {
            try {
                val linkingResult: LinkingResult = Gson().fromJson(linkTransactionResult, LinkingResult::class.java)
                val externalTransaction = RawTransaction.decodeTransaction(linkingResult.transactionEnvelope)
                externalTransaction.addSignature(kinAccount)
                val transactionId = kinAccount?.sendTransactionSync(externalTransaction)
                uiHandler.removeCallbacks(timeoutRunnable)
                transactionId?.let {
                    uiHandler.post {
                        oneWalletDataModel.masterPublicAddress = linkingResult.masterPublicAddress
                        fetchUnifiedBalance()
                        if (!reachedTimeout) {
                            linkWalletPresenter?.onLinkWalletSucceeded(oneWalletDataModel)
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


    private fun fetchUnifiedBalance() {
        executorService.execute {
            try {
                kinAccount?.getAggregatedBalanceSync(oneWalletDataModel.masterPublicAddress)
                        ?.value()?.toInt()?.let {
                    uiHandler.post {
                        balanceBarPresenter?.onBalanceReceived(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

