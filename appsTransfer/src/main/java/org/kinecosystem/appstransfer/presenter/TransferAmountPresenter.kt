package org.kinecosystem.appstransfer.presenter

import android.content.Intent
import android.util.Log
import org.kinecosystem.appstransfer.view.IAppsTransferView
import org.kinecosystem.appstransfer.view.ITransferAmountView
import org.kinecosystem.appstransfer.view.customview.AppsTransferList
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.transfer.model.*
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.manager.TransferManager

class TransferAmountPresenter(private val repository: EcosystemAppsRepository, private val transferManager: TransferManager) : BasePresenter<ITransferAmountView>(), ITransferAmountPresenter {

    private val REMOTE_PUBLIC_ADDRESS_REQUEST_CODE = 200
    private var app: EcosystemApp? = null

    override fun onAppClicked(app: EcosystemApp) {
        this.app = app
        if (app.canTransferKin) {
            requestReceiverPublicAddress(app)
        } else {
            view?.navigateToAppStore(app.downloadUrl)
        }
    }

    private fun requestReceiverPublicAddress(app: EcosystemApp) {
        repository.clearReceiverAppPublicAddress()
        app.identifier?.let { pkg ->
            app.launchActivity?.let { activityFullPath ->
                val started = transferManager.startTransferRequestActivity(REMOTE_PUBLIC_ADDRESS_REQUEST_CODE, pkg, activityFullPath)
                if (!started) {
                    view?.onTransferError()
                }
            } ?: kotlin.run { view?.onTransferError() }
        } ?: kotlin.run { view?.onTransferError() }
    }

    override fun processResponse(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REMOTE_PUBLIC_ADDRESS_REQUEST_CODE) {
            parsePublicAddressData(resultCode, intent)
        }
    }

    override fun loading() {
        view?.showLoading()
    }

    override fun loadingComplete() {
        view?.showData()
    }

    override fun loadingFailed() {
        view?.showNoData()
    }

    override fun onResume() {
        view?.invalidateList()
    }

    override fun onCloseClicked() {
        view?.close()
    }

    private fun parsePublicAddressData(resultCode: Int, intent: Intent?) {
        transferManager.processResponse(resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                Log.d("AppInfoPresenter", "Operation cancelled, no public address received")
            }

            override fun onError(error: String) {
                //TODO error cant parse address
                view?.onTransferError()
            }

            override fun onResult(address: String) {
                repository.storeReceiverAppPublicAddress(address)
                Log.d("AppInfoPresenter", "got address onResult $address")
                app?.let {
                    view?.startAmountChooserActivity(it)
                }
            }
        })
    }
}
