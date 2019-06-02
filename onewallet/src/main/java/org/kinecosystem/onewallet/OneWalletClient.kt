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
import org.kinecosystem.transfer.sender.manager.TransferManager
import java.util.concurrent.Executors

class OneWalletClient : IOneWalletClient {
    companion object {
        val ONE_WALLET_APP_ID = "org.kinecosystem.kinit"
        val ONE_WALLET_LINK_ACTIVITY = "org.kinecosystem.kinit.view.onewallet.LinkAccountActivity"
        val EXTRA_APP_PACKAGE_ID = "EXTRA_APP_PACKAGE_ID"
        val EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS = "EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS"
        val EXTRA_ACTION_TYPE = "EXTRA_ACTION_TYPE"
    }

    private var executorService = Executors.newCachedThreadPool()
    private lateinit var uiHandler: Handler
    private var linkWalletPresenter: LinkWalletPresenter? = null
    private var kinAccount: KinAccount? = null

    override fun onActivityCreated(activity: Activity, kinAccount: KinAccount, requestCode: Int,
                                   actionButtonResId: Int, progressBarResId: Int) {
        uiHandler = Handler(Looper.getMainLooper())
        val localStore = LocalStore(activity.applicationContext, "ONE_WALLET")

        linkWalletPresenter = LinkWalletPresenter(localStore, uiHandler)
        linkWalletPresenter?.onAttach(LinkWalletViewHolder(
                activity.findViewById(actionButtonResId),
                activity.findViewById(progressBarResId)))

        kinAccount.publicAddress?.let {
            setupActionListener(activity, it, requestCode)
        }
    }

    private fun setupActionListener(activity: Activity, publicAddress: String, requestCode: Int) {
        linkWalletPresenter?.view?.actionButton?.setOnClickListener {
            val model = linkWalletPresenter?.oneWalletActionModel
            model?.let {
                if (it.isLinkingButton()) {
                    val transferManager = TransferManager(activity)
                    transferManager.intentBuilder(ONE_WALLET_APP_ID, ONE_WALLET_LINK_ACTIVITY)
                            .addParam(EXTRA_APP_ACCOUNT_PUBLIC_ADDRESS, publicAddress)
                            .addParam(EXTRA_APP_PACKAGE_ID, BuildConfig.APPLICATION_ID)
                            .addParam(EXTRA_ACTION_TYPE, it.type.toString())
                            .build()
                            .start(requestCode)

                } else {
                    Toast.makeText(activity, "Topup not yet implemented", Toast.LENGTH_LONG).show()
                }
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
                processLinkingTransactionResult(data)
            }
        })
    }


    override fun onActivityDestroyed() {
        linkWalletPresenter?.onDetach()
    }

    private fun processLinkingTransactionResult(data: String) {

        Log.d("Linking", "The linking transaction envelope is: " + data)
        executorService.execute {
            try {
                val id = kinAccount?.sendLinkAccountsTransaction(data)

                uiHandler.post {
                    linkWalletPresenter?.onLinkWalletSucceeded()
                }
                Log.d("Linking", "Yay! The linking transaction was sent successfully and the transaction id is " + id?.id())
            } catch (e: Exception) {
                Log.d("Linking", "Linking transaction failed with exception $e and message ${e.message}")
                e.printStackTrace()
                uiHandler.post {
                    linkWalletPresenter?.onLinkWalletError("Exception while linking $e ${e.message}")
                }
            }
        }
    }

}

