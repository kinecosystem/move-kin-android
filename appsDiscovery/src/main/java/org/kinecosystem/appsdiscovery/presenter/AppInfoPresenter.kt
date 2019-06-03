package org.kinecosystem.appsdiscovery.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.appsdiscovery.view.IAppInfoView
import org.kinecosystem.appsdiscovery.view.customView.AppStateView
import org.kinecosystem.appsdiscovery.view.customView.TransferBarView
import org.kinecosystem.appsdiscovery.view.customView.TransferInfo
import org.kinecosystem.appsdiscovery.model.*
import org.kinecosystem.appsdiscovery.repositories.DiscoveryAppsRepository
import org.kinecosystem.transfer.sender.manager.TransferManager
import org.kinecosystem.common.utils.isAppInstalled

class AppInfoPresenter(private val appName: String?, private val repository: DiscoveryAppsRepository, private val transferManager: TransferManager) : BasePresenter<IAppInfoView>(), IAppInfoPresenter {

    val REMOTE_PUBLIC_ADDRESS_REQUEST_CODE = 200
    val AMOUNT_CHOOSER_REQUEST_CODE = 100
    val TRANSACTION_TIMEOUT = 10 * 1000L
    val MEMO_PREFIX = "CrossApps_"
    private var appState: AppStateView.State = AppStateView.State.ReceiveKinNotSupported

    private var app: EcosystemApp? = null
    private val handler = Handler()
    private var afterTimeout = false
    private var gotTransferResponse = false

    override fun updateBalance(currentBalance: Int) {
        repository.storeCurrentBalance(currentBalance)
    }

    override fun onStart() {
        view?.bindToSendService()
    }

    override fun onResume(context: Context) {
        app?.identifier?.let {
            appState = if (!context.isAppInstalled(it)) {
                AppStateView.State.NotInstalled
            } else {
                if (app?.canTransferKin!!) {
                    AppStateView.State.ReceiveKinSupported
                } else {
                    AppStateView.State.ReceiveKinNotSupported
                }
            }
            view?.updateAppState(appState)
        }
    }

    override fun onActionButtonClicked() {
        when (appState) {
            AppStateView.State.ReceiveKinSupported -> {
                onRequestReceiverPublicAddress()
            }
            AppStateView.State.NotInstalled -> {
                view?.navigateTo(app?.downloadUrl!!)
            }
        }
    }

    override fun onDestroy() {
        view?.unbindToSendService()
    }

    override fun processResponse(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == AMOUNT_CHOOSER_REQUEST_CODE) {
            processAmountResponse(resultCode, intent)
        } else if (requestCode == REMOTE_PUBLIC_ADDRESS_REQUEST_CODE) {
            parsePublicAddressData(resultCode, intent)
        }
    }

    private fun processAmountResponse(resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            intent?.let {
                view?.updateTransferStatus(TransferBarView.TransferStatus.Started)
                val amountToSend = it.getIntExtra(AmountChooserPresenter.PARAM_AMOUNT, 0)
                sendKin(amountToSend)
            } ?: kotlin.run {
                view?.updateTransferStatus(TransferBarView.TransferStatus.FailedConnectionError)
            }
        }
    }

    private fun sendKin(amountToSend: Int) {
        app?.let {
            it.identifier?.let { pkg ->
                view?.updateAmount(amountToSend)
            }
        }
        app?.identifier?.let { receiverPackage ->
            app?.name?.let { senderName ->
                view?.sendKin(repository.getReceiverAppPublicAddress(), senderName, amountToSend, getTransactionMemo(), receiverPackage)
            }
        }
        startTimeOutCounter()
    }

    private fun getTransactionMemo() = "$MEMO_PREFIX${app?.memo}"

    private fun startTimeOutCounter() {
        afterTimeout = false
        gotTransferResponse = false
        handler.postDelayed({
            if (!gotTransferResponse) {
                afterTimeout = true
                view?.updateTransferStatus(TransferBarView.TransferStatus.Timeout)
            }
        }, TRANSACTION_TIMEOUT)
    }

    private fun parsePublicAddressData(resultCode: Int, intent: Intent?) {
        transferManager.processResponse(resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                Log.d("AppInfoPresenter", "Operation cancelled, no public address received")
            }

            override fun onError(error: String) {
                Log.d("AppInfoPresenter", "Error retrieving public address, error message " + error)
                view?.updateTransferStatus(TransferBarView.TransferStatus.FailedConnectionError)
            }

            override fun onResult(address: String) {
                repository.storeReceiverAppPublicAddress(address)
                Log.d("AppInfoPresenter", "got address onResult $address")
                app?.let {
                    view?.startAmountChooserActivity(it.iconUrl, repository.getCurrentBalance(), AMOUNT_CHOOSER_REQUEST_CODE)
                }
            }
        })
    }

    override fun onTransferFailed() {
        if (!afterTimeout) {
            gotTransferResponse = true
            view?.updateTransferStatus(TransferBarView.TransferStatus.Failed)
        }
    }

    override fun onTransferComplete() {
        if (!afterTimeout) {
            gotTransferResponse = true
            view?.updateTransferStatus(TransferBarView.TransferStatus.Complete)
        }
        view?.requestCurrentBalance()
    }

    override fun onRequestReceiverPublicAddress() {
        repository.clearReceiverAppPublicAddress()
        app?.launchActivity?.let { activityPath ->
            app?.identifier?.let { receiverPkg ->
                val started = transferManager.startTransferRequestActivity(REMOTE_PUBLIC_ADDRESS_REQUEST_CODE,
                        receiverPkg, activityPath)
                if (!started) {
                    view?.updateTransferStatus(TransferBarView.TransferStatus.FailedReceiverError)
                }
            } ?: kotlin.run {
                view?.updateTransferStatus(TransferBarView.TransferStatus.FailedReceiverError)
            }
        } ?: kotlin.run {
            view?.updateTransferStatus(TransferBarView.TransferStatus.FailedReceiverError)

        }
    }

    override fun onAttach(view: IAppInfoView) {
        super.onAttach(view)
        app = repository.getAppByName(appName)
        view.initViews(app)
        app?.let { application ->
            application.identifier?.let { receiverPkg ->
                view.initTransfersInfo(TransferInfo(repository.getStoredAppIcon(), application.iconUrl, application.name, receiverPkg))
            }
        }
    }
}