package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppInfoView
import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppStateView
import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.TransferBarView
import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.TransferInfo
import org.kinecosystem.appsdiscovery.sender.model.*
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository
import org.kinecosystem.appsdiscovery.sender.transfer.TransferManager
import org.kinecosystem.appsdiscovery.utils.isAppInstalled

class AppInfoPresenter(private val appName: String?, private val repository: DiscoveryAppsRepository, private val transferManager: TransferManager) : BasePresenter<IAppInfoView>(), IAppInfoPresenter {


    private val AmountChooserRequestCode = 100
    private val memoDelim = "CrossApps-"
    private var appState: AppStateView.State = AppStateView.State.ReceiveKinNotSupported

    private var app: EcosystemApp? = null

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
        if (requestCode == AmountChooserRequestCode) {
            processAmountResponse(resultCode, intent)
        } else {
            parsePublicAddressData(requestCode, resultCode, intent)
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
                view?.initTransfersInfo(TransferInfo(repository.getStoredAppIcon(), it.iconUrl, it.name, pkg, amountToSend))
            }
        }
        val memo = "$memoDelim${app?.memo}"
        app?.identifier?.let { receiverPackage ->
            view?.startSendKin(repository.getReceiverAppPublicAddress(), amountToSend, memo, receiverPackage)
        }

    }

    private fun parsePublicAddressData(requestCode: Int, resultCode: Int, intent: Intent?) {
        transferManager.processResponse(requestCode, resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                Log.d("AppInfoPresenter", "Operation cancelled, no public address received")
            }

            override fun onError(error: String) {
                Log.d("AppInfoPresenter", "Error retrieving public address, error message " + error)
                view?.updateTransferStatus(TransferBarView.TransferStatus.FailedConnectionError)
            }

            override fun onAddressReceived(address: String) {
                repository.storeReceiverAppPublicAddress(address)
                Log.d("AppInfoPresenter", "got address onAddressReceived $address")
                app?.iconUrl?.let {
                    view?.startAmountChooserActivity(it, repository.getCurrentBalance(), AmountChooserRequestCode)
                }
            }
        })
    }

    override fun onTransferFailed() {
        view?.updateTransferStatus(TransferBarView.TransferStatus.Failed)
    }

    override fun onTransferComplete() {
        view?.requestBalance()
        view?.updateTransferStatus(TransferBarView.TransferStatus.Complete)
    }

    override fun onRequestReceiverPublicAddress() {
        repository.clearReceiverAppPublicAddress()
        app?.launchActivity?.let { activityPath ->
            app?.identifier?.let { receiverPkg ->
                val started = transferManager.startTransferRequestActivity(receiverPkg, activityPath)
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
    }
}