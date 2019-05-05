package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import android.content.Intent
import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppInfoView
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.model.launchActivity
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository
import org.kinecosystem.appsdiscovery.sender.transfer.TransferManager

class AppInfoPresenter(private val appName: String?, private val repository: DiscoveryAppsRepository, private val transferManager: TransferManager) : BasePresenter<IAppInfoView>(), IAppInfoPresenter {

    private val AmountChooserRequestCode = 100;

    private var app: EcosystemApp? = null

    enum class ReqeustReceiverPublicAddressError {
        NotStarted,
        NoPathInfo,
        BadDataReceived
    }

    override fun processResponse(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == AmountChooserRequestCode) {
//            processResponse(requestCode, resultCode, intent)
        } else {
            parsePublicAddressData(requestCode, resultCode, intent)
        }
    }

    private fun parsePublicAddressData(requestCode: Int, resultCode: Int, intent: Intent?) {
        transferManager.processResponse(requestCode, resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                view?.onRequestReceiverPublicAddressCanceled()
            }

            override fun onError(error: String) {
                view?.onRequestReceiverPublicAddressError(ReqeustReceiverPublicAddressError.BadDataReceived)

            }

            override fun onAddressReceived(address: String) {
                repository.storeReceiverAppPublicAddress(address)
                view?.onStartAmountChooser()
            }
        })
    }

    override fun onRequestReceiverPublicAddress() {
        repository.clearReceiverAppPublicAddress()
        app?.launchActivity?.let { activityPath ->
            app?.identifier?.let { receiverPkg ->
                val started = transferManager.startTransferRequestActivity(receiverPkg, activityPath)
                if (!started) {
                    view?.onRequestReceiverPublicAddressError(ReqeustReceiverPublicAddressError.NotStarted)
                } else {
                    view?.onStartRequestReceiverPublicAddress()
                }
            } ?: kotlin.run {
                view?.onRequestReceiverPublicAddressError(ReqeustReceiverPublicAddressError.NoPathInfo)
            }
        } ?: kotlin.run {
            view?.onRequestReceiverPublicAddressError(ReqeustReceiverPublicAddressError.NoPathInfo)
        }
    }

    override fun onSendKinClicked() {
    }

    override fun onAttach(view: IAppInfoView) {
        super.onAttach(view)
        app = repository.getAppByName(appName)
        view.initViews(app)
    }

    override fun onDetach() {
        super.onDetach()
    }
}