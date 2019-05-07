package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppInfoView
import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.ReceiverAppStateView
import org.kinecosystem.appsdiscovery.sender.model.*
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository
import org.kinecosystem.appsdiscovery.sender.transfer.TransferManager
import org.kinecosystem.appsdiscovery.utils.isAppInstalled

class AppInfoPresenter(private val appName: String?, private val repository: DiscoveryAppsRepository, private val transferManager: TransferManager) : BasePresenter<IAppInfoView>(), IAppInfoPresenter {

    private val AmountChooserRequestCode = 100
    private val memoDelim = "-CrossApps-"
    private var appState: ReceiverAppStateView.State = ReceiverAppStateView.State.ReceiveKinNotSupported

    private var app: EcosystemApp? = null

    override fun updateBalance(currentBalance: Int) {
        repository.storeCurrentBalance(currentBalance)
    }

    override fun onStart() {
        view?.bindToSendService(app?.identifier)
    }

    enum class ServiceError {
        ServiceNotFound,
        ServiceShouldNotBeExported
    }

    override fun onResume(context: Context) {
        app?.identifier?.let {
            appState = if (!context.isAppInstalled(it)) {
                ReceiverAppStateView.State.NotInstalled
            } else {
                if (app?.canTransferKin!!) {
                    ReceiverAppStateView.State.ReceiveKinSupported
                } else {
                    ReceiverAppStateView.State.ReceiveKinNotSupported
                }
            }
            view?.updateAppState(appState)
        }
    }

    override fun onActionButtonClicked() {
        when (appState) {
            ReceiverAppStateView.State.ReceiveKinSupported -> {
                view?.onStartRequestReceiverPublicAddress()
            }
            ReceiverAppStateView.State.NotInstalled -> {
                view?.navigateTo(app?.downloadUrl!!)
            }
        }
    }

    override fun onServiceNotFound() {
        view?.onServiceError(ServiceError.ServiceNotFound)
    }

    override fun onServiceShouldNotBeExported() {
        view?.onServiceError(ServiceError.ServiceShouldNotBeExported)
    }

    override fun onDestroy() {
        view?.unbindToSendService()
    }

    enum class RequestReceiverPublicAddressError {
        NotStarted,
        NoPathInfo,
        BadDataReceived
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
                val amountToSend = it.getIntExtra(AmountChooserPresenter.PARAM_AMOUNT, 0)
                sendKin(amountToSend)
            } ?: kotlin.run {
                view?.onRequestAmountError()
            }
        }
    }

    private fun sendKin(amountToSend: Int) {
        //1-KIT-CrossApps-TIPC
        val memo = "1-${repository.getStoredMemo()}$memoDelim${app?.memo}"
        app?.identifier?.let { receiverPackage ->
            view?.startSendKin(repository.getReceiverAppPublicAddress(), amountToSend, memo, receiverPackage)
            //TODO remove - for testing
            //view?.startSendKin(repository.getReceiverAppPublicAddress(), amountToSend, memo, "org.kinecosystem.swelly")
        }

    }

    private fun parsePublicAddressData(requestCode: Int, resultCode: Int, intent: Intent?) {
        transferManager.processResponse(requestCode, resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                view?.onRequestReceiverPublicAddressCanceled()
            }

            override fun onError(error: String) {
                view?.onRequestReceiverPublicAddressError(RequestReceiverPublicAddressError.BadDataReceived)

            }

            override fun onAddressReceived(address: String) {
                repository.storeReceiverAppPublicAddress(address)
                Log.d("####", "#### got address onAddressReceived $address")
                app?.iconUrl?.let {
                    view?.startAmountChooserActivity(it, repository.getCurrentBalance(), AmountChooserRequestCode)
                }
            }
        })
    }

    override fun onRequestReceiverPublicAddress() {
        repository.clearReceiverAppPublicAddress()
        app?.launchActivity?.let { activityPath ->
            app?.identifier?.let { receiverPkg ->
                val started = transferManager.startTransferRequestActivity(receiverPkg, activityPath)
                if (!started) {
                    view?.onRequestReceiverPublicAddressError(RequestReceiverPublicAddressError.NotStarted)
                } else {
                    view?.onStartRequestReceiverPublicAddress()
                }
            } ?: kotlin.run {
                view?.onRequestReceiverPublicAddressError(RequestReceiverPublicAddressError.NoPathInfo)
            }
        } ?: kotlin.run {
            view?.onRequestReceiverPublicAddressError(RequestReceiverPublicAddressError.NoPathInfo)
        }
    }

    override fun onAttach(view: IAppInfoView) {
        super.onAttach(view)
        app = repository.getAppByName(appName)
        view.initViews(app)
    }

}