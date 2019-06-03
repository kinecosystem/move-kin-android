package org.kinecosystem.onewallet

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import kin.sdk.KinAccount
import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.onewallet.presenter.LinkWalletPresenter
import org.kinecosystem.onewallet.view.LinkWalletViewHolder
import org.kinecosystem.onewallet.view.LinkingBarActionListener
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

    override fun onActivityCreated(activity: Activity, kinAccount: KinAccount, requestCode: Int,
                                   actionButtonResId: Int, progressBarResId: Int) {
        uiHandler = Handler(Looper.getMainLooper())
        val localStore = LocalStore(activity.applicationContext, "ONE_WALLET")

        linkWalletPresenter = LinkWalletPresenter(localStore)
        linkWalletPresenter?.onAttach(LinkWalletViewHolder(
                activity.findViewById(actionButtonResId),
                activity.findViewById(progressBarResId)))

        kinAccount.publicAddress?.let {
            setupActionListener(activity, it, requestCode)
        }
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
                transferManager.intentBuilder(ONE_WALLET_APP_ID, ONE_WALLET_LINK_ACTIVITY)
                        .addParam(EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS, publicAddress)
                        .addParam(EXTRA_APP_PACKAGE_ID, BuildConfig.APPLICATION_ID)
                        .addParam(EXTRA_ACTION_TYPE, it.type.toString())
                        .build()
                        .startForResult(requestCode)

            } else {
                Toast.makeText(activity, "Topup not yet implemented", Toast.LENGTH_LONG).show()
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


    override fun onActivityDestroyed() {
        linkWalletPresenter?.onDetach()
    }

    private fun sendLinkingTransaction(transactionEnvelope: String) {
        uiHandler.postDelayed({
            linkWalletPresenter?.onLinkWalletTimeout()
        }, TIMEOUT_IN_MILLIS + 100)

        executorService.execute {
            val startTime = System.currentTimeMillis()
            try {
                val id = kinAccount?.sendLinkAccountsTransaction(transactionEnvelope)
                if (!reachedTimeout(startTime)) {
                    uiHandler.post {
                        if (id != null) {
                            linkWalletPresenter?.onLinkWalletSucceeded()
                        } else {
                            linkWalletPresenter?.onLinkWalletError("Linking error. Null transaction id")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (!reachedTimeout(startTime)) {
                    uiHandler.post {
                        linkWalletPresenter?.onLinkWalletError("Exception while linking $e ${e.message}")
                    }
                }
            }
        }
    }

    private fun reachedTimeout(startTime: Long): Boolean {
        return (System.currentTimeMillis() - startTime) >= TIMEOUT_IN_MILLIS
    }

}

